package com.jankku.eino.ui.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.jankku.eino.R
import com.jankku.eino.databinding.FragmentBookSearchBinding
import com.jankku.eino.ui.common.BindingFragment
import com.jankku.eino.ui.common.MarginItemDecoration
import com.jankku.eino.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "BookSearchFragment"

@AndroidEntryPoint
class BookSearchFragment : BindingFragment<FragmentBookSearchBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentBookSearchBinding::inflate
    private val viewModel: BookSearchViewModel by viewModels()
    private var _adapter: BookSearchAdapter? = null
    private val adapter get() = _adapter!!
    private var keyboardShownOnce = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNav(requireActivity())
        setupObservers()
        setupRecyclerView()
        setupSearch()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _adapter = null
    }

    private fun setupSearch() {

        // Show keyboard only on initial load
        if (!keyboardShownOnce) {
            binding.tietSearch.showKeyboard()
            keyboardShownOnce = true
        }

        binding.tietSearch.addTextChangedListener(DebounceTextWatcher {
            if (it?.isNotBlank() == true) {
                viewModel.search(it.toString())
            }
        })
    }

    private fun setupRecyclerView() {
        _adapter = BookSearchAdapter { bookId ->
            findNavController().navigateSafe(
                BookSearchFragmentDirections.actionBookSearchFragmentToBookDetailFragment(
                    bookId
                )
            )
        }

        binding.rvSearch.let {
            it.setHasFixedSize(true)
            it.addItemDecoration(
                MarginItemDecoration(
                    resources.configuration.orientation,
                    resources.getInteger(R.integer.rv_list_columns),
                    resources.getDimension(R.dimen.spacing_default)
                )
            )
            it.adapter = adapter
        }
    }

    private fun setupObservers() {
        viewModel.searchResults.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    if (response.data?.results?.isNotEmpty() == true) {
                        binding.layoutNoItems.root.visibility = View.GONE
                        binding.rvSearch.visibility = View.VISIBLE
                        adapter.submitList(response.data.results)
                    } else {
                        binding.layoutNoItems.root.visibility = View.VISIBLE
                        binding.rvSearch.visibility = View.GONE
                    }
                }
                is Result.Error -> {
                    binding.layoutNoItems.root.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    binding.rvSearch.visibility = View.GONE
                    viewModel.sendEvent(Event.SearchErrorEvent(response.message.toString()))
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.eventChannel
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { event ->
                    when (event) {
                        is Event.SearchErrorEvent -> showSnackBar(binding.root, event.message)
                        else -> {}
                    }
                }
        }
    }
}