package com.example.magnifier_new.activities

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.magnifier_new.R
import com.example.magnifier_new.databinding.SavedImagesScreenBinding
import com.example.magnifier_new.adapter.ImagesAdapter
import com.example.magnifier_new.helper.getOutputDirectory
import com.example.magnifier_new.model.ImagesModel
import kotlinx.coroutines.*
import java.io.File

class SavedImagesScreen : AppCompatActivity() {
    private lateinit var binding : SavedImagesScreenBinding
    private var imagesAdapter : ImagesAdapter? = null
    private var mSharedPrefrences: SharedPreferences? = null

    companion object{
        var isMulitpleSelctionEnable = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SavedImagesScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mSharedPrefrences = getSharedPreferences(getString(R.string.pref_db), Context.MODE_PRIVATE)

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnSelectAll.setOnCheckedChangeListener { compoundButton, b ->
            if (compoundButton.isChecked){
                imagesAdapter?.clearAllSelections()
                imagesAdapter?.selectAll()

                imagesAdapter?.notifyDataSetChanged()

            }else{
                cancelMultiSelectionMode()
            }

        }
        binding.btnDeleteAll.setOnClickListener{

            deleteAllImages()

        }
        initializeAdapter()
        loadImagesList()
    }


    override fun onResume() {
        super.onResume()
        if(ImageViewerScreen.isImageDelete){
            ImageViewerScreen.isImageDelete = false
            loadImagesList()
        }
    }

    private fun loadImagesList() {
        binding.progressbar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            val list = loadAllImages()
            delay(500)
            withContext(Dispatchers.Main) {
                imagesAdapter?.updateList(list)
                updateUI()
            }
        }
    }

    private fun initializeAdapter() {
        binding.rvImages.layoutManager = GridLayoutManager(this,3)
        CoroutineScope(Dispatchers.Main).launch {
            imagesAdapter = ImagesAdapter(this@SavedImagesScreen,binding){
                updateUI()
            }
            binding.rvImages.adapter = imagesAdapter
        }
    }

    private fun updateUI() {
        imagesAdapter?.let {
            if(it.itemCount>0){
                showDataList()
            }else{
                showPlaceHolder()
            }
        }?:run{
            showPlaceHolder()
        }
    }

    private fun showDataList() {
        binding.progressbar.visibility = View.GONE
        binding.rvImages.visibility = View.VISIBLE
        binding.ivPlaceHolder.visibility = View.GONE
    }

    private fun showPlaceHolder() {
        binding.progressbar.visibility = View.GONE
        binding.rvImages.visibility = View.GONE
        binding.ivPlaceHolder.visibility = View.VISIBLE
    }


    private fun loadAllImages() : ArrayList<ImagesModel> {
        val mImagesList = ArrayList<ImagesModel>()

        val mDirectory = getOutputDirectory()
        val mList = mDirectory.listFiles()

        mList?.let { list->
            for(images in list){
                val model = ImagesModel(
                    Uri.fromFile(images.absoluteFile),
                    images.name
                )
                mImagesList.add(model)
            }
        }

        return mImagesList
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun cancelMultiSelectionMode() {
        CoroutineScope(Dispatchers.IO).launch {
            for (position in imagesAdapter?.getSelectedItemsPositions()!!) {
                imagesAdapter!!.list[position].isSelected = false
            }
            imagesAdapter!!.clearAllSelections()
            CoroutineScope(Dispatchers.Main).launch {
                // binding.cardMoreOptions.visibility = View.GONE
                isMulitpleSelctionEnable = false
                binding.btnSelectAll.visibility = View.GONE
                binding.btnDeleteAll.visibility = View.GONE
                imagesAdapter!!.notifyDataSetChanged()

            }


        }
    }

    private fun deleteAllImages() {

        CoroutineScope(Dispatchers.IO).launch {
            imagesAdapter?.getSelectedFiles()?.forEachIndexed { _, file ->

                try{
                    val delete = File(file.uri.path)
                    if (delete.exists()) {
                        delete.delete()
                    }
                }catch (e:Exception){

                }


            }


        }.invokeOnCompletion {
            CoroutineScope(Dispatchers.Main).launch {
                try{
                 isMulitpleSelctionEnable = false
                 binding.btnSelectAll.visibility = View.GONE
                 binding.btnDeleteAll.visibility= View.GONE
                 imagesAdapter?.clearAllSelections()
                 loadImagesList()
                }catch (e:Exception){

                }

            }
        }

    }
}
