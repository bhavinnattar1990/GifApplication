package com.gifapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gifapplication.data.model.Gif
import com.gifapplication.databinding.ItemGifBinding
import com.gifapplication.ui.trendinggif.TrendingGifViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GifAdapter(val viewModel: ViewModel): ListAdapter<Gif, GifAdapter.GifViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifViewHolder {
        val binding = ItemGifBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return GifViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GifViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class GifViewHolder(private val binding: ItemGifBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                ivSave.setOnClickListener {
                    val position = adapterPosition
                    if(position != RecyclerView.NO_POSITION){
                        val gif = getItem(position)
                        if(viewModel is TrendingGifViewModel) {
                            viewModel.saveGif(gif)
                        }
                        ivSave.visibility = View.GONE
                    }
                }
            }
        }

        fun bind(gif: Gif){
            binding.apply {
                Glide.with(itemView)
                    .asGif()
                    .load(gif.images?.original?.url)
                    .into(ivGif)
                tvTitle.text = gif.title
                if(viewModel is TrendingGifViewModel) {
                    CoroutineScope(Dispatchers.IO).launch {
                        if (viewModel.gifRepository.isGifAvailable(gif) == 1) {
                            CoroutineScope(Dispatchers.Main).launch {
                                ivSave.visibility = View.GONE
                            }
                        } else {
                            CoroutineScope(Dispatchers.Main).launch {
                                ivSave.visibility = View.VISIBLE
                            }
                        }
                    }
                }else {
                    ivSave.visibility = View.GONE
                }
            }
        }
    }
    class DiffCallback : DiffUtil.ItemCallback<Gif>(){
        override fun areItemsTheSame(oldItem: Gif, newItem: Gif): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Gif, newItem: Gif): Boolean {
            return oldItem == newItem
        }

    }
}