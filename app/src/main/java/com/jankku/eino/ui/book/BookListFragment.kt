package com.jankku.eino.ui.book

import android.os.Bundle
import android.util.Log
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
import com.jankku.eino.ui.MainActivity
import com.jankku.eino.ui.common.BindingFragment
import com.jankku.eino.ui.common.MarginItemDecoration
import com.jankku.eino.util.*
import dagger.hilt.android.AndroidEntryPoint
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
        showNavRail(requireActivity())
        showBottomNav(requireActivity())
        setupObservers()
        setupRecyclerView()
        setupSwipeToRefresh()
    }

    override fun onStart() {
        super.onStart()
        viewModel.getBooksByStatus()
        setupAddFab()
    }

    override fun onResume() {
        super.onResume()
        setupAddFab()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _adapter = null
    }

    private fun setupRecyclerView() {
        _adapter = BookListAdapter(requireContext()) { bookId ->
            Log.d(TAG, bookId)
            findNavController().navigateSafe(
                BookListFragmentDirections.actionBookListFragmentToBookDetailFragment(
                    bookId
                )
            )
        }

        adapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

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

    private fun setupAddFab() {
        when (requireContext().isTablet()) {
            true -> {
                (requireActivity() as MainActivity).setupNavRail(R.layout.layout_nav_rail_header_list) { fab ->
                    fab.setOnClickListener {
                        findNavController().navigateSafe(R.id.action_bookListFragment_to_addBookDialogFragment)
                    }
                }
            }
            false -> {
                binding.fabAddItem.apply {
                    show()
                    setOnClickListener {
                        findNavController().navigateSafe(R.id.action_bookListFragment_to_addBookDialogFragment)
                    }
                }
            }
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
                    binding.progressBar.show()
                }
                is Result.Success -> {
                    binding.progressBar.hide()
                    if (response.data?.results?.isNotEmpty() == true) {
                        binding.layoutNoItems.root.hide()
                        binding.rvList.show()
                    } else {
                        binding.layoutNoItems.root.show()
                        binding.rvList.hide()
                    }
                }
                is Result.Error -> {
                    binding.progressBar.hide()
                    binding.rvList.hide()
                    binding.layoutNoItems.root.show()
                    viewModel.sendEvent(Event.GetItemListError(response.message.toString()))
                }
                else -> {}
            }
        }

        viewModel.bookList.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            (requireActivity() as? MainActivity)?.setCustomTitle(
                getString(
                    R.string.appbar_books_title,
                    list.size
                )
            )
        }

        lifecycleScope.launch {
            viewModel.eventChannel
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { event ->
                    when (event) {
                        is Event.GetItemListError -> requireContext().showToast(event.message)
                        is Event.AddItemSuccess -> requireContext().showToast(event.message)
                        is Event.AddItemError -> requireContext().showToast(event.message)
                        else -> {}
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