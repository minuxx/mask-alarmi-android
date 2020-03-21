package com.softsquared.android.mask_alarmi.src.main.interfaces;

import com.softsquared.android.mask_alarmi.src.main.models.AddressResponse;
import com.softsquared.android.mask_alarmi.src.main.models.MainResponse;
import com.softsquared.android.mask_alarmi.src.main.models.ManagementResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MainRetrofitInterface {
    //    @GET("/test")
    @GET("/corona19-masks/v1/storesByGeo/json")
    Call<MainResponse> getStoresByGeo(@Query("lat") final double lat,
                                 @Query("lng") final double lng,
                                 @Query("m") final int m);

    @GET("/corona19-masks/v1/storesByAddr/json")
    Call<MainResponse> getStoresByAddr(@Query("address") final String address);

    @GET("/version")
    Call<ManagementResponse> getVersion();

    @GET("/addrlink/addrLinkApi.do")
    Call<AddressResponse> convertAddress(@Query("confmKey") final String confmKey,
                                         @Query("currentPage") final String currentPage,
                                         @Query("countPerPage") final String countPerPage,
                                         @Query("keyword") final String keyword,
                                         @Query("resultType") final String resultType);
}
