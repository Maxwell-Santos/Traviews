package com.example.traviews.fragments

import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.traviews.R
import com.example.traviews.databinding.FragmentNewPostBinding
import com.example.traviews.model.PublishPostRequest
import com.example.traviews.model.VisitCosts
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class NewPostFragment: Fragment() {
    private var _binding: FragmentNewPostBinding? = null
    private val viewModel: NewPostViewModel by viewModels()
    private val binding get() = _binding!!

    private suspend fun compressImage(uri: Uri): File = withContext(Dispatchers.IO) {
        val outputFile = File.createTempFile("compressed_", ".jpg", requireContext().cacheDir).apply {
            deleteOnExit()
        }

        try {
            Glide.with(requireContext())
                .asFile()
                .load(uri)
                .apply(RequestOptions()
                    .override(1024, 768)
                    .encodeQuality(70)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .format(DecodeFormat.PREFER_RGB_565)
                )
                .submit()
                .get()
        } catch (e: Exception) {
            throw IOException("Falha ao comprimir imagem: ${e.message}")
        }
    }

    private suspend fun loadImage (it: Uri, imgView: ImageView) {
        try {
            val compressedFile = compressImage(it)
            val filename = formatFileName(it)
            loadImageWithGlide(Uri.fromFile(compressedFile), imgView)  // Exibe a versão comprimida
            viewModel.images.add(ImageContent(filename, Uri.fromFile(compressedFile)))
        } catch (e: Exception) {
            Toast.makeText(context, "Erro ao processar imagem", Toast.LENGTH_SHORT).show()
        }
    }

    private val selectImage1 = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { lifecycleScope.launch { loadImage(it, binding.imgView1) }  }
    }
    private val selectImage2 = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { lifecycleScope.launch { loadImage(it, binding.imgView2) }  }
    }
    private val selectImage3 = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { lifecycleScope.launch { loadImage(it, binding.imgView3) }  }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, // provavelemnte o xml que foi envolvido
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.newPostUiState.collect { state ->
                when (state) {
                    is NewPostUiState.Loading -> {
                        binding.btnPublish.isEnabled = false
                        binding.btnPublish.text = getString(R.string.new_post_btn_publi_loading)
                    }

                    is NewPostUiState.Success -> {
                        binding.btnPublish.isEnabled = true
                        binding.btnPublish.text = getString(R.string.new_post_btn_publi_success)

                        Toast.makeText(context, "Postagem publicada!", Toast.LENGTH_SHORT).show()
                        delay(2000)
                        binding.btnPublish.text = getString(R.string.new_post_btn_publi_text)

                        findNavController().navigate(R.id.homeFragment)
                    }

                    is NewPostUiState.Error -> {
                        Toast.makeText(context, "Falha o publicar, tente novamente", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        initDatePicker()

        binding.imgView1.setOnClickListener {
            selectImage1.launch("image/*")
        }

        binding.imgView2.setOnClickListener {
            selectImage2.launch("image/*")
        }

        binding.imgView3.setOnClickListener {
            selectImage3.launch("image/*")
        }

        binding.btnPublish.setOnClickListener {
            val description = binding.etDescription.text.toString()
            val visitFoodCost = binding.edtVisitFoodCost.text.toString().toDoubleOrNull()
            val visitAccommodationCost = binding.edtVisitAccommodationCost.text.toString().toDoubleOrNull()
            val visitEntertainmentCost = binding.edtVisitEntertainmentCost.text.toString().toDoubleOrNull()

            viewModel.publishPost(
                PublishPostRequest(
                    viewModel.dateFormatedToSave,
                    medias = viewModel.images.map { it.filename },
                    description = description,
                    visitCosts = VisitCosts(visitFoodCost, visitAccommodationCost, visitEntertainmentCost),
                )
            )
        }
    }

    private fun initDatePicker() {
        viewModel.dateFormatedToSave = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        binding.tvDate.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        binding.pickDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecione uma data")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            datePicker.addOnPositiveButtonClickListener { selectedDate ->
                binding.tvDate.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selectedDate))
                viewModel.dateFormatedToSave = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(selectedDate))
            }

            datePicker.show(childFragmentManager, "DATE_PICKER_TAG")
        }
    }

    private fun formatFileName(imageUri: Uri) : String {
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        return "${formatter.format(now)}.${getFileExtension(imageUri)}"
    }

    private fun getFileExtension(uri: Uri): String? {
        val contentResolver: ContentResolver = requireActivity().contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun loadImageWithGlide(uri: Uri, imgView: ImageView) {
        val requestOptions = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .override(1024, 768)
            .encodeQuality(70)

        Glide.with(this)
            .load(uri)
            .apply(requestOptions)
            .centerCrop()
            .into(imgView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}