package com.jankku.eino.ui.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.jankku.eino.databinding.FragmentBookDetailBinding
import com.jankku.eino.ui.BindingFragment
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "BookDetailFragment"

@AndroidEntryPoint
class BookDetailFragment : BindingFragment<FragmentBookDetailBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentBookDetailBinding::inflate
    private val args: BookDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
    }

    private fun setData() {
        with(binding) {
            with(args.book) {
                tvBookISBN.text = isbn
                tvBookTitle.text = title
                tvBookAuthor.text = author
                tvBookPublisher.text = publisher
                tvBookPages.text = pages.toString()
                tvBookScore.text = score.toString()
                tvBookStartDate.text = start_date
                tvBookEndDate.text = end_date
                tvBookYear.text = year.toString()
                tvBookStatus.text = status
            }
        }
    }
}