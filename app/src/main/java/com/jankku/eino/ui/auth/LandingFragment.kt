package com.jankku.eino.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.jankku.eino.databinding.FragmentLandingBinding
import com.jankku.eino.ui.common.BindingFragment
import com.jankku.eino.util.hideBottomNav
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "LandingFragment"

@AndroidEntryPoint
class LandingFragment : BindingFragment<FragmentLandingBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentLandingBinding::inflate
    private val viewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNav(requireActivity())
        setupClickListeners()
        checkLoginStatus()
    }

    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
            val action = LandingFragmentDirections.actionLandingFragmentToRegisterFragment()
            findNavController().navigate(action)
        }

        binding.btnLogin.setOnClickListener {
            val action = LandingFragmentDirections.actionLandingFragmentToLoginFragment()
            findNavController().navigate(action)
        }
    }

    private fun checkLoginStatus() {
        viewModel.isLoggedIn.observe(viewLifecycleOwner) { isLoggedIn ->
            if (isLoggedIn) {
                val action = LandingFragmentDirections.actionLandingFragmentToAppGraph()
                findNavController().navigate(action)
            }
        }
    }
}