package com.example.magnifier_new.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.magnifier_new.databinding.ActivityMainBinding
import com.example.magnifier_new.helper.checkAndRequestCameraPermissions
import com.example.magnifier_new.helper.checkAndRequestPermissions
import com.example.magnifier_new.helper.openActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        eventListener()
    }

    private fun eventListener() {
        binding.btnImageMagnifier.setOnClickListener {
            checkAndRequestPermissions {
                openActivity(ImageMagnifierActivity::class.java)
            }
        }

        binding.btnLiveMagnifier.setOnClickListener {
            checkAndRequestCameraPermissions {
                if (it) {
                    openActivity(LiveMagnifierScreen::class.java)
                }
            }
        }
        binding.btnSavedImages.setOnClickListener {
            openActivity(SavedImagesScreen::class.java)
        }
    }
}