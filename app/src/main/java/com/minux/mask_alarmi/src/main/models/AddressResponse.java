package com.minux.mask_alarmi.src.main.models;

import com.google.gson.annotations.SerializedName;

public class AddressResponse {
    @SerializedName("results")
    private Address address;

    public Address getAddress() {
        return address;
    }
}
