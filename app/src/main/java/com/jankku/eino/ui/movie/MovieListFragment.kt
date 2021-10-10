package com.jankku.eino.ui.movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.jankku.eino.databinding.FragmentMovieListBinding
import com.jankku.eino.ui.BindingFragment
import com.jankku.eino.util.Result
import com.jankku.eino.util.showBottomNav
import com.jankku.eino.util.showSnackBar
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MovieListFragment"

@AndroidEntryPoint
class MovieListFragment : BindingFragment<FragmentMovieListBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentMovieListBinding::inflate
    private val viewModel: MovieListViewModel by viewModels()
    private var _adapter: MovieListAdapter? = null
    private val adapter get() = _adapter!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showBottomNav(requireActivity())
        setupObservers()
        setupRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _adapter = null
    }

    private fun setupRecyclerView() {
        _adapter = MovieListAdapter { movie ->
            val action =
                MovieListFragmentDirections.actionMovieListFragmentToMovieDetailFragment(movie)
            findNavController().navigate(action)
        }

        adapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        binding.rvMovieList.let {
            it.setHasFixedSize(true)
            it.adapter = adapter
        }
    }

    private fun setupObservers() {
        viewModel.movieListResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.submitList(response.data?.results)
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showSnackBar(binding.root, response.message.toString())
                }
            }
        }
    }
}