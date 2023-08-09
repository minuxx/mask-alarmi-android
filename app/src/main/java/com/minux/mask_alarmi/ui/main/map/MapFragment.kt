package com.minux.mask_alarmi.ui.main.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.minux.mask_alarmi.R
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.MapConstants

private const val TAG = "MapFragment"

class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var naverMap: NaverMap

    companion object {
        fun newInstance() = MapFragment()
    }

    private lateinit var viewModel: MapViewModel

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
            viewLifecycleOwner,
            Observer { stores ->
                stores?.let {
                    Log.i(TAG, "Got stores ${stores.size}")
                }
            }
        )
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
    }
}