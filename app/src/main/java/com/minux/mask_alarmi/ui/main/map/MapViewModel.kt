package com.minux.mask_alarmi.ui.main.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minux.mask_alarmi.data.repository.MaskAlarmiRepositoryImpl
import com.minux.mask_alarmi.data.models.Store
import com.naver.maps.geometry.LatLng
import kotlinx.coroutines.launch

private const val TAG = "MapViewModel"
private const val RADIUS_METER = 1000

class MapViewModel : ViewModel() {
    private val repository = MaskAlarmiRepositoryImpl.get()
    private val _stores: MutableLiveData<List<Store>> = MutableLiveData()
    val stores: LiveData<List<Store>> get() = _stores

    private val _searchedLatLng: MutableLiveData<LatLng> = MutableLiveData()
    val searchedLatLng: LiveData<LatLng> get() = _searchedLatLng

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage: MutableLiveData<String> = MutableLiveData("")
    val errorMessage: LiveData<String> get() = _errorMessage


    fun getStoresByGeo(latLng: LatLng) {
        _isLoading.postValue(true)
        repository.getStoresByGeo(
            latLng.latitude,
            latLng.longitude,
            RADIUS_METER,
            onSuccess = { stores ->
                _stores.postValue(stores)
                _isLoading.postValue(false)
            }, onFailure = { _ ->
                _isLoading.postValue(false)
            }
        )
    }

    fun getStoreByCode(storeCode: Long): Store? {
        return stores.value?.firstOrNull { it.code == storeCode }
    }

    fun searchAddress(searchAddr: String, lat: Double, lng: Double) {
        _isLoading.postValue(true)
        repository.searchAddress(searchAddr, lat, lng,
            onSuccess = { searchedAddr ->
                Log.i(TAG, "$searchedAddr")
                searchedAddr?.let {
                    _searchedLatLng.postValue(LatLng(it.latitude, it.longitude))
                    _isLoading.postValue(false)
                }
            },
            onFailure = { errorMessage ->
                _errorMessage.postValue(errorMessage)
                _isLoading.postValue(false)
            }
        )
    }

    init {
        viewModelScope.launch {
            repository.insertStoresFromJson()
        }
    }
}