package com.faviansa.storyapp.views.story.ui.detail

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.faviansa.storyapp.data.Result
import com.faviansa.storyapp.data.preferences.StoryAppPreferences
import com.faviansa.storyapp.data.preferences.dataStore
import com.faviansa.storyapp.databinding.ActivityDetailStoryBinding
import com.faviansa.storyapp.utils.displayToast
import com.faviansa.storyapp.utils.formatCardDate
import com.faviansa.storyapp.views.story.ui.StoryViewModel
import com.faviansa.storyapp.views.story.ui.StoryViewModelFactory

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    private lateinit var preferences: StoryAppPreferences
    private val storyViewModel: StoryViewModel by viewModels {
        StoryViewModelFactory.getInstance(this)
    }
    private lateinit var storyImage: ImageView
    private lateinit var storyName: TextView
    private lateinit var storyDate: TextView
    private lateinit var storyDescription: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = StoryAppPreferences.getInstance(this.dataStore)

        setupView()
        setupDetailStory()
    }

    private fun setupView() {
        storyImage = binding.detailStoryImage
        storyName = binding.detailStoryName
        storyDate = binding.detailStoryDate
        storyDescription = binding.detailStoryDescription
        progressBar = binding.progressBar
    }

    private fun setupDetailStory() {
        val storyId = intent.getStringExtra("story_id") ?: ""

        storyViewModel.getStoryById(storyId)

        storyViewModel.story.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    val detailResponse = result.data
                    val story = detailResponse.story
                    story?.let {
                        storyName.text = it.name
                        storyDate.text = formatCardDate(it.createdAt.toString())
                        storyDescription.text = it.description

                        Glide.with(this)
                            .load(it.photoUrl)
                            .into(storyImage)
                    }

                }

                is Result.Error -> {
                    showLoading(false)
                    displayToast(this, result.error)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}