package com.abreem.geoposition

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager

class GeoLocationBroadcastReceiver(val isGPSOn: (Boolean) -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val manager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        val action = intent?.action
        if (action == "android.location.PROVIDERS_CHANGED") {
            manager?.let {
                isGPSOn(
                    it.isProviderEnabled(LocationManager.GPS_PROVIDER)
                            || it.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                            || it.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)
                )
            } ?: isGPSOn(false)
        }
    }
}