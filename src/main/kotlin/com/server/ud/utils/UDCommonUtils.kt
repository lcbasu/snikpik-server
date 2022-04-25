package com.server.ud.utils

import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object UDCommonUtils {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    const val randomLocationId = "LOC_RANDOM"
    const val randomLocationZipcode = "ZZZZZZ"
    const val randomLocationName = "Global"
    const val DEFAULT_PAGING_STATE_VALUE = "NOT_SET"
    const val UNBOX_ROOT_URL = "https://letsunbox.in";

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


        "+911234560199" to "523978",
        "+911234560299" to "523978",
        "+911234560399" to "523978",
        "+911234560499" to "523978",
        "+911234560599" to "523978",
        "+911234560699" to "523978",
        "+911234560799" to "523978",
        "+911234560899" to "523978",
        "+911234560999" to "523978",
        "+911234561099" to "523978",
        "+911234561199" to "523978",
        "+911234561299" to "523978",
        "+911234561399" to "523978",
        "+911234561499" to "523978",
        "+911234561599" to "523978",
        "+911234561699" to "523978",
        "+911234561799" to "523978",
        "+911234561899" to "523978",
        "+911234561999" to "523978",
        "+911234562099" to "523978",
        "+911234562199" to "523978",
        "+911234562299" to "523978",
        "+911234562399" to "523978",
        "+911234562499" to "523978",
        "+911234562599" to "523978",
        "+911234562699" to "523978",
        "+911234562799" to "523978",
        "+911234562899" to "523978",
        "+911234562999" to "523978",
        "+911234563099" to "523978",
        "+911234563199" to "523978",
        "+911234563299" to "523978",
        "+911234563399" to "523978",
        "+911234563499" to "523978",
        "+911234563599" to "523978",
        "+911234563699" to "523978",
        "+911234563799" to "523978",
        "+911234563899" to "523978",
        "+911234563999" to "523978",
        "+911234564099" to "523978",
        "+911234564199" to "523978",
        "+911234564299" to "523978",
        "+911234564399" to "523978",
        "+911234564499" to "523978",
        "+911234564599" to "523978",
        "+911234564699" to "523978",
        "+911234564799" to "523978",
        "+911234564899" to "523978",
        "+911234564999" to "523978",
        "+911234565099" to "523978",

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

