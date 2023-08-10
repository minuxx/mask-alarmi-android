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
    val onMarkerClicked: (storeCode: Long) -> Unit
) {
    private val marker = Marker()

    fun newMarker(): Marker {
        marker.position = coordinate
        marker.icon = OverlayImage.fromResource(when(remainState) {
            RemainState.EMPTY -> R.drawable.ic_empty_small
            RemainState.FEW -> R.drawable.ic_few_small
            RemainState.SOME -> R.drawable.ic_some_small
            RemainState.PLENTY -> R.drawable.ic_plenty_small
        })

        marker.setOnClickListener {
            marker.icon = OverlayImage.fromResource(when(remainState) {
                RemainState.EMPTY -> R.drawable.ic_empty_activate
                RemainState.FEW -> R.drawable.ic_few_activate
                RemainState.SOME -> R.drawable.ic_some_activate
                RemainState.PLENTY -> R.drawable.ic_plenty_activate
            })

            onMarkerClicked(storeCode)
            true
        }

        return marker
    }
}