package com.minux.mask_alarmi

import android.app.Application
import com.minux.mask_alarmi.data.repository.StoreRepository

class MaskAlarmiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        StoreRepository.initialize(this)
    }
}