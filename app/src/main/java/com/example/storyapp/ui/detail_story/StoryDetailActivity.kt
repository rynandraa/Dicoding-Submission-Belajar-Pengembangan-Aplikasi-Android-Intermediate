package com.example.storyapp.ui.detail_story

import android.content.ContentValues
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.UserPreference
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.data.remote.ApiConfig
import com.example.storyapp.data.response.StoryDetailResponse
import com.example.storyapp.data.response.StoryResponse
import com.example.storyapp.databinding.ActivityStoryDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "Setting")
class StoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryDetailBinding
    private lateinit var detailViewModel: StoryDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        setViewModel()
    }
    private fun setViewModel(){
        detailViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[StoryDetailViewModel::class.java]

        detailViewModel.getUserData().observe(this) { userData ->
            val tokken = userData.token
            getDetailListUser(tokken)
        }


    }


    private fun getDetailListUser(token: String){
        val getId = intent.getStringExtra("Data")
        val client = getId?.let {
            ApiConfig.getApiService().getDetailStory("Bearer " + token,
                it
            )
        }
        isLoading(true)
        if (client != null) {
            client.enqueue(object : Callback<StoryDetailResponse> {
                override fun onResponse(call: Call<StoryDetailResponse>, response: Response<StoryDetailResponse>) {
                    isLoading(false)
                    if(response.isSuccessful){
                        val responsBody = response.body()
                        if (responsBody!= null && !responsBody.error){
                            setUserData(responsBody.story)
                        }
                    } else {
                        Toast.makeText(this@StoryDetailActivity,getString(R.string.load_fail) , Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<StoryDetailResponse>, t: Throwable) {
                    isLoading(false)
                    Log.e(ContentValues.TAG, "onFailure: ${t.message.toString()}")
                }
            })
        }
    }

    private fun setUserData(listUser: StoryResponse){
        binding.apply {
            tvname.text = listUser.name
            tvdesc.text = listUser.description
            Glide.with(this@StoryDetailActivity)
                .load(listUser.photoUrl)
                .into(ivDetailPhoto)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun isLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}