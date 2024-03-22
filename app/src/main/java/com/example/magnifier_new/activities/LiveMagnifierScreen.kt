package com.example.magnifier_new.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.magnifier_new.R
import com.example.magnifier_new.databinding.LiveMagnifierScreenBinding
import com.example.magnifier_new.fragment.CameraXFragment

class LiveMagnifierScreen : AppCompatActivity() {

    val binding: LiveMagnifierScreenBinding by lazy{
        LiveMagnifierScreenBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        val fragmentmanager = supportFragmentManager
        var fragment = fragmentmanager.findFragmentById(R.id.fragment_container)
        if (fragment == null) {
            fragment = CameraXFragment()
            fragmentmanager.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }

    override fun onBackPressed() {
        finish()
    }
}
