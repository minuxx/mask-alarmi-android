package com.softsquared.android.mask_alarmi.src.main.interfaces;

import com.softsquared.android.mask_alarmi.src.main.models.Store;

import java.util.ArrayList;

public interface MainActivityView {
    void getStoresSuccess(int count, ArrayList<Store> stores);

    void getStoresFailure(String message);
}
