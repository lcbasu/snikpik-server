package com.server.common.utils

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.dto.PhoneVerificationResponse
import com.twilio.rest.lookups.v1.PhoneNumber
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object CommonUtils {
    const val STRING_SEPARATOR = "_-_"

    private fun convertToAlphaNumeric(str: String): String {
        return str.replace("[^A-Za-z0-9]".toRegex(), "");
    }

    private fun convertToValidUsernameCharacters(str: String): String {
        return str.replace("[^A-Za-z0-9_.]".toRegex(), "");
    }

    fun getLowercaseStringWithOnlyCharOrDigit(str: String): String {
        return (convertToAlphaNumeric(str)).toLowerCase().filter { it.isLetterOrDigit() }
    }

    fun getLowercaseUsername(str: String): String {
        return (convertToValidUsernameCharacters(str)).toLowerCase().filter { it.isLetterOrDigit() || it == '_' || it == '.' }
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

    fun convertToStringBlob(obj: Any?) = jacksonObjectMapper().writeValueAsString(obj)

    fun convertJsonFormat(jsonObject: JSONObject): JsonNode {
        val ret = JsonNodeFactory.instance.objectNode()
        val iterator = jsonObject.keys()
        while (iterator.hasNext()) {
            val key = iterator.next()
            var value: Any
            value = try {
                jsonObject[key]
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            if (jsonObject.isNull(key)) ret.putNull(key) else if (value is String) ret.put(
                key,
                value
            ) else if (value is Int) ret.put(
                key,
                value
            ) else if (value is Long) ret.put(key, value) else if (value is Double) ret.put(
                key,
                value
            ) else if (value is Boolean) ret.put(
                key,
                value
            ) else if (value is JSONObject) ret.put(key, convertJsonFormat(value)) else if (value is JSONArray) ret.put(
                key, convertJsonFormat(
                    value
                )
            ) else throw RuntimeException("not prepared for converting instance of class " + value.javaClass)
        }
        return ret
    }

    fun convertJsonFormat(json: JSONArray): JsonNode {
        val ret = JsonNodeFactory.instance.arrayNode()
        for (i in 0 until json.length()) {
            var value: Any
            value = try {
                json[i]
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            if (json.isNull(i)) ret.addNull() else if (value is String) ret.add(value) else if (value is Int) ret.add(
                value
            ) else if (value is Long) ret.add(value) else if (value is Double) ret.add(value) else if (value is Boolean) ret.add(
                value
            ) else if (value is JSONObject) ret.add(convertJsonFormat(value)) else if (value is JSONArray) ret.add(
                convertJsonFormat(
                    value
                )
            ) else throw RuntimeException("not prepared for converting instance of class " + value.javaClass)
        }
        return ret
    }
//    fun getStopWords(): Set<String> {
//
//    }
}

