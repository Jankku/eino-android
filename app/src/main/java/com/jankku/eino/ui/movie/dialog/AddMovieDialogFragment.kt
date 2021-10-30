package com.jankku.eino.ui.movie.dialog

import androidx.core.content.res.ResourcesCompat
import com.jankku.eino.R
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "AddMovieDialogFragment"

@AndroidEntryPoint
class AddMovieDialogFragment : BaseMovieDialogFragment() {
    override fun setupActionButton() {
        binding.btnAction.apply {
            text = getString(R.string.dialog_movie_add_action_text)
            icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_add, null)
            setOnClickListener {
                viewModel.addMovie(getMovieRequest())
                requireDialog().dismiss()
            }
        }
    }

    override fun setupStatusPicker() {
        binding.mactvMovieStatus.setText(statusValueArray[0]) // "Reading" as default value
        super.setupStatusPicker()
    }

    override fun setupDatePickers() {
        binding.mactvMovieStartDate.apply {
            setText(dateFieldItems[0]) // "Today" as default value
        }

        binding.mactvMovieEndDate.apply {
            setText(dateFieldItems[0]) // "Today" as default value
        }

        super.setupDatePickers()
    }
}