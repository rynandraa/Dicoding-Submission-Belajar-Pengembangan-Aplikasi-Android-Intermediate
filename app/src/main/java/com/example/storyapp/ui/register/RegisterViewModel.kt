package com.example.storyapp.ui.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.UserModel
import com.example.storyapp.UserPreference
import com.example.storyapp.data.remote.ApiConfig
import com.example.storyapp.data.response.RegisterResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel (private val pref: UserPreference) : ViewModel() {

    private val _regis = MutableLiveData<RegisterResponse>()

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    var isError: Boolean = false


    fun RegrisData(name: String, email:String, password:String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().uploadRegister(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                _isLoading.value = false
                val responseBody = response.body()
                if (response.isSuccessful) {
                    isError = false
                    Log.e("regisResponse", "onResponse: ${response.message()}")
                    _message.value = responseBody?.message.toString()
                    _regis.value = response.body()
                } else {
                    isError = true
                    _message.value = response.message()
                    Log.e("regis", "onResponse: ${response.message()} / akun ready")
                }
            }
            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Log.e("regisFailure", "onFailure: ${t.message}")
                _isLoading.value = false
            }
        })
    }

    fun postDataRegister(name: String, email: String, password: String) {
        viewModelScope.launch {
            RegrisData(name, email, password)
        }
    }

    fun saveUser(user: UserModel) {
        viewModelScope.launch {
            pref.saveUser(user)
        }
    }
}