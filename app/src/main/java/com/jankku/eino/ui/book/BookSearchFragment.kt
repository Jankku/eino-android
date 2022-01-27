package com.jankku.eino.ui.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNav(requireActivity())
        setupObservers()
        setupRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _adapter = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        setupSearch(menu.findItem(R.id.action_search).actionView as SearchView)
    }

    private fun setupSearch(searchView: SearchView) {
        searchView.queryHint = getString(R.string.search_book_hint)

        // Show keyboard only on initial load
        if (!keyboardShownOnce) {
            searchView.isIconified = false
            searchView.showKeyboard()
            keyboardShownOnce = true
        }

        searchView.setOnQueryTextListener(DebounceTextWatcher {
            if (it?.isNotBlank() == true) {
                viewModel.search(it)
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
            requireView().hideKeyboard()
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
                    binding.progressBar.show()
                }
                is Result.Success -> {
                    binding.progressBar.hide()
                    if (response.data?.results?.isNotEmpty() == true) {
                        binding.layoutNoItems.root.hide()
                        binding.rvSearch.show()
                        adapter.submitList(response.data.results)
                    } else {
                        binding.layoutNoItems.root.show()
                        binding.rvSearch.hide()
                    }
                }
                is Result.Error -> {
                    binding.layoutNoItems.root.show()
                    binding.progressBar.hide()
                    binding.rvSearch.hide()
                    viewModel.sendEvent(Event.SearchErrorEvent(response.message.toString()))
                }
                else -> {}
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