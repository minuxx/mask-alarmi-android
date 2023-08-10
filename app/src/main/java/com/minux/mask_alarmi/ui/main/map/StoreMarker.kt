package com.minux.mask_alarmi.ui.main.map

import com.minux.mask_alarmi.R
import com.minux.mask_alarmi.domain.model.RemainState
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage


class StoreMarker {
    companion object {
        fun newMarker(latLng: LatLng, remainState: RemainState) : Marker {
            val marker = Marker()
            marker.position = latLng
            marker.icon = OverlayImage.fromResource(when(remainState) {
                RemainState.EMPTY -> R.drawable.ic_empty_small
                RemainState.FEW -> R.drawable.ic_few_small
                RemainState.SOME -> R.drawable.ic_some_small
                RemainState.PLENTY -> R.drawable.ic_plenty_small
            })

            return marker
        }
    }
}