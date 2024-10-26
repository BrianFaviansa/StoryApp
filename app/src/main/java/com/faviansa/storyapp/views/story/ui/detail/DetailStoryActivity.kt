package com.faviansa.storyapp.views.story.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.faviansa.storyapp.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}