package com.minux.mask_alarmi.ui.main.map

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.minux.mask_alarmi.R
import com.minux.mask_alarmi.data.models.Store
import com.minux.mask_alarmi.util.AnimUtil
import com.minux.mask_alarmi.util.InputUtil
import com.minux.mask_alarmi.util.LocationUtil
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
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
    private var isMapReady = false

    private var storeMarkers: List<StoreMarker> = emptyList()
    private var storeBottomDialog: StoreBottomDialog? = null

    private var locationUtil: LocationUtil? = null
    private var inputUtil: InputUtil? = null

    private lateinit var loadingPb: ProgressBar
    private lateinit var maskAmountIv: ImageView
    private lateinit var myLocationIbtn: ImageButton

    private lateinit var searchAddressIbtn: ImageButton
    private lateinit var searchAddressEt: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MapViewModel::class.java]
        locationUtil = LocationUtil(requireActivity() as AppCompatActivity)
        inputUtil = InputUtil(requireActivity() as AppCompatActivity)
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

    override fun onMapReady(map: NaverMap) {
        naverMap = map.apply {
            minZoom = CAMERA_MIN_ZOOM
            maxZoom = CAMERA_MAX_ZOOM
            uiSettings.isZoomControlEnabled = false
        }
        isMapReady = true

        locationUtil?.getLocationSource()?.let {
            naverMap.locationSource = it
            naverMap.locationTrackingMode = LocationTrackingMode.NoFollow
        }
        locationUtil?.getLastLocation { latlng ->
            latlng?.let { getStoresAndMoveCamera(it) }
        }
        naverMap.setOnMapClickListener { _, _ ->
            if (searchAddressEt.isFocusable) {
                collapseSearchBar()
            }
        }
    }

    private fun initViews(view: View) {
        loadingPb = view.findViewById(R.id.map_pb_loading)
        maskAmountIv = view.findViewById(R.id.map_iv_mask_amount)
        myLocationIbtn = view.findViewById(R.id.map_ibtn_my_location)
        myLocationIbtn.setOnClickListener {
            disconnectStoreMarkersOnMap()
            locationUtil?.getLastLocation { latlng ->
                latlng?.let { getStoresAndMoveCamera(it) }
            }
        }
        searchAddressIbtn = view.findViewById(R.id.map_ibtn_search_address)
        searchAddressEt = view.findViewById(R.id.map_et_search_address)
        searchAddressEt.setOnClickListener {
            expandSearchBar()
        }
        setFocusSearchBar(false)
    }

    private fun expandSearchBar() {
        val targetWidth =
            searchAddressEt.resources.displayMetrics.widthPixels - searchAddressEt.marginStart * 2
        val animator = ValueAnimator.ofInt(searchAddressEt.width, targetWidth).apply {
            duration = 1000
        }

        animator.addUpdateListener { animation ->
            val layoutParams = searchAddressEt.layoutParams
            layoutParams.width = animation.animatedValue as Int
            searchAddressEt.layoutParams = layoutParams
        }

        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                slideOutViews()
                searchAddressEt.hint = getString(R.string.map_search_address_hint)
            }

            override fun onAnimationEnd(animation: Animator) {
                setFocusSearchBar(true)
                inputUtil?.showSoftInput(searchAddressEt)
            }
        })

        animator.start()
    }

    private fun setFocusSearchBar(isFocusable: Boolean) {
        searchAddressEt.isFocusable = isFocusable
        searchAddressEt.isFocusableInTouchMode = isFocusable
    }

    private fun getStoresAndMoveCamera(latlng: LatLng) {
        viewModel.getStoresByGeo(latlng)
        val cameraUpdate = CameraUpdate
            .scrollAndZoomTo(latlng, CAMERA_MAX_ZOOM)
            .animate(CameraAnimation.Easing)
        naverMap.moveCamera(cameraUpdate)
    }

    private fun disconnectStoreMarkersOnMap() {
        storeMarkers.forEach { it.marker.map = null }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeStores()
        observeIsLoading()
        observeErrorMessage()
        observeSearchedLatLng()
    }

    private fun observeStores() {
        viewModel.stores.observe(
            viewLifecycleOwner
        ) { stores ->
            storeMarkers = stores?.let { createStoreMarkers(it) } ?: emptyList()

            if (isMapReady) {
                storeMarkers.forEach { it.marker.map = this.naverMap }
            }
        }
    }

    private fun createStoreMarkers(stores: List<Store>): List<StoreMarker> {
        return stores.map { store ->
            StoreMarker(store) {
                handleSelectStoreMarker(true, it)
            }.newInstance()
        }
    }

    private fun handleSelectStoreMarker(isSelect: Boolean, selectedStore: Store) {
        val selectedStoreMarker = storeMarkers.firstOrNull { it.store.code == selectedStore.code }
        selectedStoreMarker?.isSelected = isSelect
        if (isSelect) openStoreBottomDialog(selectedStore)
    }

    private fun openStoreBottomDialog(store: Store) {
        slideOutViews(true)
        storeBottomDialog = StoreBottomDialog(store, onDismiss = {
            handleSelectStoreMarker(false, store)

            CoroutineScope(Dispatchers.Main).launch {
                delay(500)
                slideInViews(true)
            }
        })
        storeBottomDialog?.show(childFragmentManager, TAG)
    }

    private fun slideOutViews(isOpenStoreBottomDialog: Boolean = false) {
        AnimUtil.startSlideOutAnim(
            maskAmountIv,
            -(maskAmountIv.height.toFloat() + maskAmountIv.marginTop.toFloat()),
            false
        )
        AnimUtil.startSlideOutAnim(
            myLocationIbtn,
            myLocationIbtn.width.toFloat() + myLocationIbtn.marginEnd.toFloat()
        )
        if (isOpenStoreBottomDialog) {
            AnimUtil.startSlideOutAnim(
                searchAddressEt,
                -(searchAddressEt.width.toFloat() + searchAddressEt.marginStart.toFloat())
            )
        }
    }

    private fun slideInViews(isCloseStoreBottomDialog: Boolean = false) {
        AnimUtil.startSlideInAnim(maskAmountIv, 0f, false)
        AnimUtil.startSlideInAnim(myLocationIbtn, 0f)
        if (isCloseStoreBottomDialog) {
            AnimUtil.startSlideInAnim(searchAddressEt, 0f)
        }
    }

    private fun observeErrorMessage() {
        viewModel.errorMessage.observe(
            viewLifecycleOwner
        ) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun observeIsLoading() {
        viewModel.isLoading.observe(
            viewLifecycleOwner
        ) { isLoading ->
            loadingPb.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun observeSearchedLatLng() {
        viewModel.searchedLatLng.observe(
            viewLifecycleOwner
        ) { latlng ->
            disconnectStoreMarkersOnMap()
            getStoresAndMoveCamera(latlng)
        }
    }

    override fun onStart() {
        super.onStart()
        setListeners()
    }

    private fun setListeners() {
        searchAddressEt.setOnEditorActionListener { v, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    val address = v.text.toString()
                    if (address.isNotEmpty()) {
                        locationUtil?.getLastLocation { latlng ->
                            latlng?.let {
                                viewModel.searchAddress(address, it.latitude, it.longitude)
                            }
                        }
                    }
                    collapseSearchBar()
                    true
                }

                else -> false
            }
        }
    }

    private fun collapseSearchBar() {
        val animator = ObjectAnimator.ofInt(searchAddressEt.width, myLocationIbtn.width).apply {
            duration = 1000
        }

        animator.addUpdateListener { animation ->
            val layoutParams = searchAddressEt.layoutParams
            layoutParams.width = animation.animatedValue as Int
            searchAddressEt.layoutParams = layoutParams
        }

        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                inputUtil?.hideSoftInput()
                setFocusSearchBar(false)
            }

            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                slideInViews()
                searchAddressEt.hint = null
                searchAddressEt.text = null
            }
        })

        animator.start()
    }

    override fun onResume() {
        super.onResume()
        locationUtil?.startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        storeBottomDialog?.dismiss()
        locationUtil?.stopLocationUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
        locationUtil = null
        inputUtil = null
    }

    companion object {
        fun newInstance() = MapFragment()
    }
}