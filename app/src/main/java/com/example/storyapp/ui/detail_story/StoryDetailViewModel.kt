package com.example.storyapp.ui.detail_story

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp.UserPreference
import com.example.storyapp.UserToken

class StoryDetailViewModel (private val pref: UserPreference) : ViewModel() {

    fun getUserData(): LiveData<UserToken> {
        return pref.getUserToken().asLiveData()
    }
}