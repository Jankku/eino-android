package com.jankku.eino.ui.movie

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jankku.eino.data.model.Movie
import com.jankku.eino.databinding.ItemListBinding

class MovieListAdapter(private val context: Context, private val clickListener: (String) -> Unit) :
    ListAdapter<Movie, MovieListAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding) { position ->
            val item = getItem(position)
            clickListener(item.movie_id)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            tvFirst.text = item.title.ifEmpty { "-" }
            tvSecond.text = item.director.ifEmpty { "-" }
            chipScore.text = item.score.toString()
            chipStatus.text = item.status.replaceFirstChar { it.uppercaseChar() }
            Glide.with(context)
                .load(item.image_url)
                .into(ivImage)
        }
    }

    class ViewHolder(
        val binding: ItemListBinding,
        clickAtPosition: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                if (bindingAdapterPosition != -1) {
                    clickAtPosition(bindingAdapterPosition)
                }
            }
        }
    }

    companion object {
        object DiffCallback : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie) =
                oldItem.movie_id == newItem.movie_id

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie) =
                oldItem == newItem
        }
    }
}