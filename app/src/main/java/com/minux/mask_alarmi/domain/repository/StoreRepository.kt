package com.minux.mask_alarmi.domain.repository

import com.minux.mask_alarmi.domain.model.Address
import com.minux.mask_alarmi.domain.model.Store

interface StoreRepository {
    fun getStoresByGeo(
        lat: Double,
        lng: Double,
        m: Int,
        onSuccess: (List<Store>) -> Unit,
        onFailure: (String) -> Unit
    )

    fun searchAddress(
        searchAddr: String,
        lat: Double,
        lng: Double,
        onSuccess: (Address?) -> Unit,
        onFailure: (String) -> Unit
    )
}