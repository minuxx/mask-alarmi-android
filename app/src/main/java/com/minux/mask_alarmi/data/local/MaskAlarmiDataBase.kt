package com.minux.mask_alarmi.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.minux.mask_alarmi.data.model.Store

@Database(entities = [ Store::class ], version = 1)
abstract class MaskAlarmiDataBase : RoomDatabase() {
    abstract fun storeDao(): StoreDao
}