package com.softsquared.android.mask_alarmi.src.main;

import android.content.Intent;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
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

import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;
import com.softsquared.android.mask_alarmi.R;
import com.softsquared.android.mask_alarmi.src.BaseActivity;
import com.softsquared.android.mask_alarmi.src.announce.AnnounceActivity;
import com.softsquared.android.mask_alarmi.src.main.interfaces.MainActivityView;
import com.softsquared.android.mask_alarmi.src.main.models.Store;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.softsquared.android.mask_alarmi.src.ApplicationClass.LOCATION_PERMISSION_REQUEST_CODE;
import static com.softsquared.android.mask_alarmi.src.ApplicationClass.RADIUS;


public class MainActivity extends BaseActivity implements MainActivityView, OnMapReadyCallback {
    private NaverMap mNaverMap;
    private GpsTracker gpsTracker;
    private FusedLocationSource mLocationSource;

    private TextView  mTvPossibleDay, mTvStoreName, mTvStoreAddr, mTvTime;
    private ImageView mIvMaskState;

    private Marker mSelectedMarker = null;
    private String mSelectedState = null;

    private LinearLayout mLlStoreInfo;

    private ArrayList<Marker> mMarkers = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set firebase & facebook analytics
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        logSentFriendRequestEvent();

        setContentView(R.layout.activity_main);
        initViews();

        FragmentManager fm = getSupportFragmentManager();

        MapFragment fgMap = (MapFragment) fm.findFragmentById(R.id.main_fg_map);

        if (fgMap != null) {
            fgMap.getMapAsync(MainActivity.this);
        }

