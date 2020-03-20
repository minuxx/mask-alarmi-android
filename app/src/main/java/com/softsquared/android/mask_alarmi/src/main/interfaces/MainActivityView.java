package com.softsquared.android.mask_alarmi.src.main.interfaces;

import com.softsquared.android.mask_alarmi.src.main.models.Store;

import java.util.ArrayList;

public interface MainActivityView {
    void getStoresByGeoSuccess(int count, ArrayList<Store> stores);

    void getStoresByGeoFailure(String message);

    void getStoresByAddrSuccess(int count, ArrayList<Store> stores);

    void getStoresByAddrFailure(String message);
}
