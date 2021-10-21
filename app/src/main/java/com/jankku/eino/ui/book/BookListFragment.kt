package com.jankku.eino.ui.book

import android.os.Bundle
import android.view.*
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jankku.eino.R
import com.jankku.eino.data.enums.Status
import com.jankku.eino.databinding.FragmentBookListBinding
import com.jankku.eino.ui.common.BindingFragment
import com.jankku.eino.util.Event
import com.jankku.eino.util.Result
import com.jankku.eino.util.showBottomNav
import com.jankku.eino.util.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

private const val TAG = "BookListFragment"

@AndroidEntryPoint
class BookListFragment : BindingFragment<FragmentBookListBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentBookListBinding::inflate
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
        addBookFabClickListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _adapter = null
    }

    private fun setupRecyclerView() {
        _adapter = BookListAdapter { book ->
            val action = BookListFragmentDirections.actionBookListFragmentToBookDetailFragment(book)
            findNavController().navigate(action)
        }

        adapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        binding.rvBookList.let {
            it.setHasFixedSize(true)
            it.adapter = adapter
        }
    }

    private fun addBookFabClickListener() {
        binding.fabAddBook.setOnClickListener {
            val action = BookListFragmentDirections.actionBookListFragmentToAddBookDialogFragment()
            findNavController().navigate(action)
        }
    }

    private fun setupObservers() {
        viewModel.books.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE

                    if (response.data!!.results.isNotEmpty()) {
                        binding.layoutNoItems.root.visibility = View.GONE
                        binding.rvBookList.visibility = View.VISIBLE
                        adapter.submitList(response.data.results)
                    } else {
                        binding.layoutNoItems.root.visibility = View.VISIBLE
                        binding.rvBookList.visibility = View.GONE
                    }
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showSnackBar(binding.root, response.message.toString())
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventChannel.collect { event ->
                when (event) {
                    is Event.AddBookSuccessEvent -> showSnackBar(binding.root, event.message)
                    is Event.AddBookErrorEvent -> showSnackBar(binding.root, event.message)
                    is Event.DeleteBookSuccessEvent -> showSnackBar(binding.root, event.message)
                    is Event.DeleteBookErrorEvent -> showSnackBar(binding.root, event.message)
                }
            }
        }
    }

    private fun statusDialog() {
        val checkedItem = viewModel.selectedStatus.value!!.ordinal
        val statusArray = Status.toArray()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.status_dialog_title))
            .setSingleChoiceItems(statusArray, checkedItem) { _, index ->
                val status = Status.values()[index]
                viewModel.setStatus(status)
            }
            .setPositiveButton(resources.getString(R.string.status_dialog_btn_apply)) { _, _ ->
                val status = viewModel.selectedStatus.value!!
                viewModel.getBooksByStatus(status)
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