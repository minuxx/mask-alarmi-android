package com.softsquared.android.mask_alarmi.src.wayfinding;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import com.softsquared.android.mask_alarmi.R;
import com.softsquared.android.mask_alarmi.src.BaseActivity;

import java.net.URISyntaxException;
import java.util.List;

public class WayFindingActivity extends BaseActivity {
    private WebView mWebView;
    private WebSettings mWebSettings;
    private String mUrl = null;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findload);

        if (getIntent() != null) {
            mUrl = getIntent().getStringExtra("way_finding_url");
        }

        mWebView = findViewById(R.id.findload_webview);

        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);

        mUrl.startsWith("intent:");

    }
}
