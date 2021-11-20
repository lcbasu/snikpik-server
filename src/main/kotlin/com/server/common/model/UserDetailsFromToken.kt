package com.server.common.model

data class UserDetailsFromToken(
        private val serialVersionUID: Long = 4408418647685225829L,
        private val uid: String,
        private val name: String? = null,
        private val absoluteMobile: String? = null,
        private val handle: String? = null,
        private val email: String? = null,
        private val issuer: String? = null,
        private val picture: String? = null,
        private val anonymous: Boolean? = true,
) {
    // Adding getters so that we can access these values when we cast them from
    // Java Object to Kotlin Object
    fun getAbsoluteMobileNumber(): String? {
        return absoluteMobile
    }

    fun getHandle(): String? {
        return handle
    }

    fun getEmail(): String? {
        return email
    }

    fun getName(): String? {
        return name
    }

    fun getUid(): String {
        return uid
    }

    fun getIsAnonymous(): Boolean? {
        return anonymous
    }

    fun getPicture(): String? {
        return picture
    }

}
