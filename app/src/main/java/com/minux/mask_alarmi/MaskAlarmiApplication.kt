package com.minux.mask_alarmi

import android.app.Application
import com.minux.mask_alarmi.data.repository.StoreRepositoryImpl

class MaskAlarmiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        StoreRepositoryImpl.initialize(this)
    }
}