package com.jankku.eino.ui.book

import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.jankku.eino.R
import com.jankku.eino.util.utcToLocal
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "AddBookDialogFragment"

@AndroidEntryPoint
class UpdateBookDialogFragment : BaseBookDialogFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    override fun setupActionButton() {
        binding.btnAction.apply {
            text = getString(R.string.dialog_update_action_text)
            icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_edit, null)
            setOnClickListener {
                val book = getBookRequest()
                viewModel.editBook(book)
                requireDialog().dismiss()
            }
        }
    }

    private fun setupObservers() {
        viewModel.book.observe(viewLifecycleOwner) { book ->
            with(binding) {
                with(book) {
                    tietBookIsbn.setText(isbn)
                    tietBookTitle.setText(title)
                    tietBookAuthor.setText(author)
                    tietBookPublisher.setText(publisher)
                    tietBookPages.setText(pages.toString())
                    tietBookYear.setText(year.toString())
                    mactvBookStatus.setText(status.replaceFirstChar { it.uppercase() }, false)
                    sliderBookScore.value = score.toFloat()
                    mactvBookStartDate.setText(utcToLocal(start_date), false)
                    mactvBookEndDate.setText(utcToLocal(end_date), false)
                }
            }
        }
    }
}