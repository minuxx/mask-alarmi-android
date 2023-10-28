package com.minux.mask_alarmi.ui.main.map

import com.minux.mask_alarmi.R
import com.minux.mask_alarmi.data.models.RemainState
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage

private const val TAG = "StoreMarker"

class StoreMarker(
    val storeCode: Long,
    private val remainState: RemainState,
    private val coordinate: LatLng,
    val onMarkerClicked: (storeCode: Long, isClicked: Boolean) -> Unit
) {
    val marker = Marker()
    var isClicked = false
        set(value) {
            field = value
            marker.icon = OverlayImage.fromResource(getMarkerIconRes(remainState, field))
        }

    fun newInstance(): StoreMarker {
        marker.position = coordinate
        marker.icon = OverlayImage.fromResource(getMarkerIconRes(remainState))
        marker.setOnClickListener {
            isClicked = !isClicked
            onMarkerClicked(storeCode, isClicked)
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