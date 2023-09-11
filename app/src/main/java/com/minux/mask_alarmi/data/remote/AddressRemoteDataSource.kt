package com.minux.mask_alarmi.data.remote

import android.content.Context
import android.util.Log
import com.minux.mask_alarmi.R
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "MaskAlarmiRemoteDataSource"
private const val NAVER_GEOCODE_BASE_URL = "https://naveropenapi.apigw.ntruss.com/"
private const val NAVER_API_KEY_ID = "X-NCP-APIGW-API-KEY-ID"
private const val NAVER_API_KEY = "X-NCP-APIGW-API-KEY"

class AddressRemoteDataSource(private val context: Context) {
    private val addressApi: AddressApi

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(NAVER_GEOCODE_BASE_URL)
            .client(provideOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        addressApi = retrofit.create(AddressApi::class.java)
    }

    private fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }

        val networkInterceptor = Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header(NAVER_API_KEY_ID, context.getString(R.string.naver_client_id))
                .header(NAVER_API_KEY, context.getString(R.string.naver_client_secret))
                .header("Accept", "application/json")
            val request = requestBuilder.build()
            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(networkInterceptor)
            .build()
    }

    fun getAddresses(
        query: String,
        coordinate: String,
        onResponse: (List<AddressItem>) -> Unit,
    ) {
        val getAddressesRequest: Call<GetAddressesResponse> = addressApi.getAddresses(
            query,
            coordinate
        )

        getAddressesRequest.enqueue(object : Callback<GetAddressesResponse> {
            override fun onFailure(call: Call<GetAddressesResponse>, t: Throwable) {
                Log.e(TAG, "Failed to fetch addresses", t)
            }

            override fun onResponse(
                call: Call<GetAddressesResponse>,
                response: Response<GetAddressesResponse>
            ) {
                Log.d(TAG, "Response received")
                val getAddressesResponse: GetAddressesResponse? = response.body()
                val addressItems: List<AddressItem> = getAddressesResponse?.addresses
                    ?: mutableListOf()
                onResponse(addressItems)
            }
        })
    }
}