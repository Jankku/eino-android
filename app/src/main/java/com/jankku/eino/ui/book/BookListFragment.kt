package com.jankku.eino.ui.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.viewbinding.ViewBinding
import com.jankku.eino.databinding.FragmentBookListBinding
import com.jankku.eino.ui.BindingFragment
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "BookListFragment"

@AndroidEntryPoint
class BookListFragment : BindingFragment<FragmentBookListBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentBookListBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}