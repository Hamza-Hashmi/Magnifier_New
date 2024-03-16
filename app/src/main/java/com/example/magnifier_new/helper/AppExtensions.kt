package com.example.magnifier_new.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.FragmentActivity
import com.example.magnifier_new.R
import com.example.magnifier_new.*


import java.io.File

const val timeFormat: String = "dd-MM-yyyy  |  HH:mm"
fun Context.scanMedia(path: String) {
    MediaScannerConnection.scanFile(
        this, arrayOf(path), null
    ) { p, _ -> Log.i("TAG", "Finished scanning $p") }
}

fun Context.privacyPolicy() {
    try {
        val url = "https://sites.google.com/view/magnifying-glass-with-light-ca"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(Intent.createChooser(intent, "Open link"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.rateUs() {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
    startActivity(intent)
}




fun <T> Context.openActivity(it: Class<T>) {
    val intent = Intent(this, it)
    startActivity(intent)
}

fun Activity.getWindowWidth(percentage: Float): Int {
    return (resources.displayMetrics.widthPixels*percentage).toInt()
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}



fun Context.isNetworkConnected(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    return true
                }
            }
        }
    } else {
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
            return true
        }
    }
    return false
}


fun Context.getFileUri(f:File):Uri?{
    return try{
        FileProvider.getUriForFile(
            this,
            "${BuildConfig.APPLICATION_ID}.provider",
            f
        )
    }catch (e:Exception){
        null
    }
}

/** Milliseconds used for UI animations */
const val ANIMATION_FAST_MILLIS = 50L
const val ANIMATION_SLOW_MILLIS = 100L
fun Context.isInternetConnected(): Boolean {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            }
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            }
        }
    } else {
        val cm =
            this.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }
    return false
}
/** Use external media if it is available, our app's file directory otherwise */
fun Context.getOutputDirectory(): File {
    val appContext = applicationContext
    val mediaDir = externalMediaDirs.firstOrNull()?.let {
        File(
            it,
            appContext.resources.getString(R.string.app_name)
        ).apply { mkdirs() }
    }
    return if (mediaDir != null && mediaDir.exists())
        mediaDir
    else
        appContext.filesDir
}


fun Context.shareImage(uri: Uri) {
    try {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "image/*"
        share.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(
            Intent.createChooser(share, "Share Image")
        )
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun View.delayViewClickable() {
    Looper.getMainLooper()?.let {
        isClickable = false
        Handler(it).postDelayed(
            {
                isClickable = true
            }, 1000
        )
    }
}

