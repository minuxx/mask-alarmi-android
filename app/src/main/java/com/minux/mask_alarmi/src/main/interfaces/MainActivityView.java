package com.minux.mask_alarmi.src.main.interfaces;

import com.minux.mask_alarmi.src.main.models.Juso;
import com.minux.mask_alarmi.src.main.models.Management;
import com.minux.mask_alarmi.src.main.models.Store;

import java.util.ArrayList;

public interface MainActivityView {
    void getStoresByGeoSuccess(int count, ArrayList<Store> stores);

    void getStoresByGeoFailure(String message);

    void getStoresByAddrSuccess(int count, ArrayList<Store> stores);

    void getStoresByAddrFailure(String message);

    void getVersionSuccess(Management management);

    void getVersionFailure(String message);

    void convertAddressSuccess(ArrayList<Juso> jusos);

    void convertAddressFailure(String message);
}
