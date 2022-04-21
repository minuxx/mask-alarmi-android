package com.minux.mask_alarmi.src.splash.models;

import com.google.gson.annotations.SerializedName;

public class Jwt {
    @SerializedName("jwt")
    private String jwt;

    public String getJwt() {
        return jwt;
    }
}