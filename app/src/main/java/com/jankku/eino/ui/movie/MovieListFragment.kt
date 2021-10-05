package com.jankku.eino.ui.movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.viewbinding.ViewBinding
import com.jankku.eino.databinding.FragmentMovieListBinding
import com.jankku.eino.ui.BindingFragment
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MovieListFragment"

@AndroidEntryPoint
class MovieListFragment : BindingFragment<FragmentMovieListBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentMovieListBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}