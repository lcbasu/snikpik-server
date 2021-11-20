package com.server.common.utils

import com.server.dk.dto.PhoneVerificationResponse
import com.twilio.rest.lookups.v1.PhoneNumber
import io.sentry.Sentry

object CommonUtils {
    var STRING_SEPARATOR = "_-_"

    fun getStringWithOnlyCharOrDigit(str: String): String {
        return str.filter { it.isLetterOrDigit() }
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
            Sentry.captureException(e)
            PhoneVerificationResponse(
                valid = false
            )
        }
    }
}

