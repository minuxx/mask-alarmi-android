package com.minux.mask_alarmi.data.repository

import com.minux.mask_alarmi.data.models.Address
import com.minux.mask_alarmi.data.models.Store

interface MaskAlarmiRepository {
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