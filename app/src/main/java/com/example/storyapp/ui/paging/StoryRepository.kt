package com.example.storyapp.ui.paging

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapp.UserPreference
import com.example.storyapp.data.remote.ApiService
import com.example.storyapp.data.response.StoryListResponse

class StoryRepository(private val apiService: ApiService, private val pref: UserPreference) {
    fun getStories(): LiveData<PagingData<StoryListResponse>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, pref)
            }
        ).liveData
    }
}