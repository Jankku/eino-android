package com.jankku.eino.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.viewbinding.ViewBinding
import com.jankku.eino.databinding.FragmentLoginBinding
import com.jankku.eino.ui.BindingFragment
import com.jankku.eino.util.NetworkStatus
import com.jankku.eino.util.Result
import com.jankku.eino.util.hideBottomNav
import com.jankku.eino.util.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

private const val TAG = "LoginFragment"

@AndroidEntryPoint
class LoginFragment : BindingFragment<FragmentLoginBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentLoginBinding::inflate
    private val viewModel: AuthViewModel by viewModels()

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNav(requireActivity())
        setLoginBtnListener()
        setupObservers()
    }

    private fun setLoginBtnListener() {
        binding.btnLogin.setOnClickListener {
            val username = binding.tietUsername.text.toString()
            val password = binding.tietPassword.text.toString()
            viewModel.login(username, password)
        }
    }

    @ExperimentalCoroutinesApi
    private fun setupObservers() {
        viewModel.response.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    showSnackBar(binding.root, response.data.toString())
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
}