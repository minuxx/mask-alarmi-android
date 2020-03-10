package com.softsquared.android.mask_alarmi.src.splash.interfaces;


import com.softsquared.android.mask_alarmi.src.splash.models.SplashResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface SplashRetrofitInterface {
    @GET("/signIn")
    Call<SplashResponse> autoLogin();

}
