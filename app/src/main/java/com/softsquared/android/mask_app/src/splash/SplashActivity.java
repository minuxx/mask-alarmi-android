package com.softsquared.android.mask_app.src.splash;

import android.content.Intent;
import android.os.Bundle;

import com.softsquared.android.mask_app.R;
import com.softsquared.android.mask_app.src.BaseActivity;
import com.softsquared.android.mask_app.src.splash.interfaces.SplashActivityView;


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
                try {
                    synchronized (this) {
                        // Wait given period of time or exit on touch
                        wait(1000);

                        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        finish();
                    }
                } catch (InterruptedException ignored) {

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
