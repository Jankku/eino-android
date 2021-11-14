package com.jankku.eino.ui.movie.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RadioButton
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jankku.eino.data.enums.MovieStatus
import com.jankku.eino.data.enums.Sort
import com.jankku.eino.databinding.FragmentSortMovieDialogBinding
import com.jankku.eino.ui.movie.MovieViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MovieSortDialogFragment"

@AndroidEntryPoint
class MovieSortDialogFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentSortMovieDialogBinding? = null
    val binding get() = _binding!!
    val viewModel: MovieViewModel by activityViewModels()

    // https://stackoverflow.com/a/68232644
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout = bottomSheetDialog.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            )
            parentLayout?.let { bottomSheet ->
                val behaviour = BottomSheetBehavior.from(bottomSheet)
                val layoutParams = bottomSheet.layoutParams
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
                bottomSheet.layoutParams = layoutParams
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSortMovieDialogBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCurrentValues()
        setupApplyButton()
        setupOnChangeListeners()
    }

    private fun setCurrentValues() {
        val selectedTitleSort = viewModel.selectedTitleSort.value!!.ordinal
        val selectedStatusSort = viewModel.selectedStatusSort.value!!.ordinal
        val selectedScoreSort = viewModel.selectedScoreSort.value!!.ordinal

        with(binding.rgTitle) {
            check(getChildAt(selectedTitleSort).id)
        }

        with(binding.rgStatus) {
            check(getChildAt(selectedStatusSort).id)
        }

        with(binding.rgScore) {
            check(getChildAt(selectedScoreSort).id)
        }
    }

    private fun setupOnChangeListeners() {
        binding.rgTitle.setOnCheckedChangeListener { group, index ->
            val item = group.findViewById<RadioButton>(index)
            val statusIndex = group.indexOfChild(item)
            val status = Sort.values()[statusIndex]
            viewModel.setTitleSort(status)
        }

        binding.rgStatus.setOnCheckedChangeListener { group, index ->
            val item = group.findViewById<RadioButton>(index)
            val statusIndex = group.indexOfChild(item)
            val status = MovieStatus.values()[statusIndex]
            viewModel.setStatusSort(status)
        }

        binding.rgScore.setOnCheckedChangeListener { group, index ->
            val item = group.findViewById<RadioButton>(index)
            val scoreIndex = group.indexOfChild(item)
            val status = Sort.values()[scoreIndex]
            viewModel.setScoreSort(status)
        }
    }

    private fun setupApplyButton() {
        binding.btnApply.setOnClickListener {
            viewModel.getMoviesByStatus()
            this.dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}