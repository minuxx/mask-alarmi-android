package com.softsquared.android.mask_alarmi.src.main;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.PointF;
import android.net.Uri;
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
import static com.softsquared.android.mask_alarmi.src.ApplicationClass.INRADIUS;


public class MainActivity extends BaseActivity implements MainActivityView, OnMapReadyCallback {
    private final static String TAG = "main act: ";
    private NaverMap mNaverMap;
    private MapFragment mFgMap;
    private GpsTracker gpsTracker;
    private FusedLocationSource mLocationSource;

    private TextView  mTvPossibleDay, mTvStoreName, mTvStoreAddr, mTvStoreStockAt ,mTvUpdateTime;
    private ImageView mIvStoreMaskState;
    private ImageButton mIbtnRefresh;

    private Marker mSelectedMarker = null;
    private String mSelectedState = null;

    private LinearLayout mLlStoreInfo;

    private ArrayList<Marker> mMarkers = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        //set firebase & facebook analytics
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        logSentFriendRequestEvent();

        //map
        FragmentManager fm = getSupportFragmentManager();

        mFgMap = (MapFragment) fm.findFragmentById(R.id.main_fg_map);

        if (mFgMap != null) {
            mFgMap.getMapAsync(MainActivity.this);
        }

        mMarkers = new ArrayList<>();

