package com.jankku.eino.ui.movie

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jankku.eino.data.model.Movie
import com.jankku.eino.databinding.ItemMovieListBinding

class MovieListAdapter(private val clickListener: (String) -> Unit) :
    ListAdapter<Movie, MovieListAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemMovieListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding) { position ->
            getItem(position)?.let { clickListener(it.movie_id) }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            tvMovieTitle.text = if (item.title.isNotEmpty()) item.title else "-"
            tvMovieStudio.text = if (item.studio.isNotEmpty()) item.studio else "-"
            tvMovieScore.text = item.score.toString()
        }
    }

    class ViewHolder(val binding: ItemMovieListBinding, clickAtPosition: (Int) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener { clickAtPosition(absoluteAdapterPosition) }
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