package com.stemdigital.inventorytracker

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment

class ImagePickerFragment : Fragment() {

    private lateinit var imageView: ImageView
    private lateinit var buttonPickFromGallery: Button
    private lateinit var buttonTakePhoto: Button
    private lateinit var buttonRemoveImage: Button

    private var selectedBitmap: Bitmap? = null
    private var selectedImagePath: String? = null

    companion object {
        const val PICK_IMAGE_REQUEST = 1001
        const val CAMERA_REQUEST = 1002
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_image_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView = view.findViewById(R.id.imageViewSelected)
        buttonPickFromGallery = view.findViewById(R. id.buttonPickFromGallery)
        buttonTakePhoto = view.findViewById(R.id. buttonTakePhoto)
        buttonRemoveImage = view.findViewById(R.id.buttonRemoveImage)

        buttonPickFromGallery.setOnClickListener {
            pickImageFromGallery()
        }

        buttonTakePhoto.setOnClickListener {
            takePhotoWithCamera()
        }

        buttonRemoveImage.setOnClickListener {
            removeImage()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun takePhotoWithCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, CAMERA_REQUEST)
        } else {
            Toast.makeText(requireContext(), "Camera not available", Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in API 34")
    override fun onActivityResult(requestCode:  Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    data?.data?.let { uri ->
                        selectedBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES. P) {
                            ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireActivity().contentResolver, uri))
                        } else {
                            @Suppress("DEPRECATION")
                            MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
                        }
                        imageView.setImageBitmap(selectedBitmap)
                    }
                }
                CAMERA_REQUEST -> {
                    selectedBitmap = data?.extras?.get("data") as? Bitmap
                    imageView.setImageBitmap(selectedBitmap)
                }
            }
        }
    }

    private fun removeImage() {
        selectedBitmap = null
        selectedImagePath = null
        imageView.setImageResource(R.drawable.ic_placeholder_image)
        Toast.makeText(requireContext(), "Image removed", Toast.LENGTH_SHORT).show()
    }

    fun getSelectedBitmap(): Bitmap? = selectedBitmap

    fun saveImage(itemId: Int): String? {
        return selectedBitmap?.let {
            ImageUtils.saveBitmapToInternalStorage(requireContext(), it, itemId)
        }
    }
}