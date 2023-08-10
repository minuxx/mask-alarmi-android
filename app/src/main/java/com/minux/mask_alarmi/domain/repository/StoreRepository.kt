package com.minux.mask_alarmi.domain.repository

import androidx.lifecycle.LiveData
import com.minux.mask_alarmi.domain.model.Store


interface StoreRepository {
    fun getStoresByGeo(): LiveData<List<Store>>
}