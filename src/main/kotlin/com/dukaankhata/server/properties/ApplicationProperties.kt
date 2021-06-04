package com.dukaankhata.server.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("aws")
data class AwsProperties(var awsKey: String? = null, var awsSecret: String? = null)

@ConstructorBinding
@ConfigurationProperties("payments")
data class PaymentProperties(val razorpay: RazorpayProperties) {
    data class RazorpayProperties(var key: String, var secret: String? = null)
}

@ConstructorBinding
@ConfigurationProperties("security")
data class SecurityProperties(
    var cookieProps: CookieProperties? = null,
    var firebaseProps: FirebaseProperties? = null,
    var allowCredentials: Boolean = false,
    var allowedOrigins: List<String>? = null,
    var allowedHeaders: List<String>? = null,
    var exposedHeaders: List<String>? = null,
    var allowedMethods: List<String>? = null,
    var allowedPublicApis: List<String>? = null
) {
    data class CookieProperties(var domain: String? = null,
                                var path: String? = null,
                                var httpOnly: Boolean = false,
                                var secure: Boolean = false,
                                var maxAgeInMinutes: Int = 0)
    data class FirebaseProperties(
        var sessionExpiryInDays: Int = 0,
        var databaseUrl: String? = null,
        var storageBucket: String? = null,
        var enableStrictServerSession: Boolean = false,
        var enableCheckSessionRevoked: Boolean = false,
        var enableLogoutEverywhere: Boolean = false,

        )

}

@ConstructorBinding
@ConfigurationProperties("twilio")
data class TwilioProperties(var accountSid: String? = null, var authToken: String? = null)

@ConstructorBinding
@ConfigurationProperties("unsplash")
data class UnsplashProperties(var clientId: String, var accessKey: String, val secretKey: String)
