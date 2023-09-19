package com.minux.mask_alarmi.ui.main.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide.init
import com.minux.mask_alarmi.data.repository.StoreRepositoryImpl
import com.minux.mask_alarmi.domain.model.Address
import com.minux.mask_alarmi.domain.model.Store
import com.naver.maps.geometry.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "MapViewModel"
private const val RADIUS_METER = 1000

class MapViewModel : ViewModel() {
    private val storeRepository = StoreRepositoryImpl.get()
    private val _stores: MutableLiveData<List<Store>> = MutableLiveData()
    val stores: LiveData<List<Store>> get() = _stores

    private val _searchedLatLng: MutableLiveData<LatLng> = MutableLiveData()
    val searchedLatLng: LiveData<LatLng> get() = _searchedLatLng


    fun getStoresByGeo(latLng: LatLng) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                storeRepository.getStoresByGeo(latLng.latitude, latLng.longitude, RADIUS_METER)
            }
            _stores.postValue(result)
        }
    }

    fun getStoreByCode(storeCode: Long): Store? {
        return stores.value?.firstOrNull { it.code == storeCode }
    }

    fun searchAddress(address: String, lat: Double, lng: Double) {
        storeRepository.searchAddress(address, lat, lng) { address ->
            Log.i(TAG, "$address")
            address?.let {
                _searchedLatLng.postValue(LatLng(it.latitude, it.longitude))
            }
        }
    }

    init {
        viewModelScope.launch {
            storeRepository.insertStoresFromJson()
        }
    }
}