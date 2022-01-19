package com.server.common.model

data class UserDetailsForToken(
        val uid: String,
        val absoluteMobile: String? = null,
)


data class UserDetailsFromUDTokens(
        val token: String,
        val uid: String,
        val absoluteMobile: String,
)
