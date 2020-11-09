package com.abreem.geoposition

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OnePlaceFeature(
    @field:Json(name ="id") val id: String,
    @field:Json(name="place_name")val placeName: String
)