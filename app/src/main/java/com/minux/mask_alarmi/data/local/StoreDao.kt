package com.minux.mask_alarmi.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.minux.mask_alarmi.data.model.Store

@Dao
interface StoreDao {
    @Query("SELECT * FROM store")
    fun getStoresByGeo(): LiveData<List<Store>>
}