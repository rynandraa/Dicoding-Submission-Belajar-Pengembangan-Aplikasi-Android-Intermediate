package com.example.storyapp.ui.login

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
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.ui.register.RegisterActivity
import com.example.storyapp.ui.story.MainActivity


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "Setting")
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var user: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()
        playAnimation()
        clickButton()
        setupViewModel()
        setupInput()



        binding.seePassword.setOnClickListener {
            if (binding.seePassword.isChecked) {
                binding.inputPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                binding.inputPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }



        binding.textRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupInput(){

        val loginTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val isEmailEmpty = binding.inputEmail.text.toString().trim().isEmpty()
                val isPasswordEmpty = binding.inputPassword.text.toString().trim().isEmpty()
                val isPasswordValid = binding.inputPassword.text.toString().length >= 8
                binding.loginButton.isEnabled = !isEmailEmpty && !isPasswordEmpty && isPasswordValid
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }

        binding.inputEmail.addTextChangedListener(loginTextWatcher)
        binding.inputPassword.addTextChangedListener(loginTextWatcher)
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]

        loginViewModel.getUser().observe(this) { user ->
            this.user = user
        }

        loginViewModel.message.observe(this) {
            chekDatavalid(it, loginViewModel.isError)
        }

        loginViewModel.isLoading.observe(this) {
            isLoading(it)
        }


    }

    private fun clickButton() {
        binding.loginButton.setOnClickListener {
            binding.apply {
                inputEmail.clearFocus()
                inputPassword.clearFocus()
            }

            if (isDataValid()) {
                loginViewModel.login()
                uploadData()
            }
            else{
                erorDialog()
            }


        }
    }

    private fun isDataValid(): Boolean {
        return binding.inputEmail.isEmailValid &&
                binding.inputPassword.isPassValid

    }


    private fun uploadData() {
        binding.apply {
            loginViewModel.PostLoginData(
                inputEmail.text.toString(),
                inputPassword.text.toString()
            )
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

    private fun chekDatavalid(msg: String, isError: Boolean) {
        if (!isError) {
            Toast.makeText(this,"Berhasil Login", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        } else {
            when (msg) {
                "Unauthorized" -> {
                    invalidData()
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

    private fun invalidData(){
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage(R.string.login_failed)

        alertDialogBuilder.setPositiveButton(R.string.OK) { dialog, which ->
            dialog.dismiss()
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
        val pleaseLogin = ObjectAnimator.ofFloat(binding.pleaselogin, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.inputEmail, View.ALPHA, 1f).setDuration(500)
        val pass = ObjectAnimator.ofFloat(binding.inputPassword, View.ALPHA, 1f).setDuration(500)
        val button = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)
        val register = ObjectAnimator.ofFloat(binding.textRegister, View.ALPHA, 1f).setDuration(500)
        val checkbox = ObjectAnimator.ofFloat(binding.seePassword, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(welcome, pleaseLogin)
        }

        AnimatorSet().apply {
            playSequentially(photo, together,email,pass,checkbox,button,register)
            start()
        }
    }


}