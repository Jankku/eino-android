package com.jankku.eino.ui.movie.dialog

import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.jankku.eino.R
import com.jankku.eino.util.utcToLocal
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "AddMovieDialogFragment"

@AndroidEntryPoint
class UpdateMovieDialogFragment : BaseMovieDialogFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    override fun setupActionButton() {
        binding.btnAction.apply {
            text = getString(R.string.dialog_movie_update_action_text)
            icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_edit, null)
            setOnClickListener {
                val movie = getMovieRequest()
                viewModel.editMovie(movie)
                requireDialog().dismiss()
            }
        }
    }

    private fun setupObservers() {
        viewModel.movie.observe(viewLifecycleOwner) { movie ->
            with(binding) {
                with(movie) {
                    tietMovieTitle.setText(title)
                    tietMovieStudio.setText(studio)
                    tietMovieDirector.setText(director)
                    tietMovieWriter.setText(writer)
                    tietMovieDuration.setText(duration.toString())
                    tietMovieYear.setText(year.toString())
                    mactvMovieStatus.setText(status.replaceFirstChar { it.uppercase() }, false)
                    sliderMovieScore.value = score.toFloat()
                    mactvMovieStartDate.setText(utcToLocal(start_date), false)
                    mactvMovieEndDate.setText(utcToLocal(end_date), false)
                }
            }
        }
    }
}