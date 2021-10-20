package com.jankku.eino.ui.book

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jankku.eino.data.model.DetailItem
import com.jankku.eino.databinding.ItemDetailBinding

class BookDetailAdapter : ListAdapter<DetailItem, BookDetailAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            tvTitle.text = if (item.text.isNotEmpty()) item.text else "-"
            tvText.text = if (item.message.isNotEmpty()) item.message else "-"
        }
    }

    class ViewHolder(val binding: ItemDetailBinding) : RecyclerView.ViewHolder(binding.root) {}

    companion object {
        object DiffCallback : DiffUtil.ItemCallback<DetailItem>() {
            override fun areItemsTheSame(oldItem: DetailItem, newItem: DetailItem) =
                oldItem.text == newItem.text

            override fun areContentsTheSame(oldItem: DetailItem, newItem: DetailItem) =
                oldItem == newItem
        }
    }
}