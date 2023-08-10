package com.minux.mask_alarmi.ui.main.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.minux.mask_alarmi.R
import com.minux.mask_alarmi.domain.model.Store
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.MapConstants

private const val TAG = "MapFragment"

class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var viewModel: MapViewModel
    private lateinit var naverMap: NaverMap
    private var storeMarkers: List<StoreMarker> = emptyList()
    private var isMapReady = false
    private var preClickedStoreCode: Long? = null

    companion object {
        fun newInstance() = MapFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MapViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        initMap()

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

    private fun makeStoreMarkers(stores: List<Store>): List<StoreMarker> {
        return stores.map { store ->
            StoreMarker(
                store.code,
                store.remainState,
                coordinate = LatLng(store.lat, store.lng),
            ) { clickedStoreCode, isClicked ->
                onStoreMarkerClicked(clickedStoreCode, isClicked)
            }.build()
        }
    }

    private fun onStoreMarkerClicked(clickedStoreCode: Long, isClicked: Boolean) {
        if (clickedStoreCode != preClickedStoreCode) {
            storeMarkers.firstOrNull { it.storeCode == preClickedStoreCode }?.isClicked = false
            preClickedStoreCode = clickedStoreCode
        } else if (isClicked) {
            preClickedStoreCode = null
        }

        Log.i(TAG, "${clickedStoreCode}, isClicked: $isClicked")
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
        isMapReady = true
        storeMarkers.forEach { it.marker.map = this.naverMap }
    }
}