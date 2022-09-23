package com.jankku.eino.ui.profile

import android.app.Dialog
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
import com.jankku.eino.databinding.FragmentDeleteAccountDialogBinding
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "DeleteAccountDialogFragment"

@AndroidEntryPoint
class DeleteAccountDialogFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentDeleteAccountDialogBinding? = null
    val binding get() = _binding!!
    val viewModel: ProfileViewModel by activityViewModels()
    private val validation = AwesomeValidation(ValidationStyle.BASIC)

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
        _binding = FragmentDeleteAccountDialogBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDeleteButton()
    }

    private fun setupDeleteButton() {
        validation.addValidation(
            binding.tietPassword,
            RegexTemplate.NOT_EMPTY,
            getString(R.string.validation_password_empty)
        )

        binding.btnDeleteAccount.setOnClickListener {
            if (validation.validate()) {
                viewModel.deleteAccount(binding.tietPassword.text.toString())
                this.dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
