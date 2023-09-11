package com.minux.mask_alarmi.data.remote

import com.minux.mask_alarmi.domain.model.Address
import com.minux.mask_alarmi.domain.model.Store

data class AddressItem(
    val addressElements: List<AddressElement>,
    val distance: Double,
    val englishAddress: String,
    val jibunAddress: String,
    val roadAddress: String,
    val x: String,
    val y: String
) {
    fun toModel(): Address = Address(
        latitude = y.toDouble(),
        longitude = x.toDouble()
    )
}

data class AddressElement(
    val code: String,
    val longName: String,
    val shortName: String,
    val types: List<String>
)