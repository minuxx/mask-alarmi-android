package com.softsquared.android.mask_app.src.splash;


import com.softsquared.android.tablinx.src.splash.interfaces.SplashActivityView;
import com.softsquared.android.tablinx.src.splash.interfaces.SplashRetrofitInterface;
import com.softsquared.android.tablinx.src.splash.models.SplashResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.softsquared.android.tablinx.src.ApplicationClass.getRetrofit;

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
                    mSplashActivityView.autoLoginFailure(null);
                }else if(splashResponse.getCode() == 100)
                    mSplashActivityView.autoLoginSuccess(splashResponse.getMessage());
                else
                    mSplashActivityView.autoLoginFailure(splashResponse.getMessage());
            }

            @Override
            public void onFailure(Call<SplashResponse> call, Throwable t) {
                mSplashActivityView.autoLoginFailure(null);
            }
        });
    }

    void getYouTubeLink()  {
        final SplashRetrofitInterface splashRetrofitInterface = getRetrofit().create(SplashRetrofitInterface.class);
        splashRetrofitInterface.getYouTubeLink().enqueue(new Callback<SplashResponse>() {
            @Override
            public void onResponse(Call<SplashResponse> call, Response<SplashResponse> response) {
                final SplashResponse splashResponse = response.body();
                if (splashResponse == null) {
                    mSplashActivityView.getYouTubeLinkFailure(null);
                } else if (splashResponse.getCode() == 100) {
                    mSplashActivityView.getYouTubeLinkSuccess(splashResponse.getResult());
                }else{
                    mSplashActivityView.getYouTubeLinkFailure(splashResponse.getMessage());
                }
            }

            @Override
            public void onFailure(Call<SplashResponse> call, Throwable t) {
                mSplashActivityView.getYouTubeLinkFailure(null);
            }
        });
    }
}
