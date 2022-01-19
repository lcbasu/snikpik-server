package com.server.common.service

import com.server.common.model.UserDetailsForToken
import com.server.common.provider.AuthProvider
import com.server.common.provider.SecurityProvider
import com.server.common.provider.communication.CommunicationProvider
import com.server.common.utils.DateUtils
import com.server.dk.dto.*
import com.server.ud.provider.auth.OtpValidationProvider
import com.server.ud.provider.user.UserV2ByMobileNumberProvider
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthServiceImpl : AuthService() {

    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var userV2ByMobileNumberProvider: UserV2ByMobileNumberProvider

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    @Autowired
    private lateinit var otpValidationProvider: OtpValidationProvider

    @Autowired
    private lateinit var communicationProvider: CommunicationProvider

//    @Autowired
//    private lateinit var jwtUtil: JwtUtil

    override fun getAuthContext(): RequestContextResponse {
        val requestContext = authProvider.validateRequest()
        return requestContext.toRequestContextResponse()
    }

    override fun login(request: LoginRequest): LoginResponse {

        // Verify if the OTP is same as what we saved earlier

        val otpValidation = otpValidationProvider.getOtpValidation(request.absoluteMobileNumber)
            ?: return LoginResponse(
                authenticated = false,
                errorMessage = "Invalid login attempt. Please enter your phone number again and try."
            )

        if (DateUtils.getEpochNow() > otpValidation.expireAt) {
            return LoginResponse(
                authenticated = false,
                errorMessage = "OTP Expired. Please enter your phone number again and try."
            )
        }

        if (request.loginSequenceId != otpValidation.loginSequenceId) {
            return LoginResponse(
                authenticated = false,
                errorMessage = "Invalid login attempt. Please enter your phone number again and try."
            )
        }

        if (request.otp != otpValidation.otp) {
            return LoginResponse(
                authenticated = false,
                errorMessage = "Invalid OTP. Please enter your OTP again and try."
            )
        }

        val userV2ByMobileNumber = userV2ByMobileNumberProvider.getOrSaveUserV2ByMobileNumber(request.absoluteMobileNumber) ?: error("Error while generating id for mobile number: ${request.absoluteMobileNumber}")

        val token = generateToken(UserDetailsForToken(
            uid = userV2ByMobileNumber.userId,
            absoluteMobile = userV2ByMobileNumber.absoluteMobile,
        ))

        return LoginResponse(
            authenticated = true,
            token = token,
        )
    }

    override fun sendOTP(request: SendOTPRequest): OTPSentResponse {
        // Step 1
        // Verify if the phone number is valid

        val otpValidation = otpValidationProvider.getOrSaveOtpValidation(request.absoluteMobileNumber)
            ?: return OTPSentResponse(
                countryCode = request.countryCode,
                absoluteMobileNumber = request.absoluteMobileNumber,
                sent = false,
            )

        // Step 2
        // Send SMS

        communicationProvider.sendSMS(
            phoneNumber = otpValidation.absoluteMobile,
            messageStr = "Your OTP is: ${otpValidation.otp}"
        )

        // Step 3
        // Save the OTP in the cache/database to verify later with timeout of 10 minutes

        // Step 4
        // Send Login sequence Id along with response to be used for validation

        return OTPSentResponse(
            countryCode = request.countryCode,
            absoluteMobileNumber = otpValidation.absoluteMobile,
            sent = true,
            loginSequenceId = otpValidation.loginSequenceId,
        )
    }

    override fun refreshToken(): LoginResponse {
        TODO("Not yet implemented")
    }

    fun generateToken(userDetails: UserDetailsForToken): String? {
        val claims: MutableMap<String, Any?> = HashMap()
        claims["uid"] = userDetails.getUid()
        claims["absoluteMobile"] = userDetails.getAbsoluteMobileNumber()
        return createToken(claims.toMap(), userDetails.getAbsoluteMobileNumber())
    }

    fun createToken(claims: Map<String, Any?>, absoluteMobileNumber: String?): String? {
        return Jwts.builder().setClaims(claims).setSubject(absoluteMobileNumber).setIssuedAt(
            Date(
                System.currentTimeMillis()
            )
        )
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 500))
            .signWith(SignatureAlgorithm.HS256, "SECRET_KEY").compact()
    }


}
