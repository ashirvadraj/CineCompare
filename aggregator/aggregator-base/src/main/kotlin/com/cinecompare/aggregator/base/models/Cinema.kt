package com.cinecompare.aggregator.base.models

data class Cinema(
    val id: String,
    val name: String,
    val address: String,
    val city: String,
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val amenities: List<String> = emptyList(),
    val distanceKm: Float = 0f
)
