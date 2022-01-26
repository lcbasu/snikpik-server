package com.server.dk.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.common.model.RequestContext

@JsonIgnoreProperties(ignoreUnknown = true)
data class RequestContextResponse(
    val userId: String,
    val uid: String?,
    val anonymous: Boolean,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SendOTPRequest(
    val countryCode: String,
    val absoluteMobileNumber: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class OTPSentResponse(
    val countryCode: String,
    val absoluteMobileNumber: String,
    val sent: Boolean,
    val loginSequenceId: String? = null, // Null if the OTP sending failed
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class LoginRequest(
    val countryCode: String,
    val absoluteMobileNumber: String,
    val otp: String,
    val loginSequenceId: String, // This is used for validation
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class LoginResponse(
    val authenticated: Boolean,
    val errorMessage: String? = null, // In case authentication failed
    val token: String? = null,
    val userId: String? = null,
    val loginSequenceId: String? = null,
    val absoluteMobileNumber: String? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class LoginResponseV2(
    val authenticated: Boolean,
    val errorMessage: String? = null, // In case authentication failed
    val firebaseCustomToken: String? = null,
    val userId: String? = null,
    val loginSequenceId: String? = null,
    val absoluteMobileNumber: String? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class RefreshTokenRequest(
    val loginSequenceId: String, // This is the id that was used during the sign in
    val token: String, // This is the old expired token
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TokenRefreshResponse(
    val oldLoginSequenceId: String,
    val refreshed: Boolean,
    val oldToken: String, // old token
    val errorMessage: String? = null, // In case authentication failed
    val newToken: String? = null, // new token
    val newLoginSequenceId: String? = null, // non-null in case of successful refresh
    val userId: String? = null,
    val absoluteMobileNumber: String? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class LogoutRequest(
    val loginSequenceId: String, // Doing this so that even if someone has the token, he can not just logout. YOu need this id
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class LogoutResponse(
    val loggedOut: Boolean,
    val errorMessage: String? = null, // In case logout failed
)

fun RequestContext.toRequestContextResponse(): RequestContextResponse {
    this.apply {
        return RequestContextResponse(
            userId = user.id,
            uid = user.uid,
            anonymous = user.anonymous
        )
    }
}
