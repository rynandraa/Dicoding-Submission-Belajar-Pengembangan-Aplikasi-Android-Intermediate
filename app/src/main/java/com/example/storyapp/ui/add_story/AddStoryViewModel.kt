package com.example.storyapp.ui.add_story

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp.UserPreference
import com.example.storyapp.UserToken
import com.example.storyapp.data.remote.ApiConfig
import com.example.storyapp.data.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddStoryViewModel (private val pref: UserPreference) : ViewModel(){
    val result = MutableLiveData<Result<RegisterResponse>>()
    var isError: Boolean = false

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getUserData(): LiveData<UserToken> {
        return pref.getUserToken().asLiveData()
    }

    fun postStory(token : String, imageMultipart: MultipartBody.Part, desc: RequestBody): LiveData<Result<RegisterResponse>> {
        _isLoading.value = true
        val service = ApiConfig.getApiService().postStories("Bearer $token", imageMultipart, desc)
        service.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        Log.d(ContentValues.TAG, responseBody.message)
                        isError = false
                        _message.value = responseBody?.message.toString()
                    }
                } else {
                    isError = true
                    _message.value = response.message()
                    Log.e(ContentValues.TAG, "onResponse: ${response.message()} / deskripsi ga ada")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(ContentValues.TAG, "onFailure: ${t.message.toString()}")
            }
        })

        return result
    }
}