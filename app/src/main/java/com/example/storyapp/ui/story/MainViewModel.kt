package com.example.storyapp.ui.story

import androidx.lifecycle.*
import com.example.storyapp.UserModel
import com.example.storyapp.UserPreference
import kotlinx.coroutines.launch


class MainViewModel (private val pref: UserPreference) : ViewModel() {

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }

}