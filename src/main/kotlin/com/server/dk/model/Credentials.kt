package com.server.dk.model

import com.server.dk.enums.CredentialType
import com.google.firebase.auth.FirebaseToken

data class Credentials(
        private val type: CredentialType? = null,
        private val decodedToken: Any? = null,
        private val idToken: String? = null,
        private val session: String? = null
)
