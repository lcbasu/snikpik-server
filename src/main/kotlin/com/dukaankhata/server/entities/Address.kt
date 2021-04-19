package com.dukaankhata.server.entities

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Address : Auditable() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = -1
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
    var phone: String? = null
}
