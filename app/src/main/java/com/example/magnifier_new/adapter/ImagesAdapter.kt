package com.example.magnifier_new.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.magnifier_new.R
import com.example.magnifier_new.activities.ImageViewerScreen
import com.example.magnifier_new.activities.SavedImagesScreen
import com.example.magnifier_new.databinding.ListitemImagesBinding
import com.example.magnifier_new.databinding.SavedImagesScreenBinding
import com.example.magnifier_new.helper.openActivity
import com.example.magnifier_new.helper.showToast
import com.example.magnifier_new.model.ImagesModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ImagesAdapter(var context: Context, var bindind: SavedImagesScreenBinding, var onAdapterChanged:(()->Unit)?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var clickable:Boolean = true
    var list = arrayListOf<ImagesModel>()
    private val selectedItemPositions = ArrayList<Int>()
    private val selectedFiles = ArrayList<ImagesModel>()
    private var sharedPref:SharedPreferences = context.getSharedPreferences(context.getString(R.string.pref_db),Context.MODE_PRIVATE)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            ListitemImagesBinding.inflate(LayoutInflater.from(context),parent,false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bindData(list[position],position)
    }

    inner class ViewHolder(val binding:ListitemImagesBinding):RecyclerView.ViewHolder(binding.root){

        fun bindData(m: ImagesModel, position: Int){
            binding.tvName.text = m.name
            binding.tvDate.text = getFormattedDate(m.uri)
            binding.tvDate.let {
                if(it.text.isEmpty()){
                    it.visibility = View.GONE
                }else{
                    it.visibility = View.VISIBLE
                }
            }
            Glide.with(context)
                .load(m.uri)
                .override(500,500)
                .into(binding.ivImage)
            binding.clImage.setOnClickListener {
                if (SavedImagesScreen.isMulitpleSelctionEnable) {
                    // Folders and Files won't be opened here but can only be selected
                    if (list[position].isSelected) {
                        unSelectItemAtPosition(position)
                        if (getSelectedFiles().size <1){

                            SavedImagesScreen.isMulitpleSelctionEnable = false
                            bindind.btnDeleteAll.visibility = View.GONE
                            bindind.btnSelectAll.visibility = View.GONE
                            for (mposition in getSelectedItemsPositions()) {
                                list[mposition].isSelected = false
                            }
                            clearAllSelections()
                        }
                    } else {
                        selectItemAtPosition(position)
                    }

                    list[position].isSelected =
                        !list[position].isSelected

                    notifyItemChanged(position)
                }
                else{
                    delayClickable()
                    ImageViewerScreen.imagesModelsGlobal = m
                    context.openActivity(ImageViewerScreen::class.java)
                }
            }
           itemView.setOnLongClickListener(OnLongClickListener {
                if (!SavedImagesScreen.isMulitpleSelctionEnable){
                    SavedImagesScreen.isMulitpleSelctionEnable = true
                    bindind.btnDeleteAll.visibility = View.VISIBLE
                    bindind.btnSelectAll.visibility = View.VISIBLE
                    list[position].isSelected = true
                    selectItemAtPosition(position)
                    notifyItemChanged(position)
                }
               true // returning true instead of false, works for me
           })
//            binding.ivOptions.setOnClickListener {
//                if(clickable){
//                    delayClickable()
//                    showMenuOptions(adapterPosition,binding.ivOptions)
//                }
//            }

            if (m.isSelected) {
                selectFile(binding)
            } else {
                unSelectFile(binding)
            }
        }

        private fun delayClickable() {
            Looper.getMainLooper()?.let {
                clickable = false
                Handler(it).postDelayed(
                    {
                        clickable = true
                    }, 1000
                )
            }
        }

        private fun deleteItem(position: Int) {
            val dialog = AlertDialog.Builder(context)
                .setTitle("Delete image")
                .setMessage("Are you sure you want to delete this image?")
                .setPositiveButton("YES") { _, _ ->
                    deleteImage(position)
                }
                .setNegativeButton("NO",null)
                .create()
            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                ResourcesCompat.getColor(context.resources,R.color.textColor,context.theme)
            )
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                ResourcesCompat.getColor(context.resources,R.color.textColor,context.theme)
            )
        }

         fun deleteImage(pos:Int) {
            CoroutineScope(Dispatchers.Main).launch {
                list[pos].uri.path?.let {
                    val f = File(it)
                    val d = f.delete()
                    if(d){
                        context.showToast("image deleted!")
                        list.removeAt(pos)
                        notifyItemRemoved(pos)
                        if(list.size==0 || list.size==3){
                            onAdapterChanged?.invoke()
                        }
                    }else{
                        context.showToast("Something went wrong, Please try again!")
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun updateList(list: ArrayList<ImagesModel>) {
        this.list = list
        performSorting()
    }

    fun sortListByName(){
        CoroutineScope(Dispatchers.Main).launch {
            list.sortWith { o1, o2 ->
                val n1 = getSimpleName(o1.name)
                val n2 = getSimpleName(o2.name)
                n1.compareTo(n2)
            }
            withContext(Dispatchers.Main){
                notifyDataSetChanged()
                onAdapterChanged?.invoke()
            }
        }
    }

    private fun getSimpleName(name: String): String {
        return name.trim().split(".")[0].lowercase()
    }

    fun sortListByTime(){
        CoroutineScope(Dispatchers.Default).launch {
            bubbleSortByTime(list,list.size)
            withContext(Dispatchers.Main){
                notifyDataSetChanged()
                onAdapterChanged?.invoke()
            }
        }
    }

    private fun bubbleSortByTime(arr: ArrayList<ImagesModel>, n: Int) {
        if (n <= 1) {
            return
        }
        for (i in 0 until n - 1) {
            val t1 = getCreatedDate(arr[i].uri)
            val t2 = getCreatedDate(arr[i+1].uri)
            if (t1 < t2) {
                val temp = arr[i]
                arr[i] = arr[i + 1]
                arr[i + 1] = temp
            }
        }
        bubbleSortByTime(arr, n - 1)
    }

    val formatter = SimpleDateFormat("dd-MM-yyyy  |  HH:mm",Locale.getDefault())
    @SuppressLint("SimpleDateFormat")
    private fun getFormattedDate(uri:Uri): String {
        val srcDate = getCreatedDate(uri)
        if(srcDate<=0){
            return ""
        }
        return try{
            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = srcDate
            formatter.format(calendar.time)
        }catch (e:Exception){
            ""
        }
    }

    private fun getCreatedDate(uri:Uri) : Long {
        return try{
            val f = File(uri.path!!)
            f.lastModified()
        }catch (e:Exception){
            0
        }
    }

    private fun performSorting(){
        if(sharedPref.getString("sorting","name").equals("name",true)){
            sortListByName()
        }else{
            sortListByTime()
        }
    }

    fun selectItemAtPosition(position: Int) {
        selectedItemPositions.add(position)
        selectedFiles.add(list[position])
        Log.e(
            "TAG",
            "SelectItemAtPosition: File added: ${list[position].name}" +
                    ", SIZE: ${selectedFiles.size}"
        )
    }

    fun unSelectItemAtPosition(position: Int) {
        selectedItemPositions.remove(position)
        selectedFiles.remove(list[position])
        Log.e(
            "TAG",
            "unSelectItemAtPosition: File removed: ${list[position].name}" +
                    ", SIZE: ${selectedFiles.size}"
        )
    }
    private fun unSelectFile(view:ListitemImagesBinding ) {
        view.ivSelected.visibility = View.GONE
    }

    private fun selectFile(view: ListitemImagesBinding) {
        view.ivSelected.visibility = View.VISIBLE
    }
    fun getSelectedItemsCount(): Int {
        return selectedItemPositions.size
    }

    fun getSelectedItemsPositions():ArrayList<Int> {
        return selectedItemPositions
    }

    fun getSelectedFiles(): ArrayList<ImagesModel> {
        return selectedFiles
    }

    fun clearAllSelections() {

        selectedFiles.clear()
        selectedItemPositions.clear()
    }
    fun selectAll(){
        list.forEachIndexed { i, _ ->
            /* selectedItemPositions.add(i)
             selectedFiles.add(File(differ.currentList[i].path))*/
            Log.e("TAG", "selectAll: $i")
            list[i].isSelected = true
            selectItemAtPosition(i)
            notifyItemChanged(i)

        }

    }
}
