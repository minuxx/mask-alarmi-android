package com.minux.mask_alarmi.data.repository

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.minux.mask_alarmi.data.local.MASK_ALARMI_DB_NAME
import com.minux.mask_alarmi.data.local.StoreLocalDataSource
import com.minux.mask_alarmi.data.local.entities.StoreEntity
import com.minux.mask_alarmi.data.models.Address
import com.minux.mask_alarmi.data.models.Store
import com.minux.mask_alarmi.data.remote.AddressRemoteDataSource
import com.minux.mask_alarmi.util.GeoUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

private const val TAG = "StoreRepositoryImpl"

class MaskAlarmiRepository private constructor(private val context: Context) {
    private val maskAlarmiDB: StoreLocalDataSource = Room.databaseBuilder(
        context.applicationContext,
        StoreLocalDataSource::class.java,
        MASK_ALARMI_DB_NAME,
    ).build()
    private val storeLocalDataSource = maskAlarmiDB.storeDao()
    private val addressRemoteDataSource = AddressRemoteDataSource(context)

    fun getStoresByGeo(
        lat: Double,
        lng: Double, m: Int,
        onSuccess: (List<Store>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val stores = storeLocalDataSource.getStoresByGeo()
                val storesInRadius = stores.filter { store ->
                    GeoUtil.calculateHaversine(lat, lng, store.lat, store.lng) <= m.toDouble()
                }.map { it.toModel() }

                onSuccess(storesInRadius)
            } catch (e: Exception) {
                onFailure("")
            }
        }
    }

    fun searchAddress(
        searchAddr: String,
        lat: Double,
        lng: Double,
        onSuccess: (Address?) -> Unit,
        onFailure: (String) -> Unit
    ) {
        addressRemoteDataSource.getAddresses(
            searchAddr,
            "$lng,$lat",
            onSuccess = { addressDTOs ->
                onSuccess(addressDTOs.minByOrNull { it.distance }?.toModel())
            },
            onFailure = { errorCode ->
                onFailure(errorCode.message)
            })
    }

    // 더미데이터 추가용 메서드
    suspend fun insertStoresFromJson() {
        withContext(Dispatchers.IO) {
            val json = loadJsonFromAsset()
            val listType = object : TypeToken<List<StoreEntity>>() {}.type
            val stores: List<StoreEntity> = Gson().fromJson(json, listType)
            storeLocalDataSource.insertStores(stores)
        }
    }

    // 더미데이터 추가용 메서드
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

    companion object {
        private var INSTANCE: MaskAlarmiRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = MaskAlarmiRepository(context)
            }
        }

        fun get(): MaskAlarmiRepository {
            return INSTANCE ?: throw IllegalStateException("StoreRepository must be initialized")
        }
    }
}