        //location & gps
        mLocationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
        gpsTracker = new GpsTracker(MainActivity.this);
    }

    @Override
    public void initViews() {
        super.initViews();
        //information
        TextView tvAnnounce = findViewById(R.id.main_tv_announce);
        tvAnnounce.setSelected(true);
        mTvPossibleDay = findViewById(R.id.main_tv_possible_day);
        setPossibleDay();
        mTvUpdateTime = findViewById(R.id.main_tv_update_at);
        mTvUpdateTime.setText(getUpdateTime());
        //util button
        mIbtnRefresh = findViewById(R.id.main_ibtn_refresh);
        ImageButton ibtnSearch = findViewById(R.id.main_ibtn_search);
        ImageButton ibtnMyLocation = findViewById(R.id.main_ibtn_mylocation);
        //Store
        mTvStoreName = findViewById(R.id.main_tv_store_name);
        mTvStoreAddr = findViewById(R.id.main_tv_store_addr);
        mTvStoreStockAt = findViewById(R.id.main_tv_store_stock_at);
        ImageButton ibtnStoreWayFinding = findViewById(R.id.main_ibtn_wayfinding);
        ImageButton ibtnStoreShare = findViewById(R.id.main_ibtn_store_share);
        mIvStoreMaskState = findViewById(R.id.main_iv_store_state);
        mLlStoreInfo = findViewById(R.id.main_ll_storeInfo);

        //information
        tvAnnounce.setOnClickListener(this);
        mTvPossibleDay.setOnClickListener(this);
        //util button
        mIbtnRefresh.setOnClickListener(this);
        ibtnSearch.setOnClickListener(this);
        ibtnMyLocation.setOnClickListener(this);
        //store
        mTvStoreName.setOnClickListener(this);
        ibtnStoreWayFinding.setOnClickListener(this);
        ibtnStoreShare.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFgMap.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setPossibleDay();
        removeAllMarkers();
        if(mLlStoreInfo.getVisibility() == View.VISIBLE) {
            showStoreInfo(View.GONE);
        }
        mTvUpdateTime.setText(getUpdateTime());
        getStores(gpsTracker.getLatitude(), gpsTracker.getLongitude(), INRADIUS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFgMap.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mFgMap.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFgMap.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mFgMap.onLowMemory();
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
            case R.id.main_ibtn_search:
                showCustomToast(getString(R.string.main_ready_bookmark));
                break;
            case R.id.main_ibtn_refresh:
                removeAllMarkers();
                startRotationRefreshIbtn(true);
                if (mLlStoreInfo.getVisibility() == View.VISIBLE) {
                    showStoreInfo(View.GONE);
                }
                mTvUpdateTime.setText(getUpdateTime());
                getStores(gpsTracker.getLatitude(), gpsTracker.getLongitude(), INRADIUS);
                break;
            case R.id.main_tv_store_name:
                showCustomToast(getString(R.string.main_ready_findwaying));
                break;
            case R.id.main_ibtn_wayfinding:
                moveFindingWay();
                break;
            case R.id.main_ibtn_store_share:
                showCustomToast(getString(R.string.main_ready_findwaying));
                break;
        }
    }



    private void startRotationRefreshIbtn(boolean startRotation){
        if(startRotation){
            mIbtnRefresh.setImageResource(R.drawable.ic_refresh_clicked);
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
//            mIbtnRefresh.setAnimation(animation);
            mIbtnRefresh.startAnimation(animation);
        }else{
            mIbtnRefresh.setImageResource(R.drawable.ic_refresh);
            mIbtnRefresh.setAnimation(null);
        }
    }

    private void searchStore(){

    }

    /*--------------
    move find way function */

    private void moveFindingWay(){
        //nmap://route/walk?slat=37.4640070&slng=126.9522394&sname=%EC%84%9C%EC%9A%B8%EB%8C%80%ED%95%99%EA%B5%90&dlat=37.4764356&dlng=126.9618302&dname=%EB%8F%99%EC%9B%90%EB%82%99%EC%84%B1%EB%8C%80%EC%95%84%ED%8C%8C%ED%8A%B8&appname=com.example.myapp
        if(mSelectedMarker != null){
            Store store = (Store)mSelectedMarker.getTag();
            if(store != null){
                String naverMapUrl = "nmap://route/walk?slat=" + gpsTracker.getLatitude() + "&slng=" + gpsTracker.getLongitude() + "&sname=" + "현재위치" +
                        "&dlat=" + store.getLat() + "&dlng=" + store.getLng() + "&dname=" + store.getName() +
                        "&appname=" + getPackageName();

                try {
                    Intent naverMapIntent = Intent.parseUri(naverMapUrl, Intent.URI_INTENT_SCHEME);
                    startActivity(naverMapIntent);
                } catch (ActivityNotFoundException e){
                    String googleMapuri ="http://maps.google.com/maps?daddr="+store.getName();
                    Intent googleMapIntent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse(googleMapuri));
                    googleMapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    googleMapIntent.addCategory(Intent.CATEGORY_LAUNCHER );
                    googleMapIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    startActivity(googleMapIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*--------------
    move find way function end */

    /*--------------
    time, day functions */

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

    private String getUpdateTime(){
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd HH:mm", Locale.KOREA);
        return simpleDateFormat.format(mDate);
    }

    private String getStockTime(String originFormatDate){
//        2020/03/17 08:52:00
//        03.15 23:59
        return originFormatDate.substring(5, 7) + "." + originFormatDate.substring(8, 10) + " " + originFormatDate.substring(11,16);
    }
    /*--------------
    time, day functions end */


    /*--------------
    marker functions */

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
                        handleClickMarker(marker, store, R.drawable.ic_empty_small, R.drawable.ic_empty_activate, R.drawable.ic_empty_big);
                        break;
                    case "break":
                        handleClickMarker(marker, store, R.drawable.ic_null_small, R.drawable.ic_null_activate, R.drawable.ic_null_big);
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
                    setStoreInfo(store.getName(), store.getAddr(), store.getStock_at() ,infoStateRes);
                    marker.setIcon(OverlayImage.fromResource(activatedRes));
                    mSelectedMarker = marker;
                    mSelectedMarker.setTag(store);
                }
                else if(mSelectedMarker != marker){
                    changeStoreInfo(store.getName(), store.getAddr(), store.getStock_at() ,infoStateRes);
                    marker.setIcon(OverlayImage.fromResource(activatedRes));
                    mSelectedMarker = marker;
                    mSelectedMarker.setTag(store);
                }else{
                    marker.setIcon(OverlayImage.fromResource(inactivatedRes));
                    mSelectedMarker = null;
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
    marker functions end */


    /*--------------
    store info functions */

    private void setStoreInfo(String name, String address, String stockAt, int res) {
        mTvStoreName.setText(name);
        mTvStoreAddr.setText(address);
        mTvStoreStockAt.setText(getStockTime(stockAt));
        mIvStoreMaskState.setImageResource(res);

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

    private void changeStoreInfo(String name, String address, String stockAt, int res) {
        mTvStoreName.setText(name);
        mTvStoreAddr.setText(address);
        mTvStoreStockAt.setText(getStockTime(stockAt));
        mIvStoreMaskState.setImageResource(res);

        Store store = (Store)mSelectedMarker.getTag();
        if(store != null) {
            switch (store.getRemain_stat()) {
                case "plenty":
                    mSelectedMarker.setIcon(OverlayImage.fromResource(R.drawable.ic_plenty_small));
                    break;
                case "some":
                    mSelectedMarker.setIcon(OverlayImage.fromResource(R.drawable.ic_som_small));
                    break;
                case "few":
                    mSelectedMarker.setIcon(OverlayImage.fromResource(R.drawable.ic_few_small));
                    break;
                case "empty":
                    mSelectedMarker.setIcon(OverlayImage.fromResource(R.drawable.ic_empty_small));
                    break;
                case "break":
                    mSelectedMarker.setIcon(OverlayImage.fromResource(R.drawable.ic_null_small));
                    break;
            }
        }
    }

    /*--------------
    store info functions end */

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
                    Store store = (Store)mSelectedMarker.getTag();
                    if(store != null) {
                        switch (store.getRemain_stat()) {
                            case "plenty":
                                mSelectedMarker.setIcon(OverlayImage.fromResource(R.drawable.ic_plenty_small));
                                break;
                            case "some":
                                mSelectedMarker.setIcon(OverlayImage.fromResource(R.drawable.ic_som_small));
                                break;
                            case "few":
                                mSelectedMarker.setIcon(OverlayImage.fromResource(R.drawable.ic_few_small));
                                break;
                            case "empty":
                                mSelectedMarker.setIcon(OverlayImage.fromResource(R.drawable.ic_empty_small));
                                break;
                            case "break":
                                mSelectedMarker.setIcon(OverlayImage.fromResource(R.drawable.ic_null_small));
                                break;
                        }
                    }
                    mSelectedMarker = null;
                    showStoreInfo(View.GONE);
                }
            }
        });
        naverMap.setLocationSource(mLocationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.NoFollow);

        UiSettings uiSettings = mNaverMap.getUiSettings();
        uiSettings.setZoomControlEnabled(false);

        getStores(gpsTracker.getLatitude(), gpsTracker.getLongitude(), INRADIUS);
    }


    /*---------
    api communication */

    private void getStores(double lat, double lng, int m) {
        if(mIbtnRefresh.getAnimation() == null){
            showProgressDialog();
        }
        MainService mainService = new MainService(this);
        mainService.getStores(lat, lng, m);
    }

    @Override
    public void getStoresSuccess(int count, ArrayList<Store> stores) {

        if(mNaverMap != null){
            setMarkers(stores);
            CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()))
                    .animate(CameraAnimation.Easing);
            mNaverMap.moveCamera(cameraUpdate);
        }

        if(mIbtnRefresh.getAnimation() != null){
            startRotationRefreshIbtn(false);
        }else{
            hideProgressDialog();
        }
    }

    @Override
    public void getStoresFailure(String message) {
        if(mNaverMap != null) {
            CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()))
                    .animate(CameraAnimation.Easing);
            mNaverMap.moveCamera(cameraUpdate);
        }

        if(mIbtnRefresh.getAnimation() != null){
            startRotationRefreshIbtn(false);
        }else{
            hideProgressDialog();
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
