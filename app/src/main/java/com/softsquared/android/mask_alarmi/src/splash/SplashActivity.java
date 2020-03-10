package com.softsquared.android.mask_alarmi.src.splash;

import android.content.Intent;
import android.os.Bundle;

import com.softsquared.android.mask_alarmi.R;
import com.softsquared.android.mask_alarmi.src.BaseActivity;
import com.softsquared.android.mask_alarmi.src.main.MainActivity;
import com.softsquared.android.mask_alarmi.src.main.interfaces.MainRetrofitInterface;
import com.softsquared.android.mask_alarmi.src.main.models.MainResponse;
import com.softsquared.android.mask_alarmi.src.splash.interfaces.SplashActivityView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.softsquared.android.mask_alarmi.src.ApplicationClass.MEDIA_TYPE_JSON;
import static com.softsquared.android.mask_alarmi.src.ApplicationClass.RADIUS_KM;
import static com.softsquared.android.mask_alarmi.src.ApplicationClass.getRetrofit;


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
