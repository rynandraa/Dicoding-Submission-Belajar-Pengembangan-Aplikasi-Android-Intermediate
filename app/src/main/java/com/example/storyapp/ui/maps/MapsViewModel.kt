package com.example.storyapp.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp.UserPreference
import com.example.storyapp.UserToken

class MapsViewModel (private val pref: UserPreference): ViewModel() {
    fun getUserData(): LiveData<UserToken> {
        return pref.getUserToken().asLiveData()
    }
}