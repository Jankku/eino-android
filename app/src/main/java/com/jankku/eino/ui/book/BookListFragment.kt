package com.jankku.eino.ui.book

import android.os.Bundle
import android.view.*
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.jankku.eino.R
import com.jankku.eino.databinding.FragmentItemListBinding
import com.jankku.eino.ui.common.BindingFragment
import com.jankku.eino.ui.common.MarginItemDecoration
import com.jankku.eino.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "BookListFragment"

@AndroidEntryPoint
class BookListFragment : BindingFragment<FragmentItemListBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentItemListBinding::inflate
    private val viewModel: BookViewModel by activityViewModels()
    private var _adapter: BookListAdapter? = null
    private val adapter get() = _adapter!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showBottomNav(requireActivity())
        setupObservers()
        setupAdapter()
        setupRecyclerView()
        setupAddBookFabClickListener()
        setupSwipeToRefresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _adapter = null
    }

    private fun setupAdapter() {
        _adapter = BookListAdapter { bookId ->
            findNavController().navigateSafe(
                BookListFragmentDirections.actionBookListFragmentToBookDetailFragment(
                    bookId
                )
            )
        }

        adapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    private fun setupRecyclerView() {
        binding.rvList.let {
            it.setHasFixedSize(true)
            it.addOnScrollListener(HideFabOnScroll(binding.fabAddItem))
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

    private fun setupAddBookFabClickListener() {
        binding.fabAddItem.setOnClickListener {
            findNavController().navigateSafe(R.id.action_bookListFragment_to_addBookDialogFragment)
        }
    }

    private fun setupSwipeToRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getBooksByStatus()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupObservers() {
        viewModel.bookListState.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    if (response.data?.results?.isNotEmpty() == true) {
                        binding.layoutNoItems.root.visibility = View.GONE
                        binding.rvList.visibility = View.VISIBLE
                    } else {
                        binding.layoutNoItems.root.visibility = View.VISIBLE
                        binding.rvList.visibility = View.GONE
                    }
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvList.visibility = View.GONE
                    binding.layoutNoItems.root.visibility = View.VISIBLE
                    viewModel.sendEvent(Event.GetItemListError(response.message.toString()))
                }
            }
        }

        viewModel.bookList.observe(viewLifecycleOwner) { list ->
            if (list.isNotEmpty()) {
                setupAdapter()
                binding.rvList.swapAdapter(adapter, false)
                adapter.submitList(list)
            }
        }

        lifecycleScope.launch {
            viewModel.eventChannel
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { event ->
                    when (event) {
                        is Event.GetItemListError -> showSnackBar(binding.root, event.message)
                        is Event.AddItemSuccessEvent -> showSnackBar(binding.root, event.message)
                        is Event.AddItemErrorEvent -> showSnackBar(binding.root, event.message)
                        is Event.DeleteItemSuccess -> showSnackBar(binding.root, event.message)
                        is Event.DeleteItemError -> showSnackBar(binding.root, event.message)
                        else -> {
                        }
                    }
                }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_item_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort -> {
                findNavController().navigateSafe(R.id.action_bookListFragment_to_bookSortDialogFragment)
                true
            }
            R.id.action_search -> {
                findNavController().navigateSafe(R.id.action_bookListFragment_to_bookSearchFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}