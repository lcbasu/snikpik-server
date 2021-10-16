package com.server.common.model

import com.server.common.enums.CredentialType

data class Credentials(
    private val type: CredentialType? = null,
    private val decodedToken: Any? = null,
    private val idToken: String? = null,
    private val session: String? = null
)
