package com.dukaankhata.server.model

data class FirebaseAuthUser(
        private val serialVersionUID: Long = 4408418647685225829L,
        private val uid: String? = null,
        private val name: String? = null,
        private val email: String? = null,
        private val isEmailVerified: Boolean = false,
        private val issuer: String? = null,
        private val picture: String? = null,
)
