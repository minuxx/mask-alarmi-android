package com.softsquared.android.mask_alarmi.src.main.interfaces;

import android.location.Address;

import com.softsquared.android.mask_alarmi.src.main.models.Juso;
import com.softsquared.android.mask_alarmi.src.main.models.Management;
import com.softsquared.android.mask_alarmi.src.main.models.Store;

import java.util.ArrayList;

public interface MainActivityView {
    void getStoresByGeoSuccess(int count, ArrayList<Store> stores);

    void getStoresByGeoFailure(String message);

    void getStoresByAddrSuccess(int count, ArrayList<Store> stores);

    void getStoresByAddrFailure(String message);

    void forceUpdateSuccess(Management management);

    void forceUpdateFailure(String message);

    void convertAddressSuccess(ArrayList<Juso> jusos);

    void convertAddressFailure(String message);
}
