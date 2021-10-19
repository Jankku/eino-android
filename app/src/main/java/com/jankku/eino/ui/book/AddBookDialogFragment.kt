package com.jankku.eino.ui.book

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
import com.jankku.eino.databinding.FragmentAddBookDialogBinding
import com.jankku.eino.network.request.AddBookRequest
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "AddBookDialogFragment"

@AndroidEntryPoint
class AddBookDialogFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentAddBookDialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BookListViewModel by activityViewModels()

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
        _binding = FragmentAddBookDialogBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addBookClickListener()
        setupDatePickers()
        setupStatusPicker()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addBookClickListener() {
        binding.btnAddBook.setOnClickListener {
            val request = getAddBookRequest()
            viewModel.addBook(request)
            this.dismiss()
        }
    }

    private fun getAddBookRequest(): AddBookRequest {
        val isbn = binding.tietBookIsbn.text.toString()
        val title = binding.tietBookTitle.text.toString()
        val author = binding.tietBookAuthor.text.toString()
        val publisher = binding.tietBookPublisher.text.toString()
        val pages = binding.tietBookPages.text.toString().toIntOrNull() ?: 0
        val year = binding.tietBookYear.text.toString().toIntOrNull() ?: 2021
        val status = binding.mactvBookStatus.text.toString().lowercase()
        val score = binding.sliderBookScore.value.toInt()
        val startDate = getDateFromString(binding.mactvBookStartDate.text.toString())
        val endDate = getDateFromString(binding.mactvBookStartDate.text.toString())

        return AddBookRequest(
            isbn,
            title,
            author,
            publisher,
            pages,
            year,
            status,
            score,
            startDate,
            endDate
        )
    }

    private fun setupStatusPicker() {
        val items = resources.getStringArray(R.array.book_status_field_items)
        val adapter = ArrayAdapter(requireContext(), R.layout.item_date_picker, items)

        binding.mactvBookStatus.apply {
            setText(items[0]) // "Reading" as default value
            setAdapter(adapter)
            setOnItemClickListener { _, _, i, _ -> setText(items[i], false) }
        }
    }

    private fun setupDatePickers() {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val today = formatter.format(Date())
        val dateFieldItems = resources.getStringArray(R.array.date_field_items)
        val adapter = ArrayAdapter(requireContext(), R.layout.item_date_picker, dateFieldItems)

        // Date pickers

        val startDatePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.book_start_date_picker_title)
            .build()

        val endDatePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.book_end_date_picker_title)
            .build()

        startDatePicker.addOnPositiveButtonClickListener {
            val date = formatter.format(it)
            binding.mactvBookStartDate.setText(date, false)
        }

        endDatePicker.addOnPositiveButtonClickListener {
            val date = formatter.format(it)
            binding.mactvBookEndDate.setText(date, false)
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
                0 -> binding.mactvBookStartDate.setText(today, false)
                1 -> startDatePicker.show(childFragmentManager, "StartDatePicker")
            }
        }

        binding.mactvBookEndDate.setOnItemClickListener { _, _, i, _ ->
            when (i) {
                0 -> binding.mactvBookEndDate.setText(today, false)
                1 -> endDatePicker.show(childFragmentManager, "EndDatePicker")
            }
        }
    }

    private fun getDateFromString(string: String): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = formatter.format(Date())
        val dateFieldItems = resources.getStringArray(R.array.date_field_items)

        return if (string == dateFieldItems[0]) {
            today
        } else {
            string
        }
    }
}