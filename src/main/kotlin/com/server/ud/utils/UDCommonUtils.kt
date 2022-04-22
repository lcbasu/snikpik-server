package com.server.ud.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.shop.model.VariantProperties
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object UDCommonUtils {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    const val randomLocationId = "LOC_RANDOM"
    const val randomLocationZipcode = "ZZZZZZ"
    const val randomLocationName = "Global"
    const val DEFAULT_PAGING_STATE_VALUE = "NOT_SET"

    val fixedLoginOTPMap = mapOf(
        // Team Numbers
        "+919037023737" to "523978",
        "+919742097429" to "111111",
        "+917012096369" to "111111",

        // Dummy Numbers
        "+911234567890" to "523978",
        "+911234567891" to "523978",
        "+911234567892" to "523978",
        "+911234567893" to "523978",
        "+911234567894" to "523978",
        "+911234567895" to "523978",
        "+911234567896" to "523978",
        "+911234567897" to "523978",
        "+911234567898" to "523978",
        "+911234567899" to "523978",


        "+91123450199" to "523978",
        "+91123450299" to "523978",
        "+91123450399" to "523978",
        "+91123450499" to "523978",
        "+91123450599" to "523978",
        "+91123450699" to "523978",
        "+91123450799" to "523978",
        "+91123450899" to "523978",
        "+91123450999" to "523978",
        "+91123451099" to "523978",
        "+91123451199" to "523978",
        "+91123451299" to "523978",
        "+91123451399" to "523978",
        "+91123451499" to "523978",
        "+91123451599" to "523978",
        "+91123451699" to "523978",
        "+91123451799" to "523978",
        "+91123451899" to "523978",
        "+91123451999" to "523978",
        "+91123452099" to "523978",
        "+91123452199" to "523978",
        "+91123452299" to "523978",
        "+91123452399" to "523978",
        "+91123452499" to "523978",
        "+91123452599" to "523978",
        "+91123452699" to "523978",
        "+91123452799" to "523978",
        "+91123452899" to "523978",
        "+91123452999" to "523978",
        "+91123453099" to "523978",
        "+91123453199" to "523978",
        "+91123453299" to "523978",
        "+91123453399" to "523978",
        "+91123453499" to "523978",
        "+91123453599" to "523978",
        "+91123453699" to "523978",
        "+91123453799" to "523978",
        "+91123453899" to "523978",
        "+91123453999" to "523978",
        "+91123454099" to "523978",
        "+91123454199" to "523978",
        "+91123454299" to "523978",
        "+91123454399" to "523978",
        "+91123454499" to "523978",
        "+91123454599" to "523978",
        "+91123454699" to "523978",
        "+91123454799" to "523978",
        "+91123454899" to "523978",
        "+91123454999" to "523978",
        "+91123455099" to "523978",

        // External Users
        "+917222878888" to "235412", // -> Encasa Unbox
        "+919846168125" to "523149", // -> TAB
    )

    val admins = listOf(
        "USR6JAUjEQIJBTtuQdnkmmpcdQhVfz2", // Basith
        "USREg7UVXtfVeZ4aFQSBwKmvuFa46A3", // Lokesh
        "USRX8WhrKwlfDTfhcRnsjpndxjOhFj1", // Akshay
        "USR2S45nqK6GyUCKvgyBTM8J2NkMtC2", // Ibrahim
        "USRtGL01YytxhfPMpuS6apUwnXMjoi1", // Shamil
        )

    fun isAdmin(userId: String): Boolean {
        return admins.contains(userId)
    }

    fun getFileExtension(fileUrl: String): String {
        return try {
            val extensionStartIndex = fileUrl.lastIndexOf(".")
            fileUrl.substring(extensionStartIndex + 1)
        } catch (e: Exception) {
            logger.error("Error while getting file extension for fileUrl: $fileUrl")
            ""
        }
    }

    fun isValidString (str: String?): Boolean {
        if (str == null || str.isEmpty() || str.isBlank()) {
            return false
        }
        return true
    }

    fun lockedUsernames (): Set<String> {
        return setOf(
            "kairalitmt",
            "steelex",
            "malabardevelopers",
            "lulugroup",
            "ikea",
            "asianpaints",
            "godrej",
            "prestige",
            "whirlpool",
            "samsung",
            "ultratechcement",
            "ambujacement",
            "tata",
            "birla",
            "bergerpaints",
            "nipponpaint",
            "kansainerolacpaints",
            "jswsteel",
            "sail",
            "tatasteel",
            "fevicol",
            "drfixit",
            "usha",
            "wipro",
            "havells",
            "philips",
            "anchor",
            "legrand",
            "polycabwires",
            "finolexcables",
            "prestige",
            "jrc",
            "kajaria",
            "jaguar",
            "pepperfry",
            "Hindware",
            "cera",
            "kohler",
            "cromptongreaves",
            "vguard",
            "homecentre",
            "urbanladder",
            "somany",
            "centuryply",
        )
    }

    fun getOtp(length: Int): String {
        return (1..length).map { (0..9).random() }.joinToString("")
    }

    fun getSha256Hash(str: String): String {
        return DigestUtils.sha256Hex(str)
    }

}

