package com.server.common.model

import com.server.common.enums.CredentialType
import com.server.common.enums.ReadableIdPrefix

data class UserDetailsFromToken(
    private val serialVersionUID: Long = 4408418647685225829L,
    private val token: String,
    private val uid: String,
    private val type: CredentialType,
    private val name: String? = null,
    private val absoluteMobile: String? = null,
//        private val handle: String? = null,
    private val email: String? = null,
    private val issuer: String? = null,
    private val picture: String? = null,
    private val anonymous: Boolean? = true,
) {

    fun getToken(): String {
        return token
    }

    // Adding getters so that we can access these values when we cast them from
    // Java Object to Kotlin Object
    fun getAbsoluteMobileNumber(): String? {
        return absoluteMobile
    }

//    fun getHandle(): String? {
//        return handle
//    }

    fun getEmail(): String? {
        return email
    }

    fun getName(): String? {
        return name
    }

    fun getUid(): String {
        return uid
    }

    fun getUserIdToUse(): String {
        return if (type == CredentialType.ID_TOKEN_UNBOX) {
            uid
        } else {
            "${ReadableIdPrefix.USR.name}$uid"
        }
    }

    fun getIsAnonymous(): Boolean? {
        return anonymous
    }

    fun getPicture(): String? {
        return picture
    }

}
