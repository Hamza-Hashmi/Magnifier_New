package com.example.magnifier_new.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.magnifier_new.databinding.ActivitySplashBinding


class SplashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {

        } else {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED ) {
            }
            else  {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }


        startHandler(3000)

        binding.getStartedBtn.setOnClickListener {
            binding.getStartedBtn.isEnabled = false
            startActivity(Intent(this@SplashScreen, MainActivity::class.java))
            finish()
        }
    }

    private fun startHandler(delayTime:Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            kotlin.run {
                binding.progressBar.visibility = View.GONE
                binding.getStartedBtn.visibility = View.VISIBLE
            }

        }, delayTime)
    }

    private fun isFirstLaunch(): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isFirstLaunch = sharedPreferences.getBoolean("first_launch", true)
        if (isFirstLaunch) {
            // Update the preference to false for future launches
            sharedPreferences.edit().putBoolean("first_launch", false).apply()
        }
        return isFirstLaunch
    }
}
