package com.faviansa.storyapp.views.story.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.faviansa.storyapp.R
import com.faviansa.storyapp.data.preferences.StoryAppPreferences
import com.faviansa.storyapp.data.preferences.dataStore
import com.faviansa.storyapp.databinding.FragmentListStoryBinding
import com.faviansa.storyapp.views.story.adapter.LoadingStateAdapter
import com.faviansa.storyapp.views.story.adapter.StoryListAdapter
import com.faviansa.storyapp.views.story.ui.StoryListViewModel
import com.faviansa.storyapp.views.story.ui.ViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


class ListStoryFragment : Fragment() {
    private var _binding: FragmentListStoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var rvStory: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var preferences: StoryAppPreferences
    private lateinit var userName: String
    private lateinit var listStoryAdapter: StoryListAdapter
    private lateinit var storyListViewModel: StoryListViewModel
    private lateinit var layoutManager: GridLayoutManager


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

        setupViews()
        getData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupViews() {
        userName = runBlocking { preferences.getName().first() }
        (activity as AppCompatActivity).supportActionBar?.title =
            getString(R.string.welcome) + " " + userName

        storyListViewModel = ViewModelProvider(
            this@ListStoryFragment,
            ViewModelFactory(requireContext())
        )[StoryListViewModel::class.java]
        rvStory = binding.rvStory
        swipeRefreshLayout = binding.swipeRefresh
        layoutManager = GridLayoutManager(requireContext(), 1)
        rvStory.layoutManager = layoutManager
        rvStory.setHasFixedSize(true)

        swipeRefreshLayout.setOnRefreshListener {
            getData()
            listStoryAdapter.refresh()
        }
    }

    private fun getData() {
        listStoryAdapter = StoryListAdapter()
        rvStory.adapter = listStoryAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                listStoryAdapter.retry()
            }
        )
        storyListViewModel.stories.observe(viewLifecycleOwner) {
            listStoryAdapter.submitData(lifecycle, it)
        }
    }
}