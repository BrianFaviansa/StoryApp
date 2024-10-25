package com.faviansa.storyapp.views.story.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.faviansa.storyapp.data.remote.response.story.ListStoryItem
import com.faviansa.storyapp.databinding.ItemStoryBinding
import com.faviansa.storyapp.utils.formatCardDate

class ListStoryAdapter : RecyclerView.Adapter<ListStoryAdapter.ViewHolder>() {
    private val storiesList = mutableListOf<ListStoryItem>()

    fun setStoryList(storiesList: List<ListStoryItem>) {
        val diffCallback = StoryDiffCallback(storiesList, storiesList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.storiesList.clear()
        this.storiesList.addAll(storiesList)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = storiesList[position]
        holder.bind(story)
    }

    override fun getItemCount(): Int = storiesList.size

    inner class ViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            with(binding) {
                storyName.text = story.name
                storyDate.text = formatCardDate(story.createdAt.toString())

                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .into(storyPhoto)
            }
        }
    }

    private class StoryDiffCallback(
        private val oldStoryList: List<ListStoryItem>,
        private val newStoryList: List<ListStoryItem>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldStoryList.size
        override fun getNewListSize(): Int = newStoryList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldStoryList[oldItemPosition].id == newStoryList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldStoryList[oldItemPosition] == newStoryList[newItemPosition]
        }
    }
}