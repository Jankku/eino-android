package com.jankku.eino.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.viewbinding.ViewBinding
import com.jankku.eino.databinding.FragmentRegisterBinding
import com.jankku.eino.ui.BindingFragment
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "RegisterFragment"

@AndroidEntryPoint
class RegisterFragment : BindingFragment<FragmentRegisterBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentRegisterBinding::inflate
    private val viewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}