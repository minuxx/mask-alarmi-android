package com.softsquared.android.mask_alarmi.src.main.models;

import com.google.gson.annotations.SerializedName;

public class Store {
    @SerializedName("addr")
    private String addr;
    @SerializedName("code")
    private String code;
    @SerializedName("created_at")
    private String created_at;
    @SerializedName("lat")
    private double lat;
    @SerializedName("lng")
    private double lng;
    @SerializedName("name")
    private String name;
    @SerializedName("remain_stat")
    private String remain_stat;
    @SerializedName("stock_at")
    private String stock_at;
    @SerializedName("type")
    private String type;

    public String getAddr() {
        return addr;
    }

    public String getCode() {
        return code;
    }

    public String getCreated_at() {
        return created_at;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getName() {
        return name;
    }

    public String getRemain_stat() {
        return remain_stat;
    }

    public String getStock_at() {
        return stock_at;
    }

    public String getType() {
        return type;
    }
}
