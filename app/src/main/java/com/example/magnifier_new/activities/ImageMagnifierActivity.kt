package com.example.magnifier_new.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.magnifier_new.R
import com.example.magnifier_new.databinding.ActivityImageMagnifierBinding
import com.example.magnifier_new.helper.checkAndRequestCameraPermissions
import com.example.magnifier_new.helper.delayViewClickable
import com.example.magnifier_new.helper.getFileUri
import com.example.magnifier_new.helper.getOutputDirectory
import com.example.magnifier_new.helper.getWindowWidth
import com.example.magnifier_new.helper.scanMedia
import com.example.magnifier_new.helper.showToast
import com.example.magnifier_new.dialogs.ImagePickerDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class ImageMagnifierActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding : ActivityImageMagnifierBinding

    private var photoUri: Uri? = null
    private var photoBitmap: Bitmap? = null
    private var alteredBitmap: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageMagnifierBinding.inflate(layoutInflater)

        setContentView(binding.root)
        binding.llPlaceHolder.setOnClickListener(this)
        /*binding.btnCamera.setOnClickListener(this)
        binding.btnGallery.setOnClickListener(this)
        binding.btnEdit.setOnClickListener(this)
        binding.btnDelete.setOnClickListener(this)*/
        binding.btnSave.setOnClickListener(this)
        binding.btnBack.setOnClickListener(this)


        binding.sbFactorBar.setOnSeekBarChangeListener(onFactorChangeListener)
        binding.sbFactorBar.progress = 30
        binding.sbRadiusBar.setOnSeekBarChangeListener(onRadiusChangeListener)
        binding.sbRadiusBar.progress = 100

        registerCameraLauncher()
        registerGalleryLauncher()
    }


    private var galleryLauncher : ActivityResultLauncher<Intent>?=null
    private fun registerGalleryLauncher() {
        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
            if(result.resultCode== RESULT_OK){
                clearImageView()
                val uri = result.data?.data
                uri?.let {
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)

                        val afterBitmap = resizeImage(bitmap)

                        binding.llPlaceHolder.visibility = View.GONE
                        binding.imageView.visibility = View.VISIBLE
                        alteredBitmap = Bitmap.createBitmap(
                            afterBitmap.width,
                            afterBitmap.height,
                            afterBitmap.config
                        )

                        binding.imageView.setNewImage(alteredBitmap, afterBitmap)

                        showToast("Touch image for zoom")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun saveImage(b : Bitmap, imageName : String) {
        val imageOutStream: OutputStream?
        val imagesDir = getOutputDirectory()
        val image = File(imagesDir, "$imageName.jpg")
        image.setLastModified(System.currentTimeMillis())
        val uri = getFileUri(image)
        imageOutStream = FileOutputStream(image)
        try {
            b.compress(Bitmap.CompressFormat.JPEG, 100, imageOutStream)
        } finally {
            imageOutStream.close()
            CoroutineScope(Dispatchers.Main).launch{
                showToast("Image Saved!")
                uri?.path?.let {
                    val f = File(it)
                    scanMedia(f.absolutePath)
                }
            }
        }
    }


    private val onFactorChangeListener: SeekBar.OnSeekBarChangeListener = object :
        SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            val factor = (progress/20)+1
            binding.imageView.setMFactor(factor)
            val factorText = "${progress/20}x"
            binding.magnificientFactorValueText.text = factorText
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {}
        override fun onStopTrackingTouch(seekBar: SeekBar) {}
    }

    private val onRadiusChangeListener: SeekBar.OnSeekBarChangeListener = object :
        SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            binding.imageView.setRadius(progress)
            binding.loupeRadiusValueText.text = progress.toString()
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {}
        override fun onStopTrackingTouch(seekBar: SeekBar) {}
    }

    private fun imagePickerDialog() {
        val imagePickerMagnifier = ImagePickerDialog(
            this@ImageMagnifierActivity,
            object : ImagePickerDialog.ImagePickerClicks {
                override fun openCamera() {
                    cameraOpen()
                }

                override fun openGallery() {
                    galleryOpen()
                }
            }
        )
        imagePickerMagnifier.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        imagePickerMagnifier.show()
    }

    override fun onClick(view: View) {
        if(binding.imageView.isUserInteracting()){
            return
        }
        when (view.id) {
            /*R.id.btnCamera -> {
                binding.btnCamera.delayViewClickable()
                cameraOpen()
            }
            R.id.btnGallery -> {
                binding.btnGallery.delayViewClickable()
                galleryOpen()
            }
            R.id.btnEdit -> {
                if(!binding.imageView.isImageSelected()){
                    showToast("Please select an image!")
                    return
                }
                if (binding.imageView.isDraw()) {
                    binding.imageView.isDraw(false)
                    binding.btnEdit.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@ImageMagnifierScreen,
                            R.drawable.ic_edit
                        )
                    )
                    binding.btnSave.visibility = View.VISIBLE
                } else {
                    binding.imageView.isDraw(true)
                    binding.btnEdit.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@ImageMagnifierScreen,
                            R.drawable.ic_done
                        )
                    )
                }
            }
            R.id.btnDelete -> {
                clearImageView()
            }*/
            R.id.llPlaceHolder -> {
                binding.llPlaceHolder.delayViewClickable()
                imagePickerDialog()
            }
            R.id.btnSave ->{
                CoroutineScope(Dispatchers.Default).launch{
                    binding.imageView.getResultBitmap()?.let {
                        withContext(Dispatchers.Main) {
                            enterNameDialog(it)
                        }
                    }?:run{
                        withContext(Dispatchers.Main){
                            showToast("Something went wrong, Please try again!")
                        }
                    }
                }
            }
            R.id.btnBack ->{
                onBackPressed()
            }
        }
    }

    private fun enterNameDialog(bitmap: Bitmap) {
        val dialog = Dialog(this)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.save_image_dialog_layout)
        dialog.window!!.setLayout(
            getWindowWidth(0.8f),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(
            ResourcesCompat.getDrawable(resources, R.drawable.auto_renewal_background,theme)
        )
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
        val btnSave = dialog.findViewById<Button>(R.id.btnSave)
        val etName = dialog.findViewById<EditText>(R.id.etName)
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        btnSave.setOnClickListener {
            val name = etName.text.toString()
            if(isValidName(name)){
                dialog.dismiss()
                CoroutineScope(Dispatchers.Default).launch {
                    saveImage(bitmap, name)
                }
            }else{
                showToast("Please enter a valid name!")
            }
        }
        dialog.setCancelable(false)
        dialog.show()
        etName.requestFocus()
    }

    private fun isValidName(name: String): Boolean {
        var res = true
        if(name.trim().isEmpty() || containsSpecialCharacters(name)){
            res = false
        }
        return res
    }

    private fun containsSpecialCharacters(name: String): Boolean {
        var res = false
        val specialCharactersString = "!@#$%&*()'+,-./:;<=>?[]^_`{|}"
        for (i in name.indices) {
            val ch: Char = name[i]
            if (specialCharactersString.contains(ch.toString())) {
                res = true
                break
            } else if (i == name.length - 1) {
                res = false
            }
        }
        return res
    }

    private fun clearImageView() {
        binding.imageView.clean()
        try{
            binding.imageView.setNewImage(null,null)
        }catch (e:Exception){
        }
        binding.llPlaceHolder.visibility = View.VISIBLE
        binding.btnSave.visibility = View.GONE
        if (binding.imageView.isDraw()) {
            binding.imageView.isDraw(false)
//            binding.btnEdit.setImageDrawable(
//                ContextCompat.getDrawable(
//                    this@ImageMagnifierActivity,
//                    R.drawable.ic_edit
//                )
//            )
        }
    }

    private fun galleryOpen() {
        /*checkAndRequestPermissions { granted->
            if(granted){
        */        val intentGallery = Intent()
        intentGallery.type = "image/*"
        intentGallery.action = Intent.ACTION_GET_CONTENT
        galleryLauncher?.launch(intentGallery)
        //}
        //}
    }

    private fun createImageFile(): File {
        return File("null")/*CameraXFragment.createFile(
            getOutputDirectory(),
            getString(R.string.app_name) + " ${System.currentTimeMillis()}"
        )*/
    }

    private var takePictureLauncher:ActivityResultLauncher<Uri>?=null
    private fun registerCameraLauncher(){
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()){ success->
            if(success){
                photoUri?.let {
                    try{
                        clearImageView()
                        photoBitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)

                        val afterBitmap = resizeImage(photoBitmap!!)

                        binding.llPlaceHolder.visibility = View.GONE
                        binding.imageView.visibility = View.VISIBLE

                        alteredBitmap = Bitmap.createBitmap(
                            afterBitmap.width,
                            afterBitmap.height,
                            afterBitmap.config
                        )

                        binding.imageView.setNewImage(alteredBitmap, afterBitmap)

                        showToast("Touch image for zoom")

                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun cameraOpen() {
        this.checkAndRequestCameraPermissions { granted->
            if(granted){
                val photoFile: File? = try {
                    createImageFile()
                } catch (e: Exception) {
                    null
                }
                photoFile?.let {
                    photoUri = this.getFileUri(it)
                    photoUri?.let { uri->
                        takePictureLauncher?.launch(uri)
                    }
                }
            }
        }
    }

    private fun resizeImage(image: Bitmap): Bitmap {
        val width = image.width
        val height = image.height

        val scaleWidth = width * 0.9
        val scaleHeight = height * 0.9

        if (image.byteCount <= 1000000)
            return image

        return Bitmap.createScaledBitmap(image, scaleWidth.toInt(), scaleHeight.toInt(), false)
    }
}