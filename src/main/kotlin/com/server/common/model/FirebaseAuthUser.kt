package com.server.common.model

data class FirebaseAuthUser(
        private val serialVersionUID: Long = 4408418647685225829L,
        private val uid: String? = null,
        private val name: String? = null,
        private val absoluteMobile: String? = null,
        private val issuer: String? = null,
        private val picture: String? = null,
) {
    // Adding getters so that we can access these values when we cast them from
    // Java Object to Kotlin Object
    fun getAbsoluteMobileNumber(): String? {
        return absoluteMobile
    }

    fun getUid(): String? {
        return uid
    }
}