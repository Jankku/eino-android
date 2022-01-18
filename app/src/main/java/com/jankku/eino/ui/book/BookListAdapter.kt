package com.jankku.eino.ui.book

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jankku.eino.data.model.Book
import com.jankku.eino.databinding.ItemListBinding

class BookListAdapter(private val clickListener: (String) -> Unit) :
    ListAdapter<Book, BookListAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding) { position ->
            val item = getItem(position)
            clickListener(item.book_id)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            tvFirst.text = if (item.title.isNotEmpty()) item.title else "-"
            tvSecond.text = if (item.author.isNotEmpty()) item.author else "-"
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
        object DiffCallback : DiffUtil.ItemCallback<Book>() {
            override fun areItemsTheSame(oldItem: Book, newItem: Book) =
                oldItem.book_id == newItem.book_id

            override fun areContentsTheSame(oldItem: Book, newItem: Book) =
                oldItem == newItem
        }
    }
}