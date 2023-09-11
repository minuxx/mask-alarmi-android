package com.minux.mask_alarmi.data.remote

data class GetAddressesResponse(
    val addresses: List<AddressItem>,
    val errorMessage: String,
    val meta: Meta,
    val status: String
)

data class Meta(
    val count: Int,
    val page: Int,
    val totalCount: Int
)