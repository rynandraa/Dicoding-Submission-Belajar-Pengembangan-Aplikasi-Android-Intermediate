package com.example.storyapp.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.UserModel
import com.example.storyapp.UserPreference
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.ui.login.LoginActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "Setting")
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel
    private var cekmypass : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()

        binding.textRegister.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        setupViewModel()
        setupInput()

        binding.showPassword.setOnClickListener {
            if (binding.showPassword.isChecked) {
                binding.inputPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.confirmPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                binding.inputPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.confirmPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
        playAnimation()
        clickButton()
    }
    private fun setupInput(){val loginTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            val isNameEmpty = binding.inputName.text.toString().trim().isEmpty()
            val isEmailEmpty = binding.inputEmail.text.toString().trim().isEmpty()
            val isPasswordEmpty = binding.inputPassword.text.toString().trim().isEmpty()
            val isPasswordValid = binding.inputPassword.text.toString().length >= 8
            val isConfirmEmpty = binding.confirmPassword.text.toString().trim().isEmpty()
            val isPasswordMatching = binding.inputPassword.text.toString() == binding.confirmPassword.text.toString()
            binding.registerButton.isEnabled = !isNameEmpty && !isEmailEmpty && !isPasswordEmpty && isPasswordValid && !isConfirmEmpty && isPasswordMatching
        }

        override fun afterTextChanged(s: Editable?) {

        }
    }

        binding.inputName.addTextChangedListener(loginTextWatcher)
        binding.inputEmail.addTextChangedListener(loginTextWatcher)
        binding.inputPassword.addTextChangedListener(loginTextWatcher)
        binding.confirmPassword.addTextChangedListener(loginTextWatcher)

    }

    private fun setupViewModel(){
        registerViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[RegisterViewModel::class.java]

        registerViewModel.message.observe(this) {
            chekEmailReady(it, registerViewModel.isError)
        }

        registerViewModel.isLoading.observe(this, {
            isLoading(it)
        })
    }


    private fun clickButton() {
        val name = binding.inputName.text.toString()
        val email = binding.inputEmail.text.toString()
        val password = binding.inputPassword.text.toString()

        binding.inputPassword.setOnFocusChangeListener { v, focused ->
            if (v != null) {
                if (!focused) {
                    checkPass()
                }
            }
        }
        binding.confirmPassword.setOnFocusChangeListener { v, focused ->
            if (v != null) {
                if (!focused) {
                    checkPass()
                }
            }
        }

        binding.registerButton.setOnClickListener {
            binding.apply {
                inputName.clearFocus()
                inputEmail.clearFocus()
                inputPassword.clearFocus()
                confirmPassword.clearFocus()

            }

            if (isDataValid()) {
                registerViewModel.saveUser(UserModel(name, email, password, false))
                uploadData()
            }
            else{
                erorDialog()
            }


        }
    }

    private fun isDataValid(): Boolean {
        return binding.inputName.isNameValid &&
                binding.inputEmail.isEmailValid &&
                binding.inputPassword.isPassValid &&
                cekmypass

    }


    private fun uploadData() {
        binding.apply {
            registerViewModel.postDataRegister(
                inputName.text.toString(),
                inputEmail.text.toString(),
                inputPassword.text.toString()
            )
        }

    }

    private fun checkPass() {
        if (binding.inputPassword.text.toString().trim() != binding.confirmPassword.text.toString().trim()) {
            binding.confirmPassword.error = resources.getString(R.string.missmatch_password)
            cekmypass = false
        } else {
            binding.confirmPassword.error = null
            cekmypass = true
        }
    }



    private fun chekEmailReady(msg: String, isError: Boolean) {
        if (!isError) {
            createdDialog()

        } else {
            when (msg) {
                "Bad Request" -> {
                    emailExist()
                    binding.inputEmail.apply {
                        setText("")
                        requestFocus()
                    }
                }
                "timeout" -> {
                    Toast.makeText(this, getString(R.string.timeout), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "${getString(R.string.error_message)} $msg", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }




    private fun erorDialog(){
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage(R.string.input_true)

        alertDialogBuilder.setPositiveButton(R.string.OK) { dialog, which ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }


    private fun emailExist(){
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage(R.string.is_email_used)

        alertDialogBuilder.setPositiveButton(R.string.OK) { dialog, which ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun createdDialog(){
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage(R.string.register_success)

        alertDialogBuilder.setPositiveButton(R.string.OK) { dialog, which ->
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }


    private fun isLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun playAnimation(){
        val photo = ObjectAnimator.ofFloat(binding.imgLogin, View.ALPHA, 1f).setDuration(1000)
        val welcome = ObjectAnimator.ofFloat(binding.welcome, View.ALPHA, 1f).setDuration(500)
        val name = ObjectAnimator.ofFloat(binding.inputName, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.inputEmail, View.ALPHA, 1f).setDuration(500)
        val pass = ObjectAnimator.ofFloat(binding.inputPassword, View.ALPHA, 1f).setDuration(500)
        val confpass = ObjectAnimator.ofFloat(binding.confirmPassword, View.ALPHA, 1f).setDuration(500)
        val button = ObjectAnimator.ofFloat(binding.registerButton, View.ALPHA, 1f).setDuration(500)
        val register = ObjectAnimator.ofFloat(binding.textRegister, View.ALPHA, 1f).setDuration(500)
        val checkbox = ObjectAnimator.ofFloat(binding.showPassword, View.ALPHA, 1f).setDuration(500)


        AnimatorSet().apply {
            playSequentially(photo, welcome,name,email,pass,confpass,checkbox,button,register)
            start()
        }
    }
}