package com.jankku.eino.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.jankku.eino.databinding.FragmentLandingBinding
import com.jankku.eino.ui.BindingFragment
import com.jankku.eino.util.hideBottomNav

class LandingFragment : BindingFragment<FragmentLandingBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentLandingBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNav(requireActivity())
        setupClickListeners()
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
}