package com.abreem.geoposition

import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


object NetworkManager {

    fun getRetrofit() = Retrofit.Builder()
        .baseUrl("https://api.mapbox.com/")
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    fun getMapBoxService() =
        getRetrofit().create(MapBoxService::class.java)
}

interface MapBoxService {

    @GET("/geocoding/v5/mapbox.places/{coords}.json")
    fun getPlaceNameFromCoordinates(@Path("coords") coords: String, @Query("access_token") accessToken: String): Observable<PlaceFeatures>

}