package com.cinecompare.aggregator.base.models

data class City(
    val id: String,
    val name: String,
    val state: String,
    val lat: Double,
    val lng: Double,
    val bmsCode: String = "",
    val districtCode: String = ""
)

val POPULAR_CITIES = listOf(
    City("MUM", "Mumbai", "Maharashtra", 19.0760, 72.8777, bmsCode = "MUMBAI", districtCode = "mumbai"),
    City("DEL", "Delhi", "Delhi", 28.6139, 77.2090, bmsCode = "NCR", districtCode = "delhi"),
    City("BLR", "Bengaluru", "Karnataka", 12.9716, 77.5946, bmsCode = "BANG", districtCode = "bengaluru"),
    City("HYD", "Hyderabad", "Telangana", 17.3850, 78.4867, bmsCode = "HYD", districtCode = "hyderabad"),
    City("CHN", "Chennai", "Tamil Nadu", 13.0827, 80.2707, bmsCode = "CHEN", districtCode = "chennai"),
    City("KOL", "Kolkata", "West Bengal", 22.5726, 88.3639, bmsCode = "KOLKATA", districtCode = "kolkata"),
    City("PUN", "Pune", "Maharashtra", 18.5204, 73.8567, bmsCode = "PUNE", districtCode = "pune"),
    City("AMD", "Ahmedabad", "Gujarat", 23.0225, 72.5714, bmsCode = "AHMEDABAD", districtCode = "ahmedabad"),
    City("JPR", "Jaipur", "Rajasthan", 26.9124, 75.7873, bmsCode = "JAI", districtCode = "jaipur"),
    City("LKN", "Lucknow", "Uttar Pradesh", 26.8467, 80.9462, bmsCode = "LKO", districtCode = "lucknow")
)
