package com.server.common.model

data class UserDetailsForToken(
        private val uid: String,
        private val absoluteMobile: String? = null,
) {
    fun getAbsoluteMobileNumber(): String? {
        return absoluteMobile
    }

    fun getUid(): String {
        return uid
    }
}
