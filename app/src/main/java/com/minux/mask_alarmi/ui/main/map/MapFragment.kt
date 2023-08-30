package com.minux.mask_alarmi.ui.main.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.marginEnd
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.minux.mask_alarmi.R
import com.minux.mask_alarmi.domain.model.Store
import com.minux.mask_alarmi.util.AnimUtil
import com.minux.mask_alarmi.util.LocationUtil
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.UiSettings
import com.naver.maps.map.util.MapConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "MapFragment"

class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var viewModel: MapViewModel
    private lateinit var naverMap: NaverMap
    private var storeMarkers: List<StoreMarker> = emptyList()
    private var isMapReady = false
    private var curStoreCode: Long? = null

    private lateinit var locationUtil: LocationUtil

    private lateinit var ivMaskAmount: ImageView
    private lateinit var ibtnMyLocation: ImageButton
    private lateinit var ibtnRefresh: ImageButton
    private lateinit var ibtnSearch: ImageButton

    companion object {
        fun newInstance() = MapFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MapViewModel::class.java]
        locationUtil = LocationUtil(requireActivity() as AppCompatActivity)
        locationUtil.initLocationSettings()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        initMap()
        ivMaskAmount = view.findViewById(R.id.main_iv_mask_amount)
        ibtnMyLocation = view.findViewById(R.id.main_ibtn_my_location)
        ibtnMyLocation.setOnClickListener {
            locationUtil.getLastLocation {
                Log.i(TAG, "lastLocation: $it")
            }
        }
        ibtnRefresh = view.findViewById(R.id.main_ibtn_refresh)
        ibtnSearch = view.findViewById(R.id.main_ibtn_search)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.storeListLiveData.observe(
            viewLifecycleOwner
        ) { stores ->
            Log.i(TAG, "Got stores ${stores.size}")
            storeMarkers = stores?.let { makeStoreMarkers(it) } ?: emptyList()

            if (isMapReady) {
                storeMarkers.forEach { it.marker.map = this.naverMap }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        locationUtil.startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        locationUtil.stopLocationUpdates()
    }

    private fun makeStoreMarkers(stores: List<Store>): List<StoreMarker> {
        return stores.map { store ->
            StoreMarker(
                store.code,
                store.remainState,
                coordinate = LatLng(store.lat, store.lng),
            ) { storeCode, isClicked ->
                onStoreMarkerClicked(storeCode, isClicked)
            }.newInstance()
        }
    }

    private fun onStoreMarkerClicked(newStoreCode: Long, isClicked: Boolean) {
        Log.i(TAG, "curStoreCode: ${curStoreCode}, newStoreCode: ${newStoreCode}, isClicked: $isClicked")
        if (newStoreCode != curStoreCode) {
            storeMarkers.firstOrNull { it.storeCode == curStoreCode }?.isClicked = false
            curStoreCode = newStoreCode
            viewModel.getStoreByCode(newStoreCode)?.let { openStoreBottomDialog(it) }
        } else if (!isClicked) {
            curStoreCode = null
        }
    }

    private fun openStoreBottomDialog(store: Store) {
        slideOutViews()
        StoreBottomDialog(store, onDismiss = {
            storeMarkers.firstOrNull { it.storeCode == store.code }?.isClicked = false
            curStoreCode = null

            CoroutineScope(Dispatchers.Main).launch {
                delay(500)
                slideInViews()
            }
        }).show(childFragmentManager, TAG)
    }

    private fun slideOutViews() {
        AnimUtil.startSlideOutAnim(ivMaskAmount, "translationY", -(ivMaskAmount.height.toFloat() + ivMaskAmount.marginTop.toFloat()))
        AnimUtil.startSlideOutAnim(ibtnMyLocation, "translationX", ibtnMyLocation.width.toFloat() + ibtnMyLocation.marginEnd.toFloat())
        AnimUtil.startSlideOutAnim(ibtnRefresh, "translationX", ibtnRefresh.width.toFloat() + ibtnRefresh.marginEnd.toFloat())
        AnimUtil.startSlideOutAnim(ibtnSearch, "translationX", -(ibtnRefresh.width.toFloat() + ibtnRefresh.marginEnd.toFloat()))
    }

    private fun slideInViews() {
        AnimUtil.startSlideInAnim(ivMaskAmount, "translationY", 0f)
        AnimUtil.startSlideInAnim(ibtnMyLocation, "translationX", 0f)
        AnimUtil.startSlideInAnim(ibtnRefresh, "translationX", 0f)
        AnimUtil.startSlideInAnim(ibtnSearch, "translationX", 0f)
    }

    private fun initMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_container) as com.naver.maps.map.MapFragment?
            ?: com.naver.maps.map.MapFragment.newInstance(
                NaverMapOptions().extent(MapConstants.EXTENT_KOREA)
        ).also {
            this.childFragmentManager
                .beginTransaction()
                .add(R.id.map_container, it)
                .commit()
        }
        mapFragment.getMapAsync(this@MapFragment)
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
//        this.naverMap.uiSettings.isZoomControlEnabled = false
        isMapReady = true
        storeMarkers.forEach { it.marker.map = this.naverMap }
    }
}