package com.minux.mask_alarmi.src.main.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Address {
    @SerializedName("common")
    private Common common;
    @SerializedName("juso")
    private ArrayList<Juso> juso;

    public Common getCommon() {
        return common;
    }

    public ArrayList<Juso> getJuso() {
        return juso;
    }
}
