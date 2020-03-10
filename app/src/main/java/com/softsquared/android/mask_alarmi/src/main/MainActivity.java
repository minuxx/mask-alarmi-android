package com.softsquared.android.mask_alarmi.src.main;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.softsquared.android.mask_alarmi.R;
import com.softsquared.android.mask_alarmi.src.BaseActivity;
import com.softsquared.android.mask_alarmi.src.main.interfaces.MainActivityView;
import com.softsquared.android.mask_alarmi.src.main.models.Store;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static com.softsquared.android.mask_alarmi.src.ApplicationClass.RADIUS_KM;


public class MainActivity extends BaseActivity implements MainActivityView, OnMapReadyCallback {
    private MapFragment mFgMap;
    private NaverMap mNaverMap;

    private GpsTracker gpsTracker;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};


    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            showCustomToast("Permission Granted");
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            showCustomToast("Permission Denied");
        }


    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("위치 및 GPS 권한 설정을 해주세요.")
                .setPermissions(
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .check();

        FragmentManager fm = getSupportFragmentManager();
        mFgMap = (MapFragment)fm.findFragmentById(R.id.main_fg_map);

        if (mFgMap != null) {
            mFgMap.getMapAsync(this);
        }
//        gpsTracker = new GpsTracker(MainActivity.this);

    }

    private void setMarkers(ArrayList<Store> stores){
        for(Store store : stores){
            Marker marker = new Marker();
            marker.setPosition(new LatLng(store.getLat(), store.getLng()));
            marker.setMap(mNaverMap);
        }
    }

    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        mNaverMap = naverMap;

        getStores(37.460187, 126.730307, RADIUS_KM);
    }

    /*---------
    API Communication*/
    private void getStores(double lat, double lng, int m){
        MainService mainService = new MainService(this);
        mainService.getStores(lat, lng, m);
    }

    @Override
    public void getStoresSuccess(int count, ArrayList<Store> stores) {
        setMarkers(stores);
    }

    @Override
    public void getStoresFailure(String message) {
        showCustomToast("Failed!");
    }
    /*---------
    API Communication End*/
}
