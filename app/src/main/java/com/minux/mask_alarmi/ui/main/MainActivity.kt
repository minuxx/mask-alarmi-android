package com.minux.mask_alarmi.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.minux.mask_alarmi.R
import com.minux.mask_alarmi.ui.main.map.MapFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment = supportFragmentManager.findFragmentById(R.id.main_fragment_container)
        if (fragment == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.main_fragment_container, MapFragment.newInstance())
                .commit()
        }
    }
}