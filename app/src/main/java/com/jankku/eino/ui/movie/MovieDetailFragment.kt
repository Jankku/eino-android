package com.jankku.eino.ui.movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.jankku.eino.databinding.FragmentMovieDetailBinding
import com.jankku.eino.ui.common.BindingFragment
import dagger.hilt.android.AndroidEntryPoint


private const val TAG = "MovieDetailFragment"

@AndroidEntryPoint
class MovieDetailFragment : BindingFragment<FragmentMovieDetailBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentMovieDetailBinding::inflate
    private val args: MovieDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
    }

    private fun setData() {
        with(binding) {
            with(args.movie) {
                tvMovieTitle.text = title
                tvMovieStudio.text = studio
                tvMovieDirector.text = director
                tvMovieWriter.text = writer
                tvMovieDuration.text = duration.toString()
                tvMovieScore.text = score.toString()
                tvMovieYear.text = year.toString()
                tvMovieStartDate.text = start_date
                tvMovieEndDate.text = end_date
                tvMovieStatus.text = status
            }
        }
    }
}