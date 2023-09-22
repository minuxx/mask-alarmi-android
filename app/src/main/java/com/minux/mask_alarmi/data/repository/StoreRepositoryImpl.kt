package com.minux.mask_alarmi.data.repository

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.minux.mask_alarmi.data.local.MASK_ALARMI_DB_NAME
import com.minux.mask_alarmi.data.local.MaskAlarmiDataBase
import com.minux.mask_alarmi.data.local.entity.StoreEntity
import com.minux.mask_alarmi.data.remote.AddressRemoteDataSource
import com.minux.mask_alarmi.domain.model.Address
import com.minux.mask_alarmi.domain.model.Store
import com.minux.mask_alarmi.domain.repository.StoreRepository
import com.minux.mask_alarmi.util.GeoUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

private const val TAG = "StoreRepositoryImpl"

class StoreRepositoryImpl private constructor(private val context: Context) : StoreRepository {
    private val maskAlarmiDB: MaskAlarmiDataBase = Room.databaseBuilder(
        context.applicationContext,
        MaskAlarmiDataBase::class.java,
        MASK_ALARMI_DB_NAME,
    ).build()
    private val storeLocalDataSource = maskAlarmiDB.storeDao()
    private val addressRemoteDataSource = AddressRemoteDataSource(context)

    override fun getStoresByGeo(
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

    override fun searchAddress(
        searchAddr: String,
        lat: Double,
        lng: Double,
        onSuccess: (Address?) -> Unit,
        onFailure: (String) -> Unit
    ) {
        addressRemoteDataSource.getAddresses(
            searchAddr,
            "$lng,$lat",
            onSuccess = { addressDtos ->
                onSuccess(addressDtos.minByOrNull { it.distance }?.toModel())
            },
            onFailure = { errorCode ->
                onFailure(errorCode.message)
            })
    }

    suspend fun insertStoresFromJson() {
        withContext(Dispatchers.IO) {
            val json = loadJsonFromAsset()
            val listType = object : TypeToken<List<StoreEntity>>() {}.type
            val stores: List<StoreEntity> = Gson().fromJson(json, listType)
            storeLocalDataSource.insertStores(stores)
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



