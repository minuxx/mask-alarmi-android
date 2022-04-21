package com.minux.mask_alarmi.src.main.models;

import com.google.gson.annotations.SerializedName;

public class Management {
    @SerializedName("version")
    private String version;
    @SerializedName("isForce")
    private String isForce;
    @SerializedName("status")
    private String status;
    @SerializedName("contents")
    private String contents;

    public String getVersion() {
        return version;
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
