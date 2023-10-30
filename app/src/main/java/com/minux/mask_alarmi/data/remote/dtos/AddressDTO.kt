package com.minux.mask_alarmi.data.remote.dtos

import com.minux.mask_alarmi.data.models.Address

data class AddressDTO(
    val addressElements: List<AddressElementDTO>,
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

data class AddressElementDTO(
    val code: String,
    val longName: String,
    val shortName: String,
    val types: List<String>
)