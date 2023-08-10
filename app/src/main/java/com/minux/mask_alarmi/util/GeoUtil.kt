package com.minux.mask_alarmi.util

import java.lang.Math.atan2
import java.lang.Math.cos
import java.lang.Math.sin
import java.lang.Math.sqrt

object GeoUtil {
    fun calculateHaversine(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val R = 6371.0 // 지구 반지름 (단위: km)

        val lat1Rad = Math.toRadians(lat1)
        val lat2Rad = Math.toRadians(lat2)
        val latDiff = Math.toRadians(lat2 - lat1)
        val lngDiff = Math.toRadians(lng2 - lng1)

        val a = sin(latDiff / 2) * sin(latDiff / 2) +
                cos(lat1Rad) * cos(lat2Rad) *
                sin(lngDiff / 2) * sin(lngDiff / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return R * c * 1000 // m 단위 변환
    }
}