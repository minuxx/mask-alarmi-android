package com.softsquared.android.mask_alarmi.src.main.models;

import com.google.gson.annotations.SerializedName;

public class Management {
    @SerializedName("versionName")
    private String versionName;
    @SerializedName("isForce")
    private String isForce;
    @SerializedName("status")
    private String status;
    @SerializedName("contents")
    private String contents;

    public String getVersionName() {
        return versionName;
    }

    public String getIsForce() {
        return isForce;
    }

    public String getStatus() {
        return status;
    }

    public String getContents() {
        return contents;
    }
}
