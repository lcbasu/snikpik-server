package com.server.sp.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

@JsonIgnoreProperties(ignoreUnknown = true)
data class PhoneNumberTaggedUserDetail (
    val name: String,
    val phoneNumber: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MomentTaggedUserDetails (
    val taggedUserIds: Set<String>,
    val taggedPhoneNumbers: Set<PhoneNumberTaggedUserDetail>,
)

fun MomentTaggedUserDetails.convertToString(): String {
    this.apply {
        return try {
            jacksonObjectMapper().writeValueAsString(this)
        } catch (e: Exception) {
            ""
        }
    }
}

fun String.toMomentTaggedUserDetails(): MomentTaggedUserDetails {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(this, MomentTaggedUserDetails::class.java)
        } catch (e: Exception) {
            MomentTaggedUserDetails(emptySet(), emptySet())
        }
    }
}
