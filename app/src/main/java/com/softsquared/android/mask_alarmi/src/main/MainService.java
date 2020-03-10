package com.softsquared.android.mask_alarmi.src.main;

import com.softsquared.android.mask_alarmi.src.main.interfaces.MainActivityView;
import com.softsquared.android.mask_alarmi.src.main.interfaces.MainRetrofitInterface;
import com.softsquared.android.mask_alarmi.src.main.models.MainResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.softsquared.android.mask_alarmi.src.ApplicationClass.getRetrofit;

class MainService {
    private final MainActivityView mMainActivityView;

    MainService(final MainActivityView mainActivityView) {
        this.mMainActivityView = mainActivityView;
    }

    void getStores(double lat, double lng, int m) {
        final MainRetrofitInterface mainRetrofitInterface = getRetrofit().create(MainRetrofitInterface.class);

        mainRetrofitInterface.getStores(lat, lng, m).enqueue(new Callback<MainResponse>() {
            @Override
            public void onResponse(Call<MainResponse> call, Response<MainResponse> response) {
                final MainResponse mainResponse = response.body();
                if (mainResponse == null) {
                    mMainActivityView.getStoresFailure(null);
                    return;
                }else{
                    mMainActivityView.getStoresSuccess(mainResponse.getCount(), mainResponse.getStores());
                }
            }

            @Override
            public void onFailure(Call<MainResponse> call, Throwable t) {
                mMainActivityView.getStoresFailure("onResponse failed");
            }
        });
    }

}
