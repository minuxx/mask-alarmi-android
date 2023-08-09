package com.minux.mask_alarmi.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.minux.mask_alarmi.data.local.MASK_ALARMI_DB_NAME
import com.minux.mask_alarmi.data.local.MaskAlarmiDataBase
import com.minux.mask_alarmi.data.model.Store
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

class StoreRepository private constructor(val context: Context){
    private val maskAlarmiDB: MaskAlarmiDataBase = Room.databaseBuilder(
        context.applicationContext,
        MaskAlarmiDataBase::class.java,
        MASK_ALARMI_DB_NAME,
    ).build()

    private val storeDao = maskAlarmiDB.storeDao()

//    fun getStoresByGeo(lat: Double, lng: Double, m: Int): LiveData<List<Store>> = storeDao.getStoresByGeo(lat, lng, m)
    fun getStoresByGeo(): LiveData<List<Store>> = storeDao.getStoresByGeo()

    suspend fun insertStoresFromJson() {
        withContext(Dispatchers.IO) {
            val json = loadJsonFromAsset()
            val listType = object : TypeToken<List<Store>>() {}.type
            val stores: List<Store> = Gson().fromJson(json, listType)
            storeDao.insertStores(stores)
        }
    }

    private fun loadJsonFromAsset(): String? {
        var json: String? = null
        try {
            val inputStream: InputStream = context.assets.open("sample_data.json")
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = buffer.toString(Charset.defaultCharset())
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return json
    }

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