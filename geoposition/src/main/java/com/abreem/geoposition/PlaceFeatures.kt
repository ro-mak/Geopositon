package com.abreem.geoposition

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaceFeatures(
    @field:Json(name = "features") val features : List<OnePlaceFeature>
)