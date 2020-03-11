package com.softsquared.android.mask_alarmi.src.splash;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.naver.maps.map.MapFragment;
import com.softsquared.android.mask_alarmi.R;
import com.softsquared.android.mask_alarmi.src.BaseActivity;
import com.softsquared.android.mask_alarmi.src.main.MainActivity;
import com.softsquared.android.mask_alarmi.src.splash.interfaces.SplashActivityView;

import java.util.List;


public class SplashActivity extends BaseActivity implements SplashActivityView {
    private static final String TAG = "SPLASH ACT://";

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
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
        public void onPermissionDenied(List<String> deniedPermissions) {
            showCustomToast("앱 설정을 통해 권한을 허용해주세요!");
            finish();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TedPermission.with(SplashActivity.this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("위치 및 GPS 권한 설정을 해주세요.")
                .setPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .check();
    }



    @Override
    public void validateFailure(String message) {

    }

    @Override
    public void validateSuccess(String message) {

    }
}
