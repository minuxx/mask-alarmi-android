package com.softsquared.android.mask_app.src.splash.models;

import com.google.gson.annotations.SerializedName;

public class Jwt {
    @SerializedName("jwt")
    private String jwt;

    public String getJwt() {
        return jwt;
    }
}