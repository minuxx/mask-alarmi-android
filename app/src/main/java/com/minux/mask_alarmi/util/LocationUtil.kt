package com.minux.mask_alarmi.util

import android.Manifest
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.util.FusedLocationSource

private const val TAG = "LocationUtil"
private const val INTERVAL_MILLIS = 5000L
private const val MIN_DISTANCE_METER = 10.0F
private const val LOCATION_PERMISSION_REQUEST_CODE = 123

class LocationUtil(private val activity: AppCompatActivity) {
    private var fusedLocationClient: FusedLocationProviderClient
    private var fusedLocationSource: FusedLocationSource
    private var locationRequest: LocationRequest
    private var locationCallback:LocationCallback

    init {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, INTERVAL_MILLIS).apply {
            setMinUpdateDistanceMeters(MIN_DISTANCE_METER)
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations){
                    Log.i(TAG, "latitude: ${location.latitude}, longitude: ${location.longitude}")
                }
            }
        }

        fusedLocationSource= FusedLocationSource(activity, LOCATION_PERMISSION_REQUEST_CODE)
    }

    private fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) ==PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) ==PackageManager.PERMISSION_GRANTED
        ) {
            Log.i(TAG, "Location Permission Granted")
            return true
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            Log.i(TAG, "Location Permission Denied")
            return false
        }
    }

    fun startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates")
        if (checkLocationPermission()) {
            fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper())
        }
    }

    fun stopLocationUpdates() {
        Log.d(TAG, "stopLocationUpdates")
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    fun getLastLocation(lastLocationCallback: (LatLng?) -> Unit) {
        if (checkLocationPermission()) {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                Log.i(TAG, "latitude: ${it.latitude}, longitude: ${it.longitude}")
                lastLocationCallback(LatLng(it.latitude, it.longitude))
            }.addOnFailureListener {
                lastLocationCallback(null)
            }
        } else {
            lastLocationCallback(null)
        }
    }

    fun getLocationSource(): FusedLocationSource? {
        return if (checkLocationPermission()) {
            fusedLocationSource
        } else {
            null
        }
    }
}