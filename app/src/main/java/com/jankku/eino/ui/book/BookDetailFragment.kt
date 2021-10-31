package com.jankku.eino.ui.book

import android.os.Bundle
import android.view.*
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.jankku.eino.R
import com.jankku.eino.databinding.FragmentItemDetailBinding
import com.jankku.eino.ui.common.BindingFragment
import com.jankku.eino.ui.common.DetailAdapter
import com.jankku.eino.util.Event
import com.jankku.eino.util.Result
import com.jankku.eino.util.hideBottomNav
import com.jankku.eino.util.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "BookDetailFragment"

@AndroidEntryPoint
class BookDetailFragment : BindingFragment<FragmentItemDetailBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentItemDetailBinding::inflate
    private val viewModel: BookViewModel by activityViewModels()
    private var _adapter: DetailAdapter? = null
    private val adapter get() = _adapter!!
    private val args: BookDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNav(requireActivity())
        viewModel.setBookId(args.bookId)
        viewModel.getBookById()
        setupRecyclerView()
        setupEditFabClickListener()
        setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _adapter = null
    }

    private fun setupRecyclerView() {
        _adapter = DetailAdapter()

        adapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        binding.rvDetail.let {
            it.setHasFixedSize(true)
            it.adapter = adapter
        }
    }

    private fun setupObservers() {
        viewModel.detailItemList.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.submitList(it.data)
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    viewModel.sendEvent { Event.GetBookErrorEvent(it.message.toString()) }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.eventChannel
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { event ->
                    when (event) {
                        is Event.GetBookErrorEvent -> showSnackBar(binding.root, event.message)
                        else -> {
                        }
                    }
                }
        }
    }

    private fun setupEditFabClickListener() {
        binding.fabEditItem.setOnClickListener {
            findNavController().navigate(R.id.action_bookDetailFragment_to_updateBookDialogFragment)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_item_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                viewModel.deleteBook()
                findNavController().navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}