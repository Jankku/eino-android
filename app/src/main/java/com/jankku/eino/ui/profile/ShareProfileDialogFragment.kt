package com.jankku.eino.ui.profile

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jankku.eino.BuildConfig
import com.jankku.eino.databinding.FragmentShareProfileDialogBinding
import com.jankku.eino.util.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

private const val TAG = "ShareProfileDialogFragment"

class ShareProfileDialogFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentShareProfileDialogBinding? = null
    val binding get() = _binding!!
    val viewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShareProfileDialogBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.generateShare()
        setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        viewModel.share.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Result.Loading -> {
                    binding.pbImage.show()
                }
                is Result.Success -> {
                    binding.pbImage.hide()
                    val share = response.data?.results?.get(0)
                    if (share?.share_id?.isNotBlank() == true) {
                        val shareImageUrl = Uri.parse(BuildConfig.baseUrl)
                            .buildUpon()
                            .appendPath("share")
                            .appendPath(share.share_id)
                            .build()
                        Glide.with(this)
                            .load(shareImageUrl)
                            .into(binding.ivShare)

                        binding.btnCopyLink.apply {
                            isEnabled = true
                            setOnClickListener {
                                requireContext().copyToClipboard(shareImageUrl.toString())
                                requireContext().showToast("Image URL copied to clipboard")
                            }
                        }

                        binding.btnShareImage.apply {
                            isEnabled = true
                            setOnClickListener {
                                val uri = getBitmapFromDrawable(
                                    binding.ivShare.drawable.toBitmap(),
                                    share.share_id
                                )
                                    ?: return@setOnClickListener requireContext().showToast("Failed to share")
                                ShareCompat.IntentBuilder(requireActivity())
                                    .setType("image/*")
                                    .setChooserTitle("Share image")
                                    .setStream(uri)
                                    .startChooser()
                            }
                        }
                    }
                }
                is Result.Error -> {
                    binding.pbImage.hide()
                    requireContext().showToast(response.message.toString())
                }
                else -> {}
            }
        }
    }


    private fun getBitmapFromDrawable(bmp: Bitmap, id: String): Uri? {
        var bmpUri: Uri? = null
        try {
            val file = File(
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "${id}.jpeg"
            )
            val out = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.JPEG, 95, out)
            out.close()

            bmpUri = FileProvider.getUriForFile(
                requireContext(),
                "com.jankku.eino.fileprovider",
                file
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bmpUri
    }


}