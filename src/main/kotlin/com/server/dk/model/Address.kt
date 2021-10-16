package com.server.dk.model

data class Address(
    val line1: String = "",
    val line2: String = "",
    val zipcode: String = "",
    val city: String = "",
    val state: String = "",
    val country: String = "",
    val googleCode: String = "", // Unique code for that location
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    var absoluteMobile: String? = "", // Phone Number with country code
    var countryCode: String? = "" // Country code
)

