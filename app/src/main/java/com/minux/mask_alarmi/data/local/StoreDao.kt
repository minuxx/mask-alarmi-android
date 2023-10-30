package com.minux.mask_alarmi.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.minux.mask_alarmi.data.local.entities.StoreEntity

@Dao
interface StoreDao {
    @Query("SELECT * FROM stores")
    fun getStoresByGeo(): List<StoreEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStores(storeEntities: List<StoreEntity>)
}