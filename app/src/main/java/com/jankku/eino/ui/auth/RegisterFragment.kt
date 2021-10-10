package com.jankku.eino.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.jankku.eino.R
import com.jankku.eino.databinding.FragmentRegisterBinding
import com.jankku.eino.ui.common.BindingFragment
import com.jankku.eino.util.NetworkStatus
import com.jankku.eino.util.Result
import com.jankku.eino.util.hideBottomNav
import com.jankku.eino.util.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

private const val TAG = "RegisterFragment"

@AndroidEntryPoint
class RegisterFragment : BindingFragment<FragmentRegisterBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentRegisterBinding::inflate
    private val viewModel: AuthViewModel by viewModels()
    private val validation = AwesomeValidation(ValidationStyle.BASIC)

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNav(requireActivity())
        setRegisterBtnListener()
        setupObservers()
    }

    private fun setRegisterBtnListener() {
        binding.btnRegister.setOnClickListener {
            formFieldValidation()

            if (validation.validate()) {
                val username = binding.tietUsername.text.toString()
                val password = binding.tietPassword.text.toString()
                val password2 = binding.tietPassword2.text.toString()
                viewModel.register(username, password, password2)
            }
        }
    }

    @ExperimentalCoroutinesApi
    private fun setupObservers() {
        viewModel.registerResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    showSnackBar(binding.root, response.data.toString())
                    val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
                    findNavController().navigate(action)
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showSnackBar(binding.root, response.message.toString())
                }
            }
        }

        viewModel.networkStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                is NetworkStatus.Available -> {
                }
                is NetworkStatus.Unavailable -> {
                    showSnackBar(binding.root, "No network available")
                }
            }
        }
    }

    private fun formFieldValidation() {
        validation.addValidation(
            activity,
            R.id.tilUsername,
            RegexTemplate.NOT_EMPTY,
            R.string.validation_username_empty
        )
        validation.addValidation(
            activity,
            R.id.tilUsername,
            "^.{3,255}\$",
            R.string.validation_username_length_invalid
        )
        validation.addValidation(
            activity,
            R.id.tilPassword,
            RegexTemplate.NOT_EMPTY,
            R.string.validation_password_empty
        )
        validation.addValidation(
            activity,
            R.id.tilPassword,
            "^.{8,255}\$",
            R.string.validation_password_length_invalid
        )
        validation.addValidation(
            activity,
            R.id.tietPassword2,
            R.id.tietPassword,
            R.string.validation_passwords_dont_match
        )
    }
}