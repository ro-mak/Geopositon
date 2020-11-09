package com.abreem.geoposition

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class GeoProvider {

    companion object {
        const val ACCESS_TOKEN =
            ""
    }

    var activity: AppCompatActivity? = null
    var locationListener: ILocationListener? = null
    var placeNameListener: IPlaceNameListener? = null
    private val locationRequest = LocationRequest().apply {
        this.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        this.interval = 5000
        this.fastestInterval = 100
    }
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val gpsBroadcastReceiver = GeoLocationBroadcastReceiver(::isGpsOn)
    private lateinit var locationCallback: LocationCallback
    var lastKnownLocation: Location? = null

    fun bindActivity(activity: AppCompatActivity) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        this.activity = activity
    }

    private fun unbindActivity() {
        this.activity = null
    }

    fun getPlaceName(location: Location): Observable<PlaceFeatures> {
        return NetworkManager.getMapBoxService()
            .getPlaceNameFromCoordinates("${location.longitude},${location.latitude}", ACCESS_TOKEN)
    }

    fun onStart() {
        registerBroadcastReceiver()
        loadLastKnownLocation { location ->
            lastKnownLocation = location
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                lastKnownLocation = locationResult.locations.last().also {
                    Log.d(
                        "GeoProvider",
                        "Location update ${it.latitude} ${it.longitude}}"
                    )
//                    getPlaceName(it).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({ place->
//                        val placeName = place.features[0].placeName
//                        Log.d("GeoProvider", placeName)
//                        placeNameListener?.getPlaceNameUpdate(placeName)
//                    },{
//                        Log.e("GeoProvider", "Error", it)
//                    })
                    locationListener?.listenToLocationUpdates(it)
                }

            }
        }
    }

    private fun isGpsOn(isOn: Boolean) {
        if (isOn) {
            startLocationRequests()
        } else {
            stopLocationRequests()
        }
    }

    fun stopLocationRequests() {
        Log.d(
            "GeoProvider",
            "stopLocationRequests"
        )
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun registerBroadcastReceiver() {
        val intentFilter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        activity?.registerReceiver(gpsBroadcastReceiver, intentFilter)
    }

    @SuppressLint("MissingPermission")
    fun loadLastKnownLocation(listenForLastKnownLocation: (Location?) -> Unit) {
        fusedLocationClient.lastLocation.addOnSuccessListener {
            listenForLastKnownLocation(it)
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationRequests() {
        Log.d(
            "GeoProvider",
            "startLocationRequests"
        )
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun onStop() {
        activity?.unregisterReceiver(gpsBroadcastReceiver)
        unbindActivity()
    }

}

interface ILocationListener {
    fun listenToLocationUpdates(location: Location)
}

interface IPlaceNameListener{
    fun getPlaceNameUpdate(placeName:String)
}