package com.minux.mask_alarmi.data.remote

data class AddressItem(
    val addressElements: List<AddressElement>,
    val distance: Double,
    val englishAddress: String,
    val jibunAddress: String,
    val roadAddress: String,
    val x: String,
    val y: String
)

data class AddressElement(
    val code: String,
    val longName: String,
    val shortName: String,
    val types: List<String>
)