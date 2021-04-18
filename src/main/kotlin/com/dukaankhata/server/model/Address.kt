package com.dukaankhata.server.model

data class Address(
    val line1: String?,
    val line2: String?,
    val zipcode: String?,
    val city: String?,
    val state: String?,
    val country: String?,
    val googleCode: String?, // Unique code for that location
    val lat: Double?,
    val long: Double?,
    val phone: String?
)
