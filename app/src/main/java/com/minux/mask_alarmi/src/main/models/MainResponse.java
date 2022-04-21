package com.minux.mask_alarmi.src.main.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MainResponse {
    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("isSuccess")
    private boolean isSuccess;

    @SerializedName("count")
    private int count;

    @SerializedName("stores")
    private ArrayList<Store> stores = null;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public boolean getIsSuccess() {
        return isSuccess;
    }

    public int getCount() {
        return count;
    }

    public ArrayList<Store> getStores() {
        return stores;
    }
}
