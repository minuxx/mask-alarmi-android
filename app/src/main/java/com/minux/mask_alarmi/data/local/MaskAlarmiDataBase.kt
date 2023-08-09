package com.minux.mask_alarmi.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.minux.mask_alarmi.data.model.Store

const val MASK_ALARMI_DB_NAME = "mask-alarmi-database"

@Database(entities = [ Store::class ], version = 4)
abstract class MaskAlarmiDataBase : RoomDatabase() {
    abstract fun storeDao(): StoreDao
}