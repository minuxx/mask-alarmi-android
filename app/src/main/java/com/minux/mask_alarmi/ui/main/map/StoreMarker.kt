package com.minux.mask_alarmi.ui.main.map

import com.minux.mask_alarmi.R
import com.minux.mask_alarmi.domain.model.RemainState
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage


class StoreMarker(
    private val storeCode: Long,
    private val remainState: RemainState,
    private val coordinate: LatLng,
    val onMarkerClicked: (storeCode: Long, isClicked: Boolean) -> Unit
) {
    private val marker = Marker()
    private var isClicked = false

    fun build(): Marker {
        marker.position = coordinate
        marker.icon = OverlayImage.fromResource(getMarkerDrawableIcon(remainState))
        marker.setOnClickListener {
            isClicked = !isClicked
            marker.icon = OverlayImage.fromResource(getMarkerDrawableIcon(remainState, isClicked))
            onMarkerClicked(storeCode, isClicked)
            true
        }
        return marker
    }

    private fun getMarkerDrawableIcon(remainState: RemainState, isClicked: Boolean = false): Int {
        return when(remainState) {
            RemainState.EMPTY -> if (isClicked) R.drawable.ic_empty_activate else R.drawable.ic_empty_small
            RemainState.FEW -> if (isClicked) R.drawable.ic_few_activate else R.drawable.ic_few_small
            RemainState.SOME -> if (isClicked) R.drawable.ic_some_activate else R.drawable.ic_some_small
            RemainState.PLENTY -> if (isClicked) R.drawable.ic_plenty_activate else R.drawable.ic_plenty_small
        }
    }
}