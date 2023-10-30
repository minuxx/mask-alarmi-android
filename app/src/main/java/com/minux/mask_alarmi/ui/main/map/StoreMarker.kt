package com.minux.mask_alarmi.ui.main.map

import com.minux.mask_alarmi.R
import com.minux.mask_alarmi.data.models.RemainState
import com.minux.mask_alarmi.data.models.Store
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage

private const val TAG = "StoreMarker"

class StoreMarker(
    val store: Store,
    private val onSelectStoreMarker: (store: Store) -> Unit
) {
    val marker = Marker()
    var isSelected = false
        set(value) {
            field = value
            marker.icon = OverlayImage.fromResource(getMarkerIconRes(store.remainState, field))
        }

    fun newInstance(): StoreMarker {
        marker.position = LatLng(store.lat, store.lng)
        marker.icon = OverlayImage.fromResource(getMarkerIconRes(store.remainState))
        marker.setOnClickListener {
            onSelectStoreMarker(store)
            true
        }
        return this
    }

    private fun getMarkerIconRes(remainState: RemainState, isClicked: Boolean = false): Int {
        return when(remainState) {
            RemainState.EMPTY -> if (isClicked) R.drawable.ic_empty_activate else R.drawable.ic_empty_small
            RemainState.FEW -> if (isClicked) R.drawable.ic_few_activate else R.drawable.ic_few_small
            RemainState.SOME -> if (isClicked) R.drawable.ic_some_activate else R.drawable.ic_some_small
            RemainState.PLENTY -> if (isClicked) R.drawable.ic_plenty_activate else R.drawable.ic_plenty_small
        }
    }
}