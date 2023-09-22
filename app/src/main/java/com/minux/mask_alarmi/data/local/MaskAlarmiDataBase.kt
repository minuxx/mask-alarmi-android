package com.minux.mask_alarmi.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.minux.mask_alarmi.data.local.entity.StoreEntity

const val MASK_ALARMI_DB_NAME = "mask-alarmi-database"

@Database(entities = [ StoreEntity::class ], version = 6)
abstract class MaskAlarmiDataBase : RoomDatabase() {
    abstract fun storeDao(): StoreDao
}