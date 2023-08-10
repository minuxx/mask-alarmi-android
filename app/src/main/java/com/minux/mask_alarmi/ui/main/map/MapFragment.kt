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
    private var storeMarkers: List<Marker> = emptyList()
    private var isMapReady = false

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
            stores?.let {
                Log.i(TAG, "Got stores ${stores.size}")

                storeMarkers = makeStoreMarkers(it)

                if (isMapReady) {
                    storeMarkers.forEach { marker -> marker.map = this.naverMap }
                }
            }
        }
    }
    private fun makeStoreMarkers(stores: List<Store>): List<Marker> {
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
    private fun onStoreMarkerClicked(storeCode: Long, isClicked: Boolean) {
        Log.i(TAG, "Click Marker ${storeCode}, $isClicked")
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
        storeMarkers.forEach { marker -> marker.map = this.naverMap }
    }
}