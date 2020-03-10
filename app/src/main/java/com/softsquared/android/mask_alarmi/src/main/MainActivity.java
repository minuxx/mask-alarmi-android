package com.softsquared.android.mask_alarmi.src.main;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.fragment.app.FragmentManager;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;
import com.softsquared.android.mask_alarmi.R;
import com.softsquared.android.mask_alarmi.src.BaseActivity;
import com.softsquared.android.mask_alarmi.src.main.interfaces.MainActivityView;
import com.softsquared.android.mask_alarmi.src.main.models.Store;

import java.util.ArrayList;
import java.util.List;

import static com.softsquared.android.mask_alarmi.src.ApplicationClass.RADIUS;


public class MainActivity extends BaseActivity implements MainActivityView, OnMapReadyCallback {
    private MapFragment mFgMap;
    private NaverMap mNaverMap;

    private GpsTracker gpsTracker;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource mLocationSource;

    private ArrayList<Marker> mMarkers = null;


    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            removeAllMarkers();
            getStores(gpsTracker.getLatitude(), gpsTracker.getLongitude(), RADIUS);
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            showCustomToast("앱 설정을 통해 권한을 허용해주세요!");
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
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .check();

        FragmentManager fm = getSupportFragmentManager();

        mFgMap = (MapFragment)fm.findFragmentById(R.id.main_fg_map);
        mMarkers = new ArrayList<>();

        if (mFgMap != null) {
            mFgMap.getMapAsync(this);
        }

        gpsTracker = new GpsTracker(MainActivity.this);

        mLocationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        getStores(gpsTracker.getLatitude(), gpsTracker.getLongitude(), RADIUS);
    }

    private void setMarkers(ArrayList<Store> stores){
        for(Store store : stores){
            Marker marker = new Marker();
            marker.setPosition(new LatLng(store.getLat(), store.getLng()));
            marker.setMap(mNaverMap);
            mMarkers.add(marker);
        }
    }

    private void removeAllMarkers(){
        for(Marker marker : mMarkers){
            marker.setMap(null);
        }
        mMarkers.clear();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,  @NonNull int[] grantResults) {
        if (mLocationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }

    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        mNaverMap = naverMap;
        mNaverMap.setLocationSource(mLocationSource);
        mNaverMap.setLocationTrackingMode(LocationTrackingMode.NoFollow);

        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(gpsTracker.getLatitude(),  gpsTracker.getLongitude()))
                .animate(CameraAnimation.Easing);;
        mNaverMap.moveCamera(cameraUpdate);
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

    }
    /*---------
    API Communication End*/
}
