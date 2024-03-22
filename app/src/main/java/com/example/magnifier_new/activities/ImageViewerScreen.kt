package com.example.magnifier_new.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import com.bumptech.glide.Glide
import com.example.magnifier_new.R
import com.example.magnifier_new.databinding.ImageViewerScreenBinding
import com.example.magnifier_new.helper.getFileUri
import com.example.magnifier_new.helper.shareImage
import com.example.magnifier_new.helper.showToast
import com.example.magnifier_new.model.ImagesModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class ImageViewerScreen : AppCompatActivity() {
    lateinit var binding: ImageViewerScreenBinding

    companion object {
        var imagesModelsGlobal: ImagesModel?=null
        var isImageDelete:Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ImageViewerScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imagesModelsGlobal?.let {
            initImage(it)
        }
        initListeners()
    }

    private fun initImage(m: ImagesModel) {
        binding.titleText.text = m.name
        Glide.with(this)
            .load(m.uri)
            .into(binding.ivImage)
    }

    private fun initListeners() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.btnDeleteImage.setOnClickListener {
            Looper.getMainLooper()?.let {
                binding.btnDeleteImage.isClickable = false
                Handler(it).postDelayed(
                    {
                        binding.btnDeleteImage.isClickable = true
                    }, 1000
                )
            }
            imagesModelsGlobal?.let {
                val dialog = AlertDialog.Builder(this)
                    .setTitle("Delete image")
                    .setMessage("Are you sure you want to delete this image?")
                    .setPositiveButton("YES") { _, _ ->
                        deleteImage()
                    }
                    .setNegativeButton("NO",null)
                    .create()
                dialog.show()
                dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(
                    R.color.textColor))
                dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(
                    R.color.textColor))
            }
        }
        binding.btnShareImage.setOnClickListener {
            Looper.getMainLooper()?.let {
                binding.btnShareImage.isClickable = false
                Handler(it).postDelayed(
                    {
                        binding.btnShareImage.isClickable = true
                    }, 1000
                )
            }
            imagesModelsGlobal?.uri?.toFile()?.let { f->
                getFileUri(f)?.let { uri->
                    shareImage(uri)
                }
            }
        }
    }

    private fun deleteImage() {
        CoroutineScope(Dispatchers.Main).launch {
            imagesModelsGlobal?.uri?.path?.let {
                val b = File(it)
                val d = b.delete()
                if(d){
                    showToast("image deleted!")
                    isImageDelete = true
                    onBackPressed()
                }else{
                    showToast("Something went wrong!")
                }
            }
        }
    }



}