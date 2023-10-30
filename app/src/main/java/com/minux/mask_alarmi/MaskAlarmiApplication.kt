package com.minux.mask_alarmi

import android.app.Application
import com.google.firebase.FirebaseApp
import com.minux.mask_alarmi.data.repository.MaskAlarmiRepository

class MaskAlarmiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MaskAlarmiRepository.initialize(this)
        FirebaseApp.initializeApp(this)
    }
}