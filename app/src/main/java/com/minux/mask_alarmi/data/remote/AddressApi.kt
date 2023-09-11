package com.minux.mask_alarmi.data.remote

import retrofit2.Call
import retrofit2.http.GET

interface AddressApi {
    @GET("/")
    fun searchAddress(): Call<SearchAddressResponse>
}