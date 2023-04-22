package com.jankku.eino.ui.profile

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jankku.eino.R
import com.jankku.eino.databinding.FragmentExportDialogBinding
import com.jankku.eino.network.response.profile.ExportAccountResponse
import com.jankku.eino.util.Result
import com.jankku.eino.util.showToast
import com.squareup.moshi.Moshi
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayInputStream
import java.io.IOException
import java.nio.charset.Charset
import java.util.*
import javax.inject.Inject

private const val TAG = "ExportDialogFragment"

@AndroidEntryPoint
class ExportDialogFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentExportDialogBinding? = null
    val binding get() = _binding!!
    val viewModel: ProfileViewModel by activityViewModels()
    private val validation = AwesomeValidation(ValidationStyle.BASIC)

    @Inject
    lateinit var moshi: Moshi

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
        _binding = FragmentExportDialogBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupExportButton()
        setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupExportButton() {
        validation.addValidation(
            binding.tietPassword,
            RegexTemplate.NOT_EMPTY,
            getString(R.string.validation_password_empty)
        )

        binding.btnExport.setOnClickListener {
            if (validation.validate()) {
                viewModel.exportAccount(binding.tietPassword.text.toString())
            }
        }
    }

    private fun setupObservers() {
        viewModel.accountData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Result.Loading -> {}
                is Result.Success -> {
                    exportAccount()
                }
                is Result.Error -> {
                    requireContext().showToast(response.message.toString())
                }
            }
        }
    }

    private fun exportAccount() {
        val formattedDate = String.format("%1tY%<tm%<td-%<tH%<tM%<tS", Date())
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            type = "application/json"
            putExtra(Intent.EXTRA_TITLE, "eino-export-${formattedDate}.json")
        }
        startActivityForResult(intent, EXPORT_REQ_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val userChosenUri = data?.data ?: return
        if (requestCode == EXPORT_REQ_CODE && resultCode == Activity.RESULT_OK) {
            try {
                val response = convertToJson(viewModel.accountData.value?.data)
                val inputStream =
                    ByteArrayInputStream(response.toByteArray(Charset.forName("UTF-8")))
                val outputStream = context?.contentResolver?.openOutputStream(userChosenUri)
                inputStream.use { input ->
                    outputStream.use { output ->
                        if (output != null) {
                            input.copyTo(output)
                        }
                    }
                }
                requireContext().showToast("Account data exported")
            } catch (e: IOException) {
                e.printStackTrace()
                requireContext().showToast(e.message.toString())
            }
            this.dismiss()
        }
    }

    private fun convertToJson(response: ExportAccountResponse?): String {
        if (response == null) throw Exception("Account data is null")

        val twoSpaceIndent = "  "
        return moshi.adapter(ExportAccountResponse::class.java).indent(twoSpaceIndent)
            .toJson(response)
    }

    companion object {
        private const val EXPORT_REQ_CODE = 1
    }
}