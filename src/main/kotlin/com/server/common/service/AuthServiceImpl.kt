package com.server.common.service

import com.server.common.provider.AuthProvider
import com.server.common.provider.SecurityProvider
import com.server.common.provider.communication.CommunicationProvider
import com.server.common.utils.DateUtils
import com.server.dk.dto.*
import com.server.ud.provider.auth.OtpValidationProvider
import com.server.ud.provider.auth.RefreshTokenProvider
import com.server.ud.provider.automation.AutomationProvider
import com.server.ud.provider.user.UserV2ByMobileNumberProvider
import com.server.ud.utils.UDCommonUtils.fixedLoginOTPMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AuthServiceImpl : AuthService() {

    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var userV2ByMobileNumberProvider: UserV2ByMobileNumberProvider

    @Autowired
    private lateinit var refreshTokenProvider: RefreshTokenProvider

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    @Autowired
    private lateinit var otpValidationProvider: OtpValidationProvider

    @Autowired
    private lateinit var communicationProvider: CommunicationProvider

//    @Autowired
//    private lateinit var jwtProvider: JwtProvider
//
//    @Autowired
//    private lateinit var uniqueIdProvider: UniqueIdProvider
//
//    @Autowired
//    private lateinit var validTokenProvider: ValidTokenProvider

//    @Autowired
//    private lateinit var jwtUtil: JwtUtil

    @Autowired
    private lateinit var automationProvider: AutomationProvider

    override fun getAuthContext(): RequestContextResponse {
        val requestContext = authProvider.validateRequest()
        return requestContext.toRequestContextResponse()
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

        automationProvider.sendSlackMessageForOTP(otpValidation)

        // Step 2
        // Send OTP SMS/Whatsapp/Any Other channel

        val isLoginOtpFixed = fixedLoginOTPMap.containsKey(request.absoluteMobileNumber)
        if (!isLoginOtpFixed) {
            val sent = communicationProvider.sendOTP(
                phoneNumber = otpValidation.absoluteMobile,
                otp = otpValidation.otp,
            )
            if (!sent) {
                return OTPSentResponse(
                    countryCode = request.countryCode,
                    absoluteMobileNumber = request.absoluteMobileNumber,
                    sent = false,
                )
            }
        }

        // UNCOMMENT IN CASE MSG91 Screws up
//        // Send SMS
//        communicationProvider.sendSMS(
//            phoneNumber = otpValidation.absoluteMobile,
//            messageStr = "<#> Unbox login OTP is: ${otpValidation.otp}\nFUQvhHBBP7x"
//        )

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

//    override fun login(request: LoginRequest): LoginResponse {
//
//        // Verify if the OTP is same as what we saved earlier
//
//        val otpValidation = otpValidationProvider.getOtpValidation(request.absoluteMobileNumber)
//            ?: return LoginResponse(
//                authenticated = false,
//                errorMessage = "Invalid login attempt. Please enter your phone number again and try."
//            )
//
//        if (DateUtils.getEpochNow() > otpValidation.expireAt) {
//            return LoginResponse(
//                authenticated = false,
//                errorMessage = "OTP Expired. Please enter your phone number again and try."
//            )
//        }
//
//        if (request.loginSequenceId != otpValidation.loginSequenceId) {
//            return LoginResponse(
//                authenticated = false,
//                errorMessage = "Invalid login attempt. Please enter your phone number again and try."
//            )
//        }
//
//        if (request.otp != otpValidation.otp) {
//            return LoginResponse(
//                authenticated = false,
//                errorMessage = "Invalid OTP. Please enter your OTP again and try."
//            )
//        }
//
//        val userV2ByMobileNumber = userV2ByMobileNumberProvider.getOrSaveUserV2ByMobileNumber(request.absoluteMobileNumber) ?: error("Error while generating id for mobile number: ${request.absoluteMobileNumber}")
//
//        val token = jwtProvider.generateToken(UserDetailsForToken(
//            uid = userV2ByMobileNumber.userId,
//            absoluteMobile = userV2ByMobileNumber.absoluteMobile,
//        ))
//
//        refreshTokenProvider.saveRefreshToken(
//            loginSequenceId = request.loginSequenceId,
//            userId = userV2ByMobileNumber.userId,
//            absoluteMobile = userV2ByMobileNumber.absoluteMobile,
//            token = token,
//            usedToRefresh = false,
//        )
//
//        validTokenProvider.saveValidToken(
//            token = token,
//            valid = true,
//            validByLoginSequenceId = request.loginSequenceId,
//        )
//
//        return LoginResponse(
//            authenticated = true,
//            token = token,
//            userId = userV2ByMobileNumber.userId,
//            loginSequenceId = request.loginSequenceId,
//            absoluteMobileNumber = request.absoluteMobileNumber,
//        )
//    }

    override fun loginV2(request: LoginRequest): LoginResponseV2 {

        // Verify if the OTP is same as what we saved earlier

        val otpValidation = otpValidationProvider.getOtpValidation(request.absoluteMobileNumber)
            ?: return LoginResponseV2(
                authenticated = false,
                errorMessage = "Invalid login attempt. Please enter your phone number again and try."
            )

        if (DateUtils.getEpochNow() > otpValidation.expireAt) {
            return LoginResponseV2(
                authenticated = false,
                errorMessage = "OTP Expired. Please enter your phone number again and try."
            )
        }

        if (request.loginSequenceId != otpValidation.loginSequenceId) {
            return LoginResponseV2(
                authenticated = false,
                errorMessage = "Invalid login attempt. Please enter your phone number again and try."
            )
        }

        if (request.otp != otpValidation.otp) {
            return LoginResponseV2(
                authenticated = false,
                errorMessage = "Invalid OTP. Please enter your OTP again and try."
            )
        }

        val userV2ByMobileNumber = userV2ByMobileNumberProvider.getOrSaveUserV2ByMobileNumber(request.absoluteMobileNumber) ?: error("Error while generating id for mobile number: ${request.absoluteMobileNumber}")

        val firebaseCustomToken = authProvider.getAuthTokenForFirebaseUserId(userV2ByMobileNumber.userId) ?: error("Error while generating auth token for user id: ${userV2ByMobileNumber.userId}")

        return LoginResponseV2(
            authenticated = true,
            firebaseCustomToken = firebaseCustomToken,
            userId = userV2ByMobileNumber.userId,
            loginSequenceId = request.loginSequenceId,
            absoluteMobileNumber = request.absoluteMobileNumber,
        )
    }

//    override fun refreshToken(request: RefreshTokenRequest): TokenRefreshResponse {
//        val oldLoginSequenceId = request.loginSequenceId
//        val oldToken = request.token
//        val refreshTokenObject = refreshTokenProvider.getRefreshToken(oldLoginSequenceId)
//        if (refreshTokenObject == null || refreshTokenObject.token != UDCommonUtils.getSha256Hash(oldToken) || refreshTokenObject.loginSequenceId != oldLoginSequenceId || refreshTokenObject.usedToRefresh) {
//            return TokenRefreshResponse(
//                oldLoginSequenceId = oldLoginSequenceId,
//                oldToken = oldToken,
//                refreshed = false,
//                errorMessage = "Invalid refresh attempt. Please try logging in again."
//            )
//        }
//
//        val userDetailsFromUDTokens = jwtProvider.validateTokenForClaims(oldToken) ?: return TokenRefreshResponse(
//            oldLoginSequenceId = oldLoginSequenceId,
//            oldToken = oldToken,
//            refreshed = false,
//            errorMessage = "Invalid refresh attempt. Please try logging in again."
//        )
//
//        val newToken = jwtProvider.generateToken(
//            UserDetailsForToken(
//                uid = userDetailsFromUDTokens.uid,
//                absoluteMobile = userDetailsFromUDTokens.absoluteMobile,
//            )
//        )
//
//        val newLoginSequenceId = uniqueIdProvider.getUniqueId(ReadableIdPrefix.RFT.name)
//
//        // Save this for future use while refreshing the token
//
//        // Invalidate old one
//        refreshTokenProvider.saveRefreshToken(
//            loginSequenceId = oldLoginSequenceId,
//            userId = userDetailsFromUDTokens.uid,
//            absoluteMobile = userDetailsFromUDTokens.absoluteMobile,
//            token = oldToken,
//            usedToRefresh = true,
//        )
//
//        // Save new one
//        refreshTokenProvider.saveRefreshToken(
//            loginSequenceId = newLoginSequenceId,
//            userId = userDetailsFromUDTokens.uid,
//            absoluteMobile = userDetailsFromUDTokens.absoluteMobile,
//            token = newToken,
//            usedToRefresh = false,
//        )
//
//        // Invalidate old one
//        validTokenProvider.saveValidToken(
//            token = oldToken,
//            valid = false,
//            validByLoginSequenceId = oldLoginSequenceId,
//            invalidByLoginSequenceId = newLoginSequenceId,
//        )
//
//        // Save New one
//        validTokenProvider.saveValidToken(
//            token = newToken,
//            valid = true,
//            validByLoginSequenceId = newLoginSequenceId,
//        )
//
//        return TokenRefreshResponse(
//            oldLoginSequenceId = oldLoginSequenceId,
//            refreshed = true,
//            oldToken = oldToken,
//            newToken = newToken,
//            newLoginSequenceId = newLoginSequenceId,
//            userId = userDetailsFromUDTokens.uid,
//            absoluteMobileNumber = refreshTokenObject.absoluteMobile,
//        )
//    }
//
//    override fun logout(request: LogoutRequest): LogoutResponse {
//        securityProvider.validateRequest()
//        val token = securityProvider.getFirebaseAuthUser()?.getToken() ?: return LogoutResponse(
//            loggedOut = false,
//            errorMessage = "Invalid logout attempt. Please try again."
//        )
//
//        val savedValidToken = validTokenProvider.getValidToken(token) ?: return LogoutResponse(
//            loggedOut = false,
//            errorMessage = "Invalid logout attempt. Please try again."
//        )
//
//        if (!savedValidToken.valid || savedValidToken.validByLoginSequenceId != request.loginSequenceId) {
//            return LogoutResponse(
//                loggedOut = false,
//                errorMessage = "Invalid logout attempt. Please try again."
//            )
//        }
//
//        // Invalidate the token
//        val updatedValidToken = validTokenProvider.saveValidToken(
//            token = token,
//            valid = false,
//            validByLoginSequenceId = request.loginSequenceId,
//            invalidByLoginSequenceId = request.loginSequenceId
//        ) ?: return LogoutResponse(
//            loggedOut = false,
//            errorMessage = "Failed to logout. Please try again."
//        )
//
//        return LogoutResponse(
//            loggedOut = !updatedValidToken.valid,
//        )
//    }
}
