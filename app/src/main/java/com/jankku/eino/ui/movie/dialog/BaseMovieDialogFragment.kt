package com.jankku.eino.ui.movie.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.jankku.eino.R
import com.jankku.eino.databinding.FragmentBaseMovieDialogBinding
import com.jankku.eino.network.request.MovieRequest
import com.jankku.eino.ui.movie.MovieViewModel
import com.jankku.eino.util.convertUnixToDate
import com.jankku.eino.util.getCurrentDate
import com.jankku.eino.util.getCurrentYear
import com.jankku.eino.util.getDateFromString
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "BaseMovieDialogFragment"

@AndroidEntryPoint
open class BaseMovieDialogFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentBaseMovieDialogBinding? = null
    val binding get() = _binding!!
    val viewModel: MovieViewModel by activityViewModels()
    lateinit var statusValueArray: Array<String>
    lateinit var dateFieldItems: Array<String>

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
        _binding = FragmentBaseMovieDialogBinding.inflate(inflater)
        statusValueArray = resources.getStringArray(R.array.dialog_movie_status)
        dateFieldItems = resources.getStringArray(R.array.date_field_items)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDatePickers()
        setupStatusPicker()
        setupActionButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    open fun setupActionButton() {}

    open fun setupStatusPicker() {
        val adapter = ArrayAdapter(requireContext(), R.layout.item_date_picker, statusValueArray)
        binding.mactvMovieStatus.apply {
            setAdapter(adapter)
            setOnItemClickListener { _, _, i, _ -> setText(statusValueArray[i], false) }
        }
    }

    open fun setupDatePickers() {
        val adapter = ArrayAdapter(requireContext(), R.layout.item_date_picker, dateFieldItems)

        // Date pickers

        val startDatePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.dialog_start_date_picker_title)
            .build()

        val endDatePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.dialog_end_date_picker_title)
            .build()

        startDatePicker.addOnPositiveButtonClickListener {
            binding.mactvMovieStartDate.setText(convertUnixToDate(it), false)
        }

        endDatePicker.addOnPositiveButtonClickListener {
            binding.mactvMovieEndDate.setText(convertUnixToDate(it), false)
        }

        // Dropdown menus

        binding.mactvMovieStartDate.apply {
            setText(dateFieldItems[0]) // "Today" as default value
            setAdapter(adapter)
        }

        binding.mactvMovieEndDate.apply {
            setText(dateFieldItems[0]) // "Today" as default value
            setAdapter(adapter)
        }

        binding.mactvMovieStartDate.setOnItemClickListener { _, _, i, _ ->
            when (i) {
                0 -> binding.mactvMovieStartDate.setText(getCurrentDate(), false)
                1 -> startDatePicker.show(childFragmentManager, "StartDatePicker")
            }
        }

        binding.mactvMovieEndDate.setOnItemClickListener { _, _, i, _ ->
            when (i) {
                0 -> binding.mactvMovieEndDate.setText(getCurrentDate(), false)
                1 -> endDatePicker.show(childFragmentManager, "EndDatePicker")
            }
        }
    }

    fun getMovieRequest(): MovieRequest {
        val title = binding.tietMovieTitle.text.toString()
        val studio = binding.tietMovieStudio.text.toString()
        val director = binding.tietMovieDirector.text.toString()
        val writer = binding.tietMovieWriter.text.toString()
        val imageUrl = binding.tietMovieImageUrl.text.toString()
        val duration = binding.tietMovieDuration.text.toString().toIntOrNull() ?: 0
        val year = binding.tietMovieYear.text.toString().toIntOrNull() ?: getCurrentYear()
        val status = binding.mactvMovieStatus.text.toString().lowercase()
        val score = binding.sliderMovieScore.value.toInt()
        val startDate =
            getDateFromString(binding.mactvMovieStartDate.text.toString(), requireActivity())
        val endDate =
            getDateFromString(binding.mactvMovieEndDate.text.toString(), requireActivity())

        return MovieRequest(
            title,
            studio,
            director,
            writer,
            imageUrl,
            duration,
            year,
            status,
            score,
            startDate,
            endDate
        )
    }
}