package com.minux.mask_alarmi.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.room.Room
import com.minux.mask_alarmi.data.local.MASK_ALARMI_DB_NAME
import com.minux.mask_alarmi.data.local.MaskAlarmiDataBase
import com.minux.mask_alarmi.domain.model.Store
import com.minux.mask_alarmi.domain.repository.StoreRepository


class StoreRepositoryImpl private constructor(val context: Context) : StoreRepository {
    private val maskAlarmiDB: MaskAlarmiDataBase = Room.databaseBuilder(
        context.applicationContext,
        MaskAlarmiDataBase::class.java,
        MASK_ALARMI_DB_NAME,
    ).build()

    private val storeDao = maskAlarmiDB.storeDao()

    /*
    * 1. 반경 1km 이내의 stores
    * 2. 필요한 데이터만을, 그리고 변수명 변경
    */
    override fun getStoresByGeo(): LiveData<List<Store>> {
        val stores = storeDao.getStoresByGeo()
        val storeListLiveData = MediatorLiveData<List<Store>>()

        storeListLiveData.addSource(stores) { stores ->
            storeListLiveData.value = stores.map { it.toModel() }
        }

        return storeListLiveData
    }

    companion object {
        private var INSTANCE: StoreRepositoryImpl? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = StoreRepositoryImpl(context)
            }
        }

        fun get(): StoreRepositoryImpl {
            return INSTANCE ?: throw IllegalStateException("StoreRepository must be initialized")
        }
    }
}


/*
suspend fun insertStoresFromJson() {
    withContext(Dispatchers.IO) {
        val json = loadJsonFromAsset()
        val listType = object : TypeToken<List<StoreEntity>>() {}.type
        val stores: List<StoreEntity> = Gson().fromJson(json, listType)
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
}*/
