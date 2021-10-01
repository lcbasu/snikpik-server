package com.dukaankhata.server.entities

import javax.persistence.*

@Entity
class Address : Auditable() {
    @Id
    @Column(unique = true)
    var id: String = ""
    var line1: String? = null
    var line2: String? = null
    var zipcode: String? = null
    var city: String? = null
    var state: String? = null
    var country: String? = null
    var googleCode: String? = null // Unique code for that location
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    // Every saveAddress should have a phone number
    var absoluteMobile: String? = "" // Phone Number with country code
    var countryCode: String? = "" // Country code
}
