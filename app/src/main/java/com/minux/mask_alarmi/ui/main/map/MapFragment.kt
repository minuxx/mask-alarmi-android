package com.minux.mask_alarmi.ui.main.map

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginEnd
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.internal.ViewUtils.showKeyboard
import com.minux.mask_alarmi.R
import com.minux.mask_alarmi.domain.model.Store
import com.minux.mask_alarmi.util.AnimUtil
import com.minux.mask_alarmi.util.LocationUtil
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.MapConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


private const val TAG = "MapFragment"
private const val CAMERA_MIN_ZOOM = 8.0
private const val CAMERA_MAX_ZOOM = 14.0

class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var viewModel: MapViewModel
    private lateinit var naverMap: NaverMap
    private var storeMarkers: List<StoreMarker> = emptyList()
    private var isMapReady = false
    private var curStoreCode: Long? = null

    private var locationUtil: LocationUtil? = null

    private lateinit var ivMaskAmount: ImageView
    private lateinit var ibtnMyLocation: ImageButton
    private lateinit var ibtnRefresh: ImageButton
    private lateinit var ibtnSearch: ImageButton
    private lateinit var etSearch: EditText

    companion object {
        fun newInstance() = MapFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MapViewModel::class.java]
        locationUtil = LocationUtil(requireActivity() as AppCompatActivity)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        initMap()
        initViews(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeStores()
    }

    override fun onResume() {
        super.onResume()
        locationUtil?.startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        locationUtil?.stopLocationUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
        locationUtil = null
    }

    private fun initViews(view: View) {
        ivMaskAmount = view.findViewById(R.id.main_iv_mask_amount)
        ibtnMyLocation = view.findViewById(R.id.main_ibtn_my_location)
        ibtnMyLocation.setOnClickListener {
            removeCurStoreMarkers()
            moveLastLocation()
        }
        ibtnRefresh = view.findViewById(R.id.main_ibtn_refresh)
        ibtnSearch = view.findViewById(R.id.main_ibtn_search)
        ibtnSearch.setOnClickListener {
            expandSearchBar(etSearch)
        }
        etSearch = view.findViewById(R.id.main_et_search)
    }

    private fun observeStores() {
        viewModel.stores.observe(
            viewLifecycleOwner
        ) { stores ->
            Log.i(TAG, "Got stores ${stores.size}")
            storeMarkers = stores?.let { makeStoreMarkers(it) } ?: emptyList()

            if (isMapReady) {
                storeMarkers.forEach { it.marker.map = this.naverMap }
            }
        }
    }

    private fun initMap() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_container) as com.naver.maps.map.MapFragment?
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
        this.naverMap = naverMap.apply {
            minZoom = CAMERA_MIN_ZOOM
            maxZoom = CAMERA_MAX_ZOOM
        }
//        this.naverMap.uiSettings.isZoomControlEnabled = false
        isMapReady = true
        moveLastLocation()
    }

    private fun moveLastLocation() {
        locationUtil?.getLastLocation { latlng ->
            Log.i(TAG, "lastLocation: $latlng")
            latlng?.let {
                viewModel.getStoresByGeo(latlng)
                val cameraUpdate = CameraUpdate
                    .scrollAndZoomTo(LatLng(it.latitude, it.longitude), CAMERA_MAX_ZOOM)
                    .animate(CameraAnimation.Easing)
                naverMap.moveCamera(cameraUpdate)
            }
        }
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

    private fun removeCurStoreMarkers() {
        storeMarkers.forEach { it.marker.map = null }
    }

    private fun onStoreMarkerClicked(newStoreCode: Long, isClicked: Boolean) {
        Log.i(
            TAG,
            "curStoreCode: ${curStoreCode}, newStoreCode: ${newStoreCode}, isClicked: $isClicked"
        )
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
        AnimUtil.startSlideOutAnim(
            ivMaskAmount,
            -(ivMaskAmount.height.toFloat() + ivMaskAmount.marginTop.toFloat()),
            false
        )
        AnimUtil.startSlideOutAnim(
            ibtnMyLocation,
            ibtnMyLocation.width.toFloat() + ibtnMyLocation.marginEnd.toFloat()
        )
        AnimUtil.startSlideOutAnim(
            ibtnRefresh,
            ibtnRefresh.width.toFloat() + ibtnRefresh.marginEnd.toFloat()
        )
        AnimUtil.startSlideOutAnim(
            ibtnSearch,
            -(ibtnRefresh.width.toFloat() + ibtnRefresh.marginEnd.toFloat())
        )
    }

    private fun slideInViews() {
        AnimUtil.startSlideInAnim(ivMaskAmount, 0f, false)
        AnimUtil.startSlideInAnim(ibtnMyLocation, 0f)
        AnimUtil.startSlideInAnim(ibtnRefresh, 0f)
        AnimUtil.startSlideInAnim(ibtnSearch, 0f)
    }

    private fun expandSearchBar(etSearch: EditText) {
        val targetWidth = etSearch.resources.displayMetrics.widthPixels - etSearch.marginEnd * 2

        val animator = ValueAnimator.ofInt(1, targetWidth)
        animator.duration = 1200

        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            val layoutParams = etSearch.layoutParams
            layoutParams.width = value
            etSearch.layoutParams = layoutParams
        }

        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                etSearch.visibility = View.VISIBLE
            }
            override fun onAnimationEnd(animation: Animator) {
                etSearch.requestFocus()
                // 키보드 표시 코드 추가
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

        animator.start()
    }
}