package com.minux.mask_alarmi.data.remote.dtos

data class GetAddressesResponse(
    val addresses: List<AddressDTO>,
    val errorMessage: String,
    val meta: MetaDTO,
    val status: String
)

data class MetaDTO(
    val count: Int,
    val page: Int,
    val totalCount: Int
)