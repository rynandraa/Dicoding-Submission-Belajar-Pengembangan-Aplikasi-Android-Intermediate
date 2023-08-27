package com.example.storyapp.ui.paging

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.data.response.StoryListResponse
import com.example.storyapp.databinding.StoryItemBinding
import com.example.storyapp.ui.detail_story.StoryDetailActivity

class MainAdapter :
    PagingDataAdapter<StoryListResponse, MainAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
            holder.itemView.setOnClickListener {
                val moveToDetail = Intent(holder.itemView.context, StoryDetailActivity::class.java)
                moveToDetail.putExtra("Data", data.id)
                holder.itemView.context.startActivity(moveToDetail)
            }
        }
    }

    class MyViewHolder(private val binding: StoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: StoryListResponse) {
            binding.tvItem.text = data.name
            Glide.with(itemView.context)
                .load(data.photoUrl)
                .into(binding.photo)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryListResponse>() {
            override fun areItemsTheSame(oldItem: StoryListResponse, newItem: StoryListResponse): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoryListResponse, newItem: StoryListResponse): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}