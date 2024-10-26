package com.faviansa.storyapp.views.story.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.faviansa.storyapp.data.remote.response.story.ListStoryItem
import com.faviansa.storyapp.databinding.ItemStoryBinding
import com.faviansa.storyapp.utils.formatCardDate
import com.faviansa.storyapp.views.story.ui.detail.DetailStoryActivity

class ListStoryAdapter : RecyclerView.Adapter<ListStoryAdapter.ViewHolder>() {
    private val storiesList = mutableListOf<ListStoryItem>()


    @SuppressLint("NotifyDataSetChanged")
    fun setStoryList(newStories: List<ListStoryItem>) {
        val sortByNewest = newStories.sortedByDescending { it.createdAt }
        storiesList.clear()
        storiesList.addAll(sortByNewest)
        notifyDataSetChanged()
    }

    fun addStoryList(newStories: List<ListStoryItem>) {
        val sortByNewest = newStories.sortedByDescending { it.createdAt }
        val oldSize = storiesList.size
        storiesList.addAll(sortByNewest)
        notifyItemRangeInserted(oldSize, sortByNewest.size)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearList() {
        storiesList.clear()
        notifyDataSetChanged()
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

    inner class ViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(story: ListStoryItem) {
            with(binding) {
                storyName.text = story.name
                storyDate.text = formatCardDate(story.createdAt.toString())

                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .centerCrop()
                    .into(storyPhoto)

                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                    intent.putExtra("story_id", story.id)
                    itemView.context.startActivity(intent)
                }

            }
        }
    }

}