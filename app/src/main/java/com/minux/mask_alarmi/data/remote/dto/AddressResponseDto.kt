package com.minux.mask_alarmi.data.remote.dto

data class GetAddressesResponse(
    val addresses: List<AddressDto>,
    val errorMessage: String,
    val meta: MetaDto,
    val status: String
)

data class MetaDto(
    val count: Int,
    val page: Int,
    val totalCount: Int
)