package com.example.storyapp

import com.example.storyapp.data.response.StoryListResponse

object DataDummy {
    fun generateDummyQuoteResponse(): List<StoryListResponse> {
        val newsList = ArrayList<StoryListResponse>()
        for (i in 0..10) {
            val news = StoryListResponse(
                "32434fer",
                "Title $i",
                "2022-02-22T22:22:22Z",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                2.342345345,
                "erwercsdfsdgs",
                3.45446546456
            )
            newsList.add(news)
        }
        return newsList
    }
}