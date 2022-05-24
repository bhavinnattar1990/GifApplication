package com.gifapplication.ui.savedgif

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.gifapplication.R
import com.gifapplication.adapter.GifAdapter
import com.gifapplication.databinding.FragmentSavedGifsBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SavedGifFragment : Fragment(R.layout.fragment_saved_gifs) {

    private val viewModel: SavedGifViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSavedGifsBinding.bind(view)
        val gifAdapter = GifAdapter(viewModel)
        binding.apply {
            binding.rvSavedGifs.apply {
                adapter = gifAdapter
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(requireContext(), 2)
            }

            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val gif = gifAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onDeleteGif(gif)
                }
            }).attachToRecyclerView(binding.rvSavedGifs)
        }

        viewModel.getAllGifs().observe(viewLifecycleOwner) {
            gifAdapter.submitList(it)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.savedGifEvent.collect { event ->
                when (event) {
                    is SavedGifViewModel.SavedGifEvent.ShowSavedMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }
}