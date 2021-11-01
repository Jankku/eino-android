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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jankku.eino.R
import com.jankku.eino.data.enums.BookStatus
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
        setupRecyclerView()
        setupAddBookFabClickListener()
        setupSwipeToRefresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _adapter = null
    }

    private fun setupRecyclerView() {
        _adapter = BookListAdapter { bookId ->
            val action =
                BookListFragmentDirections.actionBookListFragmentToBookDetailFragment(bookId)
            findNavController().navigate(action)
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

    private fun setupAddBookFabClickListener() {
        binding.fabAddItem.setOnClickListener {
            findNavController().navigate(R.id.action_bookListFragment_to_addBookDialogFragment)
        }
    }

    private fun setupSwipeToRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getBooksByStatus()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupObservers() {
        viewModel.bookList.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    if (response.data?.results?.isNotEmpty() == true) {
                        binding.layoutNoItems.root.visibility = View.GONE
                        binding.rvList.visibility = View.VISIBLE
                        adapter.submitList(response.data.results)
                    } else {
                        binding.layoutNoItems.root.visibility = View.VISIBLE
                        binding.rvList.visibility = View.GONE
                    }
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showSnackBar(binding.root, response.message.toString())
                }
            }
        }

        lifecycleScope.launch {
            viewModel.eventChannel
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { event ->
                    when (event) {
                        is Event.AddBookSuccessEvent -> showSnackBar(binding.root, event.message)
                        is Event.AddBookErrorEvent -> showSnackBar(binding.root, event.message)
                        is Event.DeleteBookSuccessEvent -> showSnackBar(binding.root, event.message)
                        is Event.DeleteBookErrorEvent -> showSnackBar(binding.root, event.message)
                        else -> {
                        }
                    }
                }
        }
    }

    private fun statusDialog() {
        val checkedItem = viewModel.selectedStatus.value!!.ordinal
        val statusArray = BookStatus.toArray()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.status_dialog_title))
            .setSingleChoiceItems(statusArray, checkedItem) { _, index ->
                val status = BookStatus.values()[index]
                viewModel.setStatus(status)
            }
            .setPositiveButton(resources.getString(R.string.status_dialog_btn_apply)) { _, _ ->
                viewModel.getBooksByStatus()
            }
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_item_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_status -> {
                statusDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}