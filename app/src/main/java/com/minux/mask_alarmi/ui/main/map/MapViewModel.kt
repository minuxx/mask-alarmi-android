package com.minux.mask_alarmi.ui.main.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minux.mask_alarmi.data.repository.StoreRepositoryImpl
import com.minux.mask_alarmi.domain.model.Store
import kotlinx.coroutines.launch

//37.4708038, 126.9174509, 5000

class MapViewModel : ViewModel() {
    private val storeRepository = StoreRepositoryImpl.get()
    val storeListLiveData = storeRepository.getStoresByGeo(37.6470525, 127.0333818, 1000)

    fun getStoreByCode(storeCode: Long): Store? {
        return storeListLiveData.value?.firstOrNull { it.code == storeCode }
    }
//    init {
//        viewModelScope.launch {
//            storeRepository.insertStoresFromJson()
//        }
//    }
}