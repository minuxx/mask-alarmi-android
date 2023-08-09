package com.minux.mask_alarmi.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.minux.mask_alarmi.data.local.MASK_ALARMI_DB_NAME
import com.minux.mask_alarmi.data.local.MaskAlarmiDataBase
import com.minux.mask_alarmi.data.model.Store

class StoreRepository private constructor(context: Context){
    private val maskAlarmiDB: MaskAlarmiDataBase = Room.databaseBuilder(
        context.applicationContext,
        MaskAlarmiDataBase::class.java,
        MASK_ALARMI_DB_NAME,
    ).build()

    private val storeDao = maskAlarmiDB.storeDao()

    fun getStoresByGeo(lat: Double, lng: Double, m: Int): LiveData<List<Store>> = storeDao.getStoresByGeo(lat, lng, m)
    /*
    * 1. 반경 1km 이내의 stores
    * 2. 필요한 데이터만을, 그리고 변수명 변경
    * */

    companion object {
        private var INSTANCE: StoreRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = StoreRepository(context)
            }
        }

        fun get(): StoreRepository {
            return INSTANCE ?: throw IllegalStateException("StoreRepository must be initialized")
        }
    }
}