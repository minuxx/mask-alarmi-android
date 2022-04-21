package com.minux.mask_alarmi.src.main;

import com.minux.mask_alarmi.src.main.interfaces.MainActivityView;
import com.minux.mask_alarmi.src.main.interfaces.MainRetrofitInterface;
import com.minux.mask_alarmi.src.main.models.AddressResponse;
import com.minux.mask_alarmi.src.main.models.MainResponse;
import com.minux.mask_alarmi.src.main.models.ManagementResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.minux.mask_alarmi.src.ApplicationClass.SEARCH_CONFIRM_KEY;
import static com.minux.mask_alarmi.src.ApplicationClass.getPublicJusoRetrofit;
import static com.minux.mask_alarmi.src.ApplicationClass.getPublicMaskRetrofit;
import static com.minux.mask_alarmi.src.ApplicationClass.getRetrofit;


class MainService {
    private final MainActivityView mMainActivityView;

    MainService(final MainActivityView mainActivityView) {
        this.mMainActivityView = mainActivityView;
    }

    void getStoresByGeo(double lat, double lng, int m) {
        final MainRetrofitInterface mainRetrofitInterface = getPublicMaskRetrofit().create(MainRetrofitInterface.class);

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
        final MainRetrofitInterface mainRetrofitInterface = getPublicMaskRetrofit().create(MainRetrofitInterface.class);

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


    void getVersion() {
        final MainRetrofitInterface mainRetrofitInterface = getRetrofit().create(MainRetrofitInterface.class);

        mainRetrofitInterface.getVersion().enqueue(new Callback<ManagementResponse>() {
            @Override
            public void onResponse(Call<ManagementResponse> call, Response<ManagementResponse> response) {
                final ManagementResponse managementResponse = response.body();
                if (managementResponse == null) {
                    mMainActivityView.getVersionFailure(null);
                    return;
                }else if(managementResponse.getCode() == 200){
                    mMainActivityView.getVersionSuccess(managementResponse.getManagement());
                }else{
                    mMainActivityView.getVersionFailure(managementResponse.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ManagementResponse> call, Throwable t) {
                mMainActivityView.getVersionFailure("onResponse failed");
            }
        });
    }

    void convertAddress(String keyword) {
        final MainRetrofitInterface mainRetrofitInterface = getPublicJusoRetrofit().create(MainRetrofitInterface.class);

        mainRetrofitInterface.convertAddress(SEARCH_CONFIRM_KEY, "1", "10", keyword, "json").enqueue(new Callback<AddressResponse>() {
            @Override
            public void onResponse(Call<AddressResponse> call, Response<AddressResponse> response) {
                final AddressResponse addressResponse = response.body();
                if (addressResponse == null) {
                    mMainActivityView.convertAddressFailure(null);
                    return;
                }else if(addressResponse.getAddress().getCommon().getErrorCode().equals("0")){
                    mMainActivityView.convertAddressSuccess(addressResponse.getAddress().getJuso());
                }else if(addressResponse.getAddress().getCommon().getErrorCode().equals("E0006")){
                    mMainActivityView.convertAddressFailure(addressResponse.getAddress().getCommon().getErrorMessage());
                }else{
                    mMainActivityView.convertAddressFailure(addressResponse.getAddress().getCommon().getErrorMessage());
                }
            }

            @Override
            public void onFailure(Call<AddressResponse> call, Throwable t) {
                mMainActivityView.convertAddressFailure("onResponse failed");
            }
        });
    }
}
