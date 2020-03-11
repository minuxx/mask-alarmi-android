package com.softsquared.android.mask_alarmi.src.splash;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.softsquared.android.mask_alarmi.R;
import com.softsquared.android.mask_alarmi.src.BaseActivity;
import com.softsquared.android.mask_alarmi.src.main.MainActivity;
import com.softsquared.android.mask_alarmi.src.splash.interfaces.SplashActivityView;

import java.util.List;


public class SplashActivity extends BaseActivity implements SplashActivityView {
    private static final String TAG = "SPLASH ACT://";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread splashThread;

        splashThread = new Thread() {
            @Override
            public void run() {
                synchronized (this) {
                    try {
                        wait(1000);

                        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        splashThread.start();
    }



    @Override
    public void validateFailure(String message) {

    }

    @Override
    public void validateSuccess(String message) {

    }
}
