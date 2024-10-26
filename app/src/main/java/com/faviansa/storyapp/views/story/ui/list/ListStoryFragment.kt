package com.faviansa.storyapp.views.story.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.faviansa.storyapp.R
import com.faviansa.storyapp.data.Result
import com.faviansa.storyapp.data.preferences.StoryAppPreferences
import com.faviansa.storyapp.data.preferences.dataStore
import com.faviansa.storyapp.databinding.FragmentListStoryBinding
import com.faviansa.storyapp.utils.displayToast
import com.faviansa.storyapp.views.story.adapter.ListStoryAdapter
import com.faviansa.storyapp.views.story.ui.StoryViewModel
import com.faviansa.storyapp.views.story.ui.StoryViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


class ListStoryFragment : Fragment() {
    private var _binding: FragmentListStoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var listStoryAdapter: ListStoryAdapter
    private lateinit var rvStory: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var preferences: StoryAppPreferences
    private lateinit var userName: String
    private val storyViewModel: StoryViewModel by viewModels {
        StoryViewModelFactory.getInstance(requireActivity())
    }

    private var currentPage = 1
    private val pageSize = 10
    private var isLoading = false
    private var isLastPage = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentListStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferences = StoryAppPreferences.getInstance(requireActivity().dataStore)

        setupView()
        setupAction()
        setupRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        resetList()
        fetchStories()
    }

    private fun setupView() {
        userName = runBlocking { preferences.getName().first() }
        (activity as AppCompatActivity).supportActionBar?.title =
            getString(R.string.welcome) + " " + userName

        rvStory = binding.rvStory
        swipeRefreshLayout = binding.swipeRefresh

    }

    private fun setupAction() {

    }

    private fun setupRecyclerView() {
        listStoryAdapter = ListStoryAdapter()
        val layoutManager = GridLayoutManager(requireContext(), 1)
        rvStory.layoutManager = layoutManager
        rvStory.setHasFixedSize(true)
        rvStory.adapter = listStoryAdapter

        rvStory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= pageSize) {
                        loadMoreItems()
                    }
                }
            }
        })

        fetchStories()

        swipeRefreshLayout.setOnRefreshListener {
            resetList()
            fetchStories()
        }

        storyViewModel.stories.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    isLoading = true
                    if (currentPage == 1) {
                        swipeRefreshLayout.isRefreshing = true
                    } else {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                }

                is Result.Success -> {
                    isLoading = false
                    swipeRefreshLayout.isRefreshing = false
                    binding.progressBar.visibility = View.GONE

                    val stories = result.data
                    if (stories.isEmpty()) {
                        isLastPage = true
                        if (currentPage == 1) {
                            binding.emptyView.visibility = View.VISIBLE
                        }
                    } else {
                        binding.emptyView.visibility = View.GONE
                        if (currentPage == 1) {
                            listStoryAdapter.setStoryList(stories)
                        } else {
                            listStoryAdapter.addStoryList(stories)
                        }
                    }
                }

                is Result.Error -> {
                    isLoading = false
                    swipeRefreshLayout.isRefreshing = false
                    binding.progressBar.visibility = View.GONE
                    displayToast(requireActivity(), result.error)
                }
            }
        }
    }

    private fun loadMoreItems() {
        currentPage++
        fetchStories()
    }

    private fun resetList() {
        currentPage = 1
        isLastPage = false
        listStoryAdapter.clearList()
    }

    private fun fetchStories() {
        storyViewModel.getAllStories(currentPage, pageSize, 0)
    }
}