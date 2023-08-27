package com.example.storyapp.ui.login

import android.util.Log
import androidx.lifecycle.*
import com.example.storyapp.UserModel
import com.example.storyapp.UserPreference
import com.example.storyapp.UserToken
import com.example.storyapp.data.remote.ApiConfig
import com.example.storyapp.data.response.LoginResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel (private val pref: UserPreference) : ViewModel(){
    private val _login = MutableLiveData<LoginResponse>()

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    var isError: Boolean = false

    fun LoginData(email:String, password:String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().uploadDataLogin(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                val responseBody = response.body()
                if (response.isSuccessful) {
                    if (responseBody!= null && !responseBody.error){
                        val token = responseBody.loginResult.token
                        saveUserData(UserToken(token))
                    }
                    isError = false
                    Log.e("loginResponse", "onResponse: ${response.message()}")
                    _message.value = responseBody?.message.toString()
                    _login.value = response.body()
                } else {
                    isError = true
                    _message.value = response.message()
                    Log.e("regis", "onResponse: ${response.message()} / akun ready")
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e("loginFailure", "onFailure: ${t.message}")
            }
        })
    }
    fun PostLoginData(email: String, password: String) {
        viewModelScope.launch {
            LoginData(email, password)
        }
    }


    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun login() {
        viewModelScope.launch {
            pref.login()
        }
    }

    fun saveUserData(userData: UserToken){
        viewModelScope.launch {
            pref.saveUserToken(userData)
        }
    }
}