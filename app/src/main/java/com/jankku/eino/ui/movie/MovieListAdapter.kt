package com.jankku.eino.ui.movie

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jankku.eino.data.model.Movie
import com.jankku.eino.databinding.ItemListBinding

class MovieListAdapter(private val clickListener: (String) -> Unit) :
    ListAdapter<Movie, MovieListAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding) { position ->
            getItem(position)?.let { clickListener(it.movie_id) }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            tvFirst.text = if (item.title.isNotEmpty()) item.title else "-"
            tvSecond.text = if (item.studio.isNotEmpty()) item.studio else "-"
            tvScore.text = item.score.toString()
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