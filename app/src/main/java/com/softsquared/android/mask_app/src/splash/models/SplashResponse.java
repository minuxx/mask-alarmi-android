package com.softsquared.android.mask_app.src.splash.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SplashResponse {
    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("isSuccess")
    private boolean isSuccess;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public boolean getIsSuccess() {
        return isSuccess;
    }
}
