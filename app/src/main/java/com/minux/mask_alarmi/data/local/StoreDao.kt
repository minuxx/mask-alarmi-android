package com.minux.mask_alarmi.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.minux.mask_alarmi.data.model.Store

@Dao
interface StoreDao {
//    @Query("SELECT * FROM store " +
//            "WHERE (6371 * " +
//            "acos(cos(radians(:lat)) * cos(radians(lat)) * " +
//            "cos(radians(lng) - radians(:lng)) + " +
//            "sin(radians(:lat)) * sin(radians(lat)))) <= :m")
//    fun getStoresByGeo(lat: Double, lng: Double, m: Int): LiveData<List<Store>>

    @Query("SELECT * FROM store")
    fun getStoresByGeo(): LiveData<List<Store>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertStores(pharmacies: List<Store>)
}