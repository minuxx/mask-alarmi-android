package com.softsquared.android.mask_alarmi.src.main;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;
import com.softsquared.android.mask_alarmi.R;
import com.softsquared.android.mask_alarmi.src.BaseActivity;
import com.softsquared.android.mask_alarmi.src.announce.AnnounceActivity;
import com.softsquared.android.mask_alarmi.src.main.interfaces.MainActivityView;
import com.softsquared.android.mask_alarmi.src.main.models.Store;
import com.softsquared.android.mask_alarmi.src.splash.SplashActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.softsquared.android.mask_alarmi.src.ApplicationClass.RADIUS;


public class MainActivity extends BaseActivity implements MainActivityView, OnMapReadyCallback {
    private MapFragment mFgMap;
    private NaverMap mNaverMap;

    private GpsTracker gpsTracker;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource mLocationSource;

    private ArrayList<Marker> mMarkers = null;

    private TextView  mTvStoreName, mTvStoreAddr, mTvTime;
    private ImageView mIvMaskState;

    private Marker mSelectedMarker = null;
    private String mSelectedState = null;

    private LinearLayout mLlStoreInfo;

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            FragmentManager fm = getSupportFragmentManager();

            mFgMap = (MapFragment) fm.findFragmentById(R.id.main_fg_map);

            if (mFgMap != null) {
                mFgMap.getMapAsync(MainActivity.this);
            }

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
        initViews();

        TedPermission.with(MainActivity.this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("위치 및 GPS 권한 설정을 해주세요.")
                .setPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .check();


        mMarkers = new ArrayList<>();
        mLocationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
        gpsTracker = new GpsTracker(MainActivity.this);
    }

