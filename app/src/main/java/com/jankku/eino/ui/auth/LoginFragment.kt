package com.jankku.eino.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jankku.eino.databinding.FragmentLoginBinding
import com.jankku.eino.util.NetworkStatus
import com.jankku.eino.util.Result
import com.jankku.eino.util.hideBottomNav
import com.jankku.eino.util.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

private const val TAG = "LoginFragment"

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    @ExperimentalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        hideBottomNav(requireActivity())
        setLoginBtnListener()
        setupObservers()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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