        mMarkers = new ArrayList<>();
        mLocationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
        gpsTracker = new GpsTracker(MainActivity.this);
    }

    @Override
    public void initViews() {
        super.initViews();
        TextView tvAnnounce = findViewById(R.id.main_tv_announce);
        tvAnnounce.setSelected(true);

        mTvPossibleDay = findViewById(R.id.main_tv_possible_day);
        setPossibleDay();

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
        mTvPossibleDay.setOnClickListener(this);
        ibtnReload.setOnClickListener(this);
        mTvStoreName.setOnClickListener(this);
        ibtnMyLocation.setOnClickListener(this);
        ibtnWayFinding.setOnClickListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setPossibleDay();
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
                if(mLlStoreInfo.getVisibility() == View.VISIBLE) {
                    showStoreInfo(View.GONE);
                }
                mTvTime.setText(setTime());
                getStores(gpsTracker.getLatitude(), gpsTracker.getLongitude(), RADIUS);
                break;
        }
    }


    /*--------------
    time, day function */

    private String setTime(){
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.KOREA);
        return simpleDateFormat.format(mDate);
    }

    private void setPossibleDay() {
        Calendar cal = Calendar.getInstance();

        int weekNum = cal.get(Calendar.DAY_OF_WEEK);
        //1~7 일~토
        switch (weekNum) {
            case 1: //토, 일
            case 7:
                mTvPossibleDay.setText(getString(R.string.main_possible_all));
                break;
            default:
                int first = (weekNum - 1) % 10;
                int second = (first + 5) % 10;

                String possibleDayStr = "오늘은 출생연도 끝자리 " + first + "・" + second + "년생이 살 수 있어요!";

                SpannableStringBuilder ssb = new SpannableStringBuilder(possibleDayStr);
                ssb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.mainPossibleYearText)), 13, 18, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                mTvPossibleDay.setText(ssb);
                break;
        }
    }

    /*--------------
    time, day function end */


    /*--------------
    marker function */

    private void setMarkers(ArrayList<Store> stores) {
        for (final Store store : stores) {
            if (store.getRemain_stat() != null) {

                final Marker marker = new Marker();
                marker.setPosition(new LatLng(store.getLat(), store.getLng()));

                switch (store.getRemain_stat()) {
                    case "plenty":
                        handleClickMarker(marker, store, R.drawable.ic_plenty_small, R.drawable.ic_plenty_activate, R.drawable.ic_plenty_big);
                        break;
                    case "some":
                        handleClickMarker(marker, store, R.drawable.ic_som_small, R.drawable.ic_som_activate, R.drawable.ic_som_big);
                        break;
                    case "few":
                        handleClickMarker(marker, store, R.drawable.ic_few_small, R.drawable.ic_few_activate, R.drawable.ic_few_big);
                        break;
                    case "empty":
                    case "break":
                        handleClickMarker(marker, store, R.drawable.ic_empty_small, R.drawable.ic_empty_activate, R.drawable.ic_empty_big);
                        break;
                }
                marker.setMap(mNaverMap);
                mMarkers.add(marker);
            }
        }
    }

    private void handleClickMarker(final Marker marker, final Store store, final int inactivatedRes, final int activatedRes, final int infoStateRes){
        marker.setIcon(OverlayImage.fromResource(inactivatedRes));
        marker.setOnClickListener(new Overlay.OnClickListener() {
            @Override
            public boolean onClick(@NonNull Overlay overlay) {
                if(mSelectedMarker == null){
                    setStoreInfo(store.getName(), store.getAddr(), infoStateRes);
                    marker.setIcon(OverlayImage.fromResource(activatedRes));
                    mSelectedMarker = marker;
                    mSelectedState = store.getRemain_stat();
                }
                else if(mSelectedMarker != marker){
                    changeStoreInfo(store.getName(), store.getAddr(), infoStateRes);
                    marker.setIcon(OverlayImage.fromResource(activatedRes));
                    mSelectedMarker = marker;
                    mSelectedState = store.getRemain_stat();
                }else{
                    marker.setIcon(OverlayImage.fromResource(inactivatedRes));
                    mSelectedMarker = null;
                    mSelectedState = null;
                    showStoreInfo(View.GONE);
                }
                return true;
            }
        });
    }

    private void removeAllMarkers() {
        mSelectedMarker = null;
        for (Marker marker : mMarkers) {
            marker.setMap(null);
        }
        mMarkers.clear();
    }

    /*--------------
    marker function end */


    /*--------------
    store info */

    private void setStoreInfo(String name, String address, int res) {
        mTvStoreName.setText(name);
        mTvStoreAddr.setText(address);
        mIvMaskState.setImageResource(res);

       showStoreInfo(View.VISIBLE);
    }

    private void showStoreInfo(int visibility){
        if(visibility == View.VISIBLE) {
            Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slide_up);
            mLlStoreInfo.startAnimation(slideUp);
        }else{
            Animation slideDown = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slide_down);
            mLlStoreInfo.startAnimation(slideDown);
        }
        mLlStoreInfo.setVisibility(visibility);
    }

    private void changeStoreInfo(String name, String address, int res) {
        mTvStoreName.setText(name);
        mTvStoreAddr.setText(address);
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
            case "break":
                mSelectedMarker.setIcon(OverlayImage.fromResource(R.drawable.ic_empty_small));
                break;
        }
    }

    /*--------------
    store info end */

    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        mNaverMap = naverMap;
        LocationOverlay locationOverlay = naverMap.getLocationOverlay();
        locationOverlay.setVisible(true);
        locationOverlay.setIcon(OverlayImage.fromResource(R.drawable.ic_range_and_location));

        mNaverMap.setOnMapClickListener(new NaverMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
                if(mLlStoreInfo.getVisibility() == View.VISIBLE){
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
                        case "break":
                            mSelectedMarker.setIcon(OverlayImage.fromResource(R.drawable.ic_empty_small));
                            break;
                    }
                    mSelectedMarker = null;
                    mSelectedState = null;
                    showStoreInfo(View.GONE);
                }
            }
        });
        naverMap.setLocationSource(mLocationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.NoFollow);

        UiSettings uiSettings = mNaverMap.getUiSettings();
        uiSettings.setZoomControlEnabled(false);

        getStores(gpsTracker.getLatitude(), gpsTracker.getLongitude(), RADIUS);
    }


    /*---------
    api communication */

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
    api communication end*/


    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    public void logSentFriendRequestEvent () {
        AppEventsLogger logger = AppEventsLogger.newLogger(this);
        logger.logEvent("sentFriendRequest");
    }
}
