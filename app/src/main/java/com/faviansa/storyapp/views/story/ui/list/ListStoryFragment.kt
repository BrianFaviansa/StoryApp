package com.faviansa.storyapp.views.story.ui.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.faviansa.storyapp.data.Result
import com.faviansa.storyapp.databinding.FragmentListStoryBinding
import com.faviansa.storyapp.utils.displayToast
import com.faviansa.storyapp.views.story.StoryViewModelFactory
import com.faviansa.storyapp.views.story.adapter.ListStoryAdapter
import com.faviansa.storyapp.views.story.ui.StoryViewModel


class ListStoryFragment : Fragment() {
    private var _binding: FragmentListStoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var listStoryAdapter: ListStoryAdapter
    private lateinit var rvStory: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val storyViewModel: StoryViewModel by viewModels {
        StoryViewModelFactory.getInstance(requireActivity())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentListStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupAction()
        setupRecyclerView()
    }

    private fun setupView() {
        rvStory = binding.rvStory
        swipeRefreshLayout = binding.swipeRefresh

    }

    private fun setupAction() {

    }

    private fun setupRecyclerView() {
        listStoryAdapter = ListStoryAdapter()
        rvStory.layoutManager = GridLayoutManager(requireContext(), 1)
        rvStory.setHasFixedSize(true)
        rvStory.adapter = listStoryAdapter

        fetchStories()

        storyViewModel.stories.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    swipeRefreshLayout.isRefreshing = true
                }

                is Result.Success -> {
                    swipeRefreshLayout.isRefreshing = false
                    listStoryAdapter.setStoryList(result.data)
                }

                is Result.Error -> {
                    swipeRefreshLayout.isRefreshing = false
                    displayToast(requireActivity(), result.error)
                    Log.e("Fetch Stories", result.error)
                }
            }
        }
    }

    private fun fetchStories() {
        storyViewModel.getAllStories()
    }
}