    @Override
    public void initViews() {
        super.initViews();
        TextView tvAnnounce = findViewById(R.id.main_tv_announce);
        tvAnnounce.setSelected(true);
        TextView tvPossibleDay = findViewById(R.id.main_tv_possible_day);
        setPossibleDay(tvPossibleDay);
        mTvTime = findViewById(R.id.main_tv_time);
        mTvTime.setText(setTime());
        ImageButton ibtnReload = findViewById(R.id.main_ibtn_reload);
        mTvStoreName = findViewById(R.id.main_tv_store_name);
        mTvStoreAddr = findViewById(R.id.main_tv_store_addr);
        mIvMaskState = findViewById(R.id.main_iv_state);
        ImageButton ibtnMyLocation = findViewById(R.id.main_ibtn_mylocation);
        ImageButton ibtnWayFinding = findViewById(R.id.main_ibtn_wayfinding);

        mLlStoreInfo = findViewById(R.id.main_ll_storeInfo);

        tvAnnounce.setOnClickListener(this);
        tvPossibleDay.setOnClickListener(this);
        ibtnReload.setOnClickListener(this);
        mTvStoreName.setOnClickListener(this);
        ibtnMyLocation.setOnClickListener(this);
        ibtnWayFinding.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.main_tv_announce:
                Intent announceIntent = new Intent(MainActivity.this, AnnounceActivity.class);
                startActivity(announceIntent);
                break;
            case R.id.main_tv_possible_day:
                break;
            case R.id.main_ibtn_mylocation:
                CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()))
                        .animate(CameraAnimation.Easing);
                ;
                mNaverMap.moveCamera(cameraUpdate);
                break;
            case R.id.main_tv_store_name:
                showCustomToast(getString(R.string.main_ready_bookmark));
                break;
            case R.id.main_ibtn_wayfinding:
                showCustomToast(getString(R.string.main_ready_findwaying));
                break;
            case R.id.main_ibtn_reload:
                removeAllMarkers();
                showStoreInfo(View.GONE);
                mTvTime.setText(setTime());
                getStores(gpsTracker.getLatitude(), gpsTracker.getLongitude(), RADIUS);
                break;
        }
    }



    private String setTime(){
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.KOREA);
        return simpleDateFormat.format(mDate);
    }

    private void setPossibleDay(TextView tvPossibleDay) {
        Calendar cal = Calendar.getInstance();

        int weekNum = cal.get(Calendar.DAY_OF_WEEK);
        //1~7 일~토
        switch (weekNum) {
            case 1: //토, 일
            case 7:
                tvPossibleDay.setText(getString(R.string.main_possible_all));
                break;
            default:
                int first = (weekNum - 1) % 10;
                int second = (first + 5) % 10;

                String possibleDayStr = "오늘은 출생연도 끝자리 " + first + "・" + second + "년생이 살 수 있어요!";

                SpannableStringBuilder ssb = new SpannableStringBuilder(possibleDayStr);
                ssb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.mainPossibleYearText)), 13, 18, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                tvPossibleDay.setText(ssb);
                break;
        }

    }

    private void setMarkers(ArrayList<Store> stores) {
        for (final Store store : stores) {
            if (store.getRemain_stat() != null) {
                final Marker marker = new Marker();
                marker.setPosition(new LatLng(store.getLat(), store.getLng()));
                switch (store.getRemain_stat()) {
                    case "plenty":
                        marker.setIcon(OverlayImage.fromResource(R.drawable.ic_plenty_small));
                        marker.setOnClickListener(new Overlay.OnClickListener() {
                            @Override
                            public boolean onClick(@NonNull Overlay overlay) {
                                if(mSelectedMarker == null){
                                    setStoreInfo(store.getName(), store.getAddr(), R.drawable.ic_plenty_big);
                                    marker.setIcon(OverlayImage.fromResource(R.drawable.ic_plenty_activate));
                                    mSelectedMarker = marker;
                                    mSelectedState = store.getRemain_stat();
                                }else if(mSelectedMarker != marker){
                                    setStoreInfo(store.getRemain_stat(), store.getName(), store.getAddr(), R.drawable.ic_plenty_big);
                                    marker.setIcon(OverlayImage.fromResource(R.drawable.ic_plenty_activate));
                                    mSelectedMarker = marker;
                                    mSelectedState = store.getRemain_stat();
                                }else{
                                    marker.setIcon(OverlayImage.fromResource(R.drawable.ic_plenty_small));
                                    mSelectedMarker = null;
                                    showStoreInfo(View.GONE);
                                }
                                return true;
                            }
                        });
                        break;
                    case "some":
                        marker.setIcon(OverlayImage.fromResource(R.drawable.ic_som_small));
                        marker.setOnClickListener(new Overlay.OnClickListener() {
                            @Override
                            public boolean onClick(@NonNull Overlay overlay) {
                                if(mSelectedMarker == null){
                                    setStoreInfo(store.getName(), store.getAddr(), R.drawable.ic_som_big);
                                    marker.setIcon(OverlayImage.fromResource(R.drawable.ic_som_activate));
                                    mSelectedMarker = marker;
                                    mSelectedState = store.getRemain_stat();
                                }else if(mSelectedMarker != marker){
                                    setStoreInfo(store.getRemain_stat(), store.getName(), store.getAddr(), R.drawable.ic_som_big);
                                    marker.setIcon(OverlayImage.fromResource(R.drawable.ic_som_activate));
                                    mSelectedMarker = marker;
                                    mSelectedState = store.getRemain_stat();
                                }else{
                                    marker.setIcon(OverlayImage.fromResource(R.drawable.ic_som_small));
                                    mSelectedMarker = null;
                                    showStoreInfo(View.GONE);
                                }
                                return true;
                            }
                        });
                        break;
                    case "few":
                        marker.setIcon(OverlayImage.fromResource(R.drawable.ic_few_small));
                        marker.setOnClickListener(new Overlay.OnClickListener() {
                            @Override
                            public boolean onClick(@NonNull Overlay overlay) {
                                if(mSelectedMarker == null){
                                    setStoreInfo(store.getName(), store.getAddr(), R.drawable.ic_few_big);
                                    marker.setIcon(OverlayImage.fromResource(R.drawable.ic_few_activate));
                                    mSelectedMarker = marker;
                                    mSelectedState = store.getRemain_stat();
                                }else if(mSelectedMarker != marker){
                                    setStoreInfo(store.getRemain_stat(), store.getName(), store.getAddr(), R.drawable.ic_few_big);
                                    marker.setIcon(OverlayImage.fromResource(R.drawable.ic_few_activate));
                                    mSelectedMarker = marker;
                                    mSelectedState = store.getRemain_stat();
                                }else{
                                    marker.setIcon(OverlayImage.fromResource(R.drawable.ic_few_small));
                                    mSelectedMarker = null;
                                    showStoreInfo(View.GONE);
                                }
                                return true;
                            }
                        });
                        break;
                    case "empty":
                        marker.setIcon(OverlayImage.fromResource(R.drawable.ic_empty_small));
                        marker.setOnClickListener(new Overlay.OnClickListener() {
                            @Override
                            public boolean onClick(@NonNull Overlay overlay) {
                                if(mSelectedMarker == null){
                                    setStoreInfo(store.getName(), store.getAddr(), R.drawable.ic_empty_big);
                                    marker.setIcon(OverlayImage.fromResource(R.drawable.ic_empty_activate));
                                    mSelectedMarker = marker;
                                    mSelectedState = store.getRemain_stat();
                                }
                                else if(mSelectedMarker != marker){
                                    setStoreInfo(store.getRemain_stat(), store.getName(), store.getAddr(), R.drawable.ic_empty_big);
                                    marker.setIcon(OverlayImage.fromResource(R.drawable.ic_empty_activate));
                                    mSelectedMarker = marker;
                                    mSelectedState = store.getRemain_stat();
                                }else{
                                    marker.setIcon(OverlayImage.fromResource(R.drawable.ic_empty_small));
                                    mSelectedMarker = null;
                                    showStoreInfo(View.GONE);
                                }
                                return true;
                            }
                        });
                        break;
                }
                marker.setMap(mNaverMap);
                mMarkers.add(marker);
            }
        }
    }

    private void setStoreInfo(String name, String addr, int res) {
        mTvStoreName.setText(name);
        mTvStoreAddr.setText(addr);
        mIvMaskState.setImageResource(res);

       showStoreInfo(View.VISIBLE);
    }

    private void showStoreInfo(int visivlity){
        if(visivlity == View.VISIBLE) {
            Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slide_up);
            mLlStoreInfo.startAnimation(slideUp);
        }else{
            Animation slideDown = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slide_down);
            mLlStoreInfo.startAnimation(slideDown);
        }
        mLlStoreInfo.setVisibility(visivlity);
    }

    private void setStoreInfo(String state, String name, String addr, int res) {
        mTvStoreName.setText(name);
        mTvStoreAddr.setText(addr);
        mIvMaskState.setImageResource(res);

        switch (mSelectedState) {
            case "plenty":
                mSelectedMarker.setIcon(OverlayImage.fromResource(R.drawable.ic_plenty_small));
                break;
            case "som":
                mSelectedMarker.setIcon(OverlayImage.fromResource(R.drawable.ic_som_small));
                break;
            case "few":
                mSelectedMarker.setIcon(OverlayImage.fromResource(R.drawable.ic_few_small));
                break;
            case "empty":
                mSelectedMarker.setIcon(OverlayImage.fromResource(R.drawable.ic_empty_small));
                break;
        }
    }


    private void removeAllMarkers() {
        for (Marker marker : mMarkers) {
            marker.setMap(null);
        }
        mMarkers.clear();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
        naverMap.setLocationSource(mLocationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.NoFollow);

        UiSettings uiSettings = mNaverMap.getUiSettings();
        uiSettings.setZoomControlEnabled(false);


        getStores(gpsTracker.getLatitude(), gpsTracker.getLongitude(), RADIUS);
    }


    /*---------
    API Communication*/
    private void getStores(double lat, double lng, int m) {
        showProgressDialog();
        MainService mainService = new MainService(this);
        mainService.getStores(lat, lng, m);
    }

    @Override
    public void getStoresSuccess(int count, ArrayList<Store> stores) {
        hideProgressDialog();
        if(mNaverMap != null){
            setMarkers(stores);
            CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()))
                    .animate(CameraAnimation.Easing);
            mNaverMap.moveCamera(cameraUpdate);
        }
    }

    @Override
    public void getStoresFailure(String message) {
        hideProgressDialog();
        if(mNaverMap != null) {
            CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()))
                    .animate(CameraAnimation.Easing);
            mNaverMap.moveCamera(cameraUpdate);
        }
    }
    /*---------
    API Communication End*/

}
