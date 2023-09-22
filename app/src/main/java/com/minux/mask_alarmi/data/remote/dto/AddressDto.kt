package com.minux.mask_alarmi.data.remote.dto

import com.minux.mask_alarmi.domain.model.Address

data class AddressDto(
    val addressElements: List<AddressElementDto>,
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

data class AddressElementDto(
    val code: String,
    val longName: String,
    val shortName: String,
    val types: List<String>
)