package com.minux.mask_alarmi.data.remote

import com.minux.mask_alarmi.data.remote.dto.GetAddressesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface AddressApi {
    @GET("map-geocode/v2/geocode")
    fun getAddresses(@Query("query") query: String, @Query("coordinate") coordinate: String): Call<GetAddressesResponse>
}