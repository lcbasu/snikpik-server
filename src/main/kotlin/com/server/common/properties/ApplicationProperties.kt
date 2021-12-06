package com.server.common.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("aws")
data class AwsProperties(val accountId: String, val awsKey: String, val awsSecret: String, val amplify: AmplifyProperties, val es: ESProperties, val keyspace: KeyspaceProperties, val sqs: SqsProperties) {

//    data class DynamoDbProperties(var endpoint: String)

    /**
     *
     * Example:
     *
     * wellKnownIssuer: https://cognito-idp.ap-south-1.amazonaws.com/ap-south-1_lIoW4O3di
     * wellKnownUrlEndpoint: https://cognito-idp.ap-south-1.amazonaws.com/ap-south-1_lIoW4O3di/.well-known/jwks.json
     *
     * */
    data class AmplifyProperties(val wellKnownIssuer: String, val wellKnownUrlEndpoint: String)
    data class ESProperties(val host: String, val port: Int, val protocol: String, val username: String, val password: String)
    data class KeyspaceProperties(val name: String, val username: String, val password: String)
    data class SqsProperties(val region: String, val queueName: String, val queueUrl: String)
}

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
    var allowedOriginPatterns: List<String>? = null,
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

@ConstructorBinding
@ConfigurationProperties("pdf")
data class PdfProperties(val salarySlip: SalarySlipProperties) {
    data class SalarySlipProperties(var templateName: String, var variableName: String)
}

@ConstructorBinding
@ConfigurationProperties("datastax")
data class DatastaxProperties(val astra: AstraProperties) {
    data class AstraProperties(var secureConnectBundleS3Bucket: String, var secureConnectBundleS3key: String)
}

@ConstructorBinding
@ConfigurationProperties("cloudinary")
data class CloudinaryProperties(val cloudName: String, val apiKey: String, val apiSecret: String)

@ConstructorBinding
@ConfigurationProperties("algolia")
data class AlgoliaProperties(val applicationId: String, val apiKey: String)
