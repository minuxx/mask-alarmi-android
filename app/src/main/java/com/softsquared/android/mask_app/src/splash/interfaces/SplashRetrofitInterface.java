package com.softsquared.android.mask_app.src.splash.interfaces;


import com.softsquared.android.mask_app.src.splash.models.SplashResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface SplashRetrofitInterface {
    @GET("/signIn")
    Call<SplashResponse> autoLogin();

}
