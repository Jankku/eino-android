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
import com.jankku.eino.ui.MainActivity
import com.jankku.eino.ui.common.BindingFragment
import com.jankku.eino.ui.common.DetailAdapter
import com.jankku.eino.ui.common.MarginItemDecoration
import com.jankku.eino.util.*
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
        setupEditFab()
        setupObservers()
        setupSwipeToRefresh()
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
            it.addItemDecoration(
                MarginItemDecoration(
                    resources.configuration.orientation,
                    resources.getInteger(R.integer.rv_detail_columns),
                    resources.getDimension(R.dimen.spacing_default)
                )
            )
            it.adapter = adapter
        }
    }

    private fun setupSwipeToRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getBookById()
            binding.swipeRefresh.isRefreshing = false
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
                    binding.rvDetail.visibility = View.VISIBLE
                    if (requireContext().isNotTablet()) binding.fabEditItem.visibility =
                        View.VISIBLE
                    binding.layoutNoItem.root.visibility = View.GONE
                    adapter.submitList(it.data)
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvDetail.visibility = View.GONE
                    if (requireContext().isNotTablet()) binding.fabEditItem.visibility = View.GONE
                    binding.layoutNoItem.root.visibility = View.VISIBLE
                    viewModel.sendEvent(Event.GetItemError(it.message.toString()))
                }
            }
        }

        lifecycleScope.launch {
            viewModel.eventChannel
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { event ->
                    when (event) {
                        is Event.EditItemSuccess -> showSnackBar(binding.root, event.message)
                        is Event.EditItemError -> showSnackBar(binding.root, event.message)
                        is Event.DeleteItemSuccess -> {
                            showSnackBar(binding.root, event.message)
                            findNavController().navigateUp()
                        }
                        is Event.DeleteItemError -> showSnackBar(binding.root, event.message)
                        else -> {}
                    }
                }
        }
    }

    private fun setupEditFab() {
        when (requireContext().isTablet()) {
            true -> {
                (requireActivity() as MainActivity).setupNavRail(R.layout.layout_nav_rail_header_detail) { fab ->
                    fab.setOnClickListener {
                        findNavController().navigateSafe(R.id.action_bookDetailFragment_to_updateBookDialogFragment)
                    }
                }
            }
            false -> {
                binding.fabEditItem.apply {
                    visibility = View.VISIBLE
                    setOnClickListener {
                        findNavController().navigateSafe(R.id.action_bookDetailFragment_to_updateBookDialogFragment)
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_item_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                viewModel.deleteBook()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}