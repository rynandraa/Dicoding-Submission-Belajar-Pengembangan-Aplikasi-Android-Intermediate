package com.example.storyapp.ui.story

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.UserPreference
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.data.remote.ApiConfig
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.ui.add_story.AddStoryActivity
import com.example.storyapp.ui.login.LoginActivity
import com.example.storyapp.ui.maps.MapsActivity
import com.example.storyapp.ui.paging.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "Setting")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    private val mainPagingViewModel: PagingViewModel by viewModels {
        val apiService = ApiConfig.getApiService()
        val pref = UserPreference.getInstance(dataStore)
        ViewModelPagingFactory(StoryRepository(apiService, pref))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addStory.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }

        setupViewModel()
        val layoutManager = LinearLayoutManager(this)
        binding.RvListStory.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.RvListStory.addItemDecoration(itemDecoration)

        getData()
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    private fun getData(){
        val adapter = MainAdapter()

        binding.RvListStory.adapter = adapter

        adapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.Loading -> {
                    isLoading(true)
                }
                is LoadState.Error -> {
                    isLoading(false)
                }
                else -> {
                    isLoading(false)
                }
            }
        }

        binding.RvListStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        mainPagingViewModel.story.observe(this) {
            adapter.submitData(lifecycle, it)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.logout) {
            mainViewModel.logout()
        }
        if (id == R.id.maps) {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
        return true
    }

    private fun setupViewModel() {
        val pref = UserPreference.getInstance(dataStore)
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(pref)
        )[MainViewModel::class.java]

        mainViewModel.getUser().observe(this) { user ->
            if (user.isLogin) {
                getData()
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            getData()
        }

    }

    private fun isLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}