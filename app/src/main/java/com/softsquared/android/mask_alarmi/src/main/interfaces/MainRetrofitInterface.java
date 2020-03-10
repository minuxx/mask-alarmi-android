package com.softsquared.android.mask_alarmi.src.main.interfaces;

import com.softsquared.android.mask_alarmi.src.main.models.MainResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MainRetrofitInterface {
    //    @GET("/test")
    @GET("/corona19-masks/v1/storesByGeo/json")
    Call<MainResponse> getStores(@Query("lat") final double lat,
                                 @Query("lng") final double lng,
                                 @Query("m") final int m);


}
