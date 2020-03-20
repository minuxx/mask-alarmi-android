package com.softsquared.android.mask_alarmi.src.main;

import com.softsquared.android.mask_alarmi.src.main.interfaces.MainActivityView;
import com.softsquared.android.mask_alarmi.src.main.interfaces.MainRetrofitInterface;
import com.softsquared.android.mask_alarmi.src.main.models.MainResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.softsquared.android.mask_alarmi.src.ApplicationClass.getRetrofit;

class MainService {
    private final MainActivityView mMainActivityView;

    MainService(final MainActivityView mainActivityView) {
        this.mMainActivityView = mainActivityView;
    }

    void getStoresByGeo(double lat, double lng, int m) {
        final MainRetrofitInterface mainRetrofitInterface = getRetrofit(true).create(MainRetrofitInterface.class);

        mainRetrofitInterface.getStoresByGeo(lat, lng, m).enqueue(new Callback<MainResponse>() {
            @Override
            public void onResponse(Call<MainResponse> call, Response<MainResponse> response) {
                final MainResponse mainResponse = response.body();
                if (mainResponse == null) {
                    mMainActivityView.getStoresByGeoFailure(null);
                    return;
                }else{
                    mMainActivityView.getStoresByGeoSuccess(mainResponse.getCount(), mainResponse.getStores());
                }
            }

            @Override
            public void onFailure(Call<MainResponse> call, Throwable t) {
                mMainActivityView.getStoresByGeoFailure("onResponse failed");
            }
        });
    }

    void getStoresByAddr(String address) {
        final MainRetrofitInterface mainRetrofitInterface = getRetrofit(true).create(MainRetrofitInterface.class);

        mainRetrofitInterface.getStoresByAddr(address).enqueue(new Callback<MainResponse>() {
            @Override
            public void onResponse(Call<MainResponse> call, Response<MainResponse> response) {
                final MainResponse mainResponse = response.body();
                if (mainResponse == null) {
                    mMainActivityView.getStoresByAddrFailure(null);
                    return;
                }else{
                    mMainActivityView.getStoresByAddrSuccess(mainResponse.getCount(), mainResponse.getStores());
                }
            }

            @Override
            public void onFailure(Call<MainResponse> call, Throwable t) {
                mMainActivityView.getStoresByAddrFailure("onResponse failed");
            }
        });
    }

}
