package com.minux.mask_alarmi.ui.main.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minux.mask_alarmi.data.repository.StoreRepository
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {
    private val storeRepository = StoreRepository.get()
    val storeListLiveData = storeRepository.getStoresByGeo()

    init {
        viewModelScope.launch {
            storeRepository.insertStoresFromJson()
        }
    }
}