package com.minux.mask_alarmi.src.main.models;

import com.google.gson.annotations.SerializedName;

public class ManagementResponse {
    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("isSuccess")
    private boolean isSuccess;

    @SerializedName("result")
    private Management management;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public boolean getIsSuccess() {
        return isSuccess;
    }

    public Management getManagement() { return  management; }
}
