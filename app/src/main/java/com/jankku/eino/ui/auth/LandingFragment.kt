package com.jankku.eino.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.jankku.eino.R
import com.jankku.eino.databinding.FragmentLandingBinding
import com.jankku.eino.ui.common.BindingFragment
import com.jankku.eino.util.hideBottomNav
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "LandingFragment"

@AndroidEntryPoint
class LandingFragment : BindingFragment<FragmentLandingBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentLandingBinding::inflate
    private val viewModel: AuthViewModel by hiltNavGraphViewModels(R.id.auth_graph)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNav(requireActivity())
        setupClickListeners()
        checkLoginStatus()
    }

    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_landingFragment_to_registerFragment)
        }

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_landingFragment_to_loginFragment)
        }
    }

    private fun checkLoginStatus() {
        viewModel.isLoggedIn.observe(viewLifecycleOwner) { isLoggedIn ->
            if (isLoggedIn) {
                findNavController().navigate(R.id.action_global_book_graph)
            }
        }
    }
}