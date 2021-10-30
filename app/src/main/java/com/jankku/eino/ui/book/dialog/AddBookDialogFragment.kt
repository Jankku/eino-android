package com.jankku.eino.ui.book.dialog

import androidx.core.content.res.ResourcesCompat
import com.jankku.eino.R
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "AddBookDialogFragment"

@AndroidEntryPoint
class AddBookDialogFragment : BaseBookDialogFragment() {
    override fun setupActionButton() {
        binding.btnAction.apply {
            text = getString(R.string.dialog_book_add_action_text)
            icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_add, null)
            setOnClickListener {
                viewModel.addBook(getBookRequest())
                requireDialog().dismiss()
            }
        }
    }

    override fun setupStatusPicker() {
        binding.mactvBookStatus.setText(statusValueArray[0]) // "Reading" as default value
        super.setupStatusPicker()
    }

    override fun setupDatePickers() {
        binding.mactvBookStartDate.apply {
            setText(dateFieldItems[0]) // "Today" as default value
        }

        binding.mactvBookEndDate.apply {
            setText(dateFieldItems[0]) // "Today" as default value
        }

        super.setupDatePickers()
    }
}