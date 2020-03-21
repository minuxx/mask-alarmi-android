package com.softsquared.android.mask_alarmi.src.splash;


import com.softsquared.android.mask_alarmi.src.splash.interfaces.SplashActivityView;
import com.softsquared.android.mask_alarmi.src.splash.interfaces.SplashRetrofitInterface;
import com.softsquared.android.mask_alarmi.src.splash.models.SplashResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.softsquared.android.mask_alarmi.src.ApplicationClass.getRetrofit;

class SplashService {
    private final SplashActivityView mSplashActivityView;

    SplashService(final SplashActivityView splashActivityView) {
        this.mSplashActivityView = splashActivityView;
    }

    void autoLogin() {
        final SplashRetrofitInterface splashRetrofitInterface = getRetrofit().create(SplashRetrofitInterface.class);
        splashRetrofitInterface.autoLogin().enqueue(new Callback<SplashResponse>() {
            @Override
            public void onResponse(Call<SplashResponse> call, Response<SplashResponse> response) {
                final SplashResponse splashResponse = response.body();
                if (splashResponse == null) {
                    mSplashActivityView.validateFailure(null);
                }else if(splashResponse.getCode() == 100)
                    mSplashActivityView.validateSuccess(splashResponse.getMessage());
                else
                    mSplashActivityView.validateFailure(splashResponse.getMessage());
            }

            @Override
            public void onFailure(Call<SplashResponse> call, Throwable t) {
                mSplashActivityView.validateFailure(null);
            }
        });
    }

}
