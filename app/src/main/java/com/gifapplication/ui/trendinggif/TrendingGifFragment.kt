package com.gifapplication.ui.trendinggif

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gifapplication.R
import com.gifapplication.adapter.GifAdapter
import com.gifapplication.databinding.FragmentTrendingGifsBinding
import com.gifapplication.utils.QUERY_PAGE_SIZE
import com.gifapplication.utils.Resource
import com.gifapplication.utils.SEARCH_TIME_DELAY
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "TrendingGifFragment"
@AndroidEntryPoint
class TrendingGifFragment : Fragment(R.layout.fragment_trending_gifs) {

    private val viewModel: TrendingGifViewModel by viewModels()
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    lateinit var binding : FragmentTrendingGifsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentTrendingGifsBinding.bind(view)
        val gifAdapter = GifAdapter(viewModel)

        binding.apply {
            rvTrendingGif.apply {
                adapter = gifAdapter
                setHasFixedSize(true)
                addOnScrollListener(this@TrendingGifFragment.scrollListener)
            }
        }

        viewModel.trendingGif.observe(viewLifecycleOwner) {
            when(it){
                is Resource.Success -> {
                    binding.paginationProgressBar.visibility = View.INVISIBLE
                    isLoading = false
                    it.data?.let { newsResponse ->
                        gifAdapter.submitList(newsResponse.data.toList())
                        val totalPages = (newsResponse.pagination.total_count?.div(QUERY_PAGE_SIZE + 2) ?: 0)
                        isLastPage = viewModel.trendingGifPage == totalPages
                        if(isLastPage)
                            binding.rvTrendingGif.setPadding(0,0,0,0)
                    }
                }
                is Resource.Error -> {
                    binding.paginationProgressBar.visibility = View.INVISIBLE
                    isLoading = true
                    it.message?.let { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "Error: $message")
                    }
                }
                is Resource.Loading -> {
                    binding.paginationProgressBar.visibility = View.VISIBLE
                }
            }
        }

        var job: Job? = null
        binding.etSearch.addTextChangedListener{ editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_TIME_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.searchGif(editable.toString())
                    } else {
                        viewModel.getTrendingGifs()
                    }
                }
            }
        }

        viewModel.searchGif.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    isLoading = false
                    binding.paginationProgressBar.visibility = View.INVISIBLE
                    it.data?.let { gifResponse ->
                        gifAdapter.submitList(gifResponse.data.toList())
                        val totalPages = (gifResponse?.pagination?.total_count?.div(QUERY_PAGE_SIZE + 2) ?: 0)
                        isLastPage = viewModel.searchGifPage == totalPages
                        if(isLastPage)
                            binding.rvTrendingGif.setPadding(0,0,0,0)
                    }
                }
                is Resource.Error -> {
                    isLoading = true
                    binding.paginationProgressBar.visibility = View.INVISIBLE
                    it.message?.let { message ->
                        Log.e(TAG, "Error: $message")
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                    binding.paginationProgressBar.visibility = View.VISIBLE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.gifEvent.collect { event ->
                when (event) {
                    is TrendingGifViewModel.GifEvent.ShowSavedMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){ //State is scrolling
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val totalVisibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + totalVisibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if(shouldPaginate){
                if(binding.etSearch.text.isEmpty()) {
                    viewModel.getTrendingGifs()
                }else{
                    viewModel.searchGif(binding?.etSearch?.text.toString())
                }
                isScrolling = false
            }
        }
    }
}