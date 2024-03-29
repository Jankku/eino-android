package com.jankku.eino.ui.book.dialog

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
import com.jankku.eino.databinding.FragmentBaseBookDialogBinding
import com.jankku.eino.network.request.BookRequest
import com.jankku.eino.ui.book.BookViewModel
import com.jankku.eino.util.convertUnixToDate
import com.jankku.eino.util.getCurrentDate
import com.jankku.eino.util.getCurrentYear
import com.jankku.eino.util.getDateFromString
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "BaseBookDialogFragment"

@AndroidEntryPoint
open class BaseBookDialogFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentBaseBookDialogBinding? = null
    val binding get() = _binding!!
    val viewModel: BookViewModel by activityViewModels()
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
        _binding = FragmentBaseBookDialogBinding.inflate(inflater)
        statusValueArray = resources.getStringArray(R.array.dialog_book_status)
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
        binding.mactvBookStatus.apply {
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
            binding.mactvBookStartDate.setText(convertUnixToDate(it), false)
        }

        endDatePicker.addOnPositiveButtonClickListener {
            binding.mactvBookEndDate.setText(convertUnixToDate(it), false)
        }

        // Dropdown menus

        binding.mactvBookStartDate.apply {
            setText(dateFieldItems[0]) // "Today" as default value
            setAdapter(adapter)
        }

        binding.mactvBookEndDate.apply {
            setText(dateFieldItems[0]) // "Today" as default value
            setAdapter(adapter)
        }

        binding.mactvBookStartDate.setOnItemClickListener { _, _, i, _ ->
            when (i) {
                0 -> binding.mactvBookStartDate.setText(getCurrentDate(), false)
                1 -> startDatePicker.show(childFragmentManager, "StartDatePicker")
            }
        }

        binding.mactvBookEndDate.setOnItemClickListener { _, _, i, _ ->
            when (i) {
                0 -> binding.mactvBookEndDate.setText(getCurrentDate(), false)
                1 -> endDatePicker.show(childFragmentManager, "EndDatePicker")
            }
        }
    }

    fun getBookRequest(): BookRequest {
        val isbn = binding.tietBookIsbn.text.toString()
        val title = binding.tietBookTitle.text.toString()
        val author = binding.tietBookAuthor.text.toString()
        val publisher = binding.tietBookPublisher.text.toString()
        val imageUrl = binding.tietBookImageUrl.text.toString()
        val pages = binding.tietBookPages.text.toString().toIntOrNull() ?: 0
        val year = binding.tietBookYear.text.toString().toIntOrNull() ?: getCurrentYear()
        val status = binding.mactvBookStatus.text.toString().lowercase()
        val score = binding.sliderBookScore.value.toInt()
        val startDate =
            getDateFromString(binding.mactvBookStartDate.text.toString(), requireActivity())
        val endDate = getDateFromString(binding.mactvBookEndDate.text.toString(), requireActivity())

        return BookRequest(
            isbn,
            title,
            author,
            publisher,
            imageUrl,
            pages,
            year,
            status,
            score,
            startDate,
            endDate
        )
    }
}