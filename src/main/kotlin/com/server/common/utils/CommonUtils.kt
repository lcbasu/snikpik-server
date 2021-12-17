package com.server.common.utils

import com.server.dk.dto.PhoneVerificationResponse
import com.twilio.rest.lookups.v1.PhoneNumber

object CommonUtils {
    var STRING_SEPARATOR = "_-_"

    private fun convertToAlphaNumeric(str: String): String {
        return str.replace("[^A-Za-z0-9]".toRegex(), "");
    }

    fun getLowercaseStringWithOnlyCharOrDigit(str: String): String {
        return (convertToAlphaNumeric(str)).toLowerCase().filter { it.isLetterOrDigit() }
    }

    fun getSanitizePhoneNumber(absoluteMobile: String?): String? {
        var sanitizePhoneNumber = absoluteMobile
        if (sanitizePhoneNumber != null) {
            // To long is for removing the leading Zeros
            sanitizePhoneNumber = sanitizePhoneNumber
                .filter { it.isDigit() }
        }
        if (absoluteMobile != null && absoluteMobile.startsWith("+")) {
            sanitizePhoneNumber = "+$sanitizePhoneNumber"
        }
        return sanitizePhoneNumber
    }


    fun getVerifiedPhoneResponse(absoluteMobile: String): PhoneVerificationResponse {
        return try {
            val result = PhoneNumber.fetcher(com.twilio.type.PhoneNumber(absoluteMobile)).fetch()
            PhoneVerificationResponse(
                valid = true,
                countryCode = result.countryCode,
                numberInNationalFormat = result.nationalFormat,
                numberInInterNationalFormat = result.phoneNumber.toString()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            PhoneVerificationResponse(
                valid = false
            )
        }
    }

//    fun getStopWords(): Set<String> {
//
//    }
}

