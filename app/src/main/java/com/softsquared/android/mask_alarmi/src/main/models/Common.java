package com.softsquared.android.mask_alarmi.src.main.models;

import com.google.gson.annotations.SerializedName;

public class Common {
    @SerializedName("errorMessage")
    private String errorMessage;
    @SerializedName("countPerPage")
    private String countPerPage;
    @SerializedName("totalCount")
    private String totalCount;
    @SerializedName("errorCode")
    private String errorCode;
    @SerializedName("currentPage")
    private String currentPage;


    public String getErrorMessage() {
        return errorMessage;
    }

    public String getCountPerPage() {
        return countPerPage;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getCurrentPage() {
        return currentPage;
    }
}
