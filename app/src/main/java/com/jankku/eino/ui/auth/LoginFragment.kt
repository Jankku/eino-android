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
import com.jankku.eino.NavGraphDirections
import com.jankku.eino.R
import com.jankku.eino.databinding.FragmentLoginBinding
import com.jankku.eino.ui.common.BindingFragment
import com.jankku.eino.util.Result
import com.jankku.eino.util.hideBottomNav
import com.jankku.eino.util.navigateSafe
import com.jankku.eino.util.showSnackBar
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "LoginFragment"

@AndroidEntryPoint
class LoginFragment : BindingFragment<FragmentLoginBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentLoginBinding::inflate
    private val viewModel: AuthViewModel by viewModels()
    private val validation = AwesomeValidation(ValidationStyle.BASIC)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNav(requireActivity())
        setLoginBtnListener()
        setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        validation.clear()
    }

    private fun setLoginBtnListener() {
        binding.btnLogin.setOnClickListener {
            formFieldValidation()

            if (validation.validate()) {
                val username = binding.tietUsername.text.toString()
                val password = binding.tietPassword.text.toString()
                viewModel.login(username, password)
            }
        }
    }

    private fun setupObservers() {
        viewModel.loginResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Result.Loading -> {
                    binding.progressBar.show()
                }
                is Result.Success -> {
                    binding.progressBar.hide()
                    findNavController().navigateSafe(NavGraphDirections.actionGlobalBookGraph())
                }
                is Result.Error -> {
                    binding.progressBar.hide()
                    showSnackBar(binding.root, response.message.toString())
                }
                else -> {}
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
            R.id.tilPassword,
            RegexTemplate.NOT_EMPTY,
            R.string.validation_password_empty
        )
    }
}