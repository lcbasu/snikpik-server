package com.server.common.provider.auth

import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.UniqueIdProvider
import com.server.common.utils.DateUtils
import com.server.common.dao.auth.OtpValidationRepository
import com.server.common.entities.auth.OtpValidation
import com.server.common.utils.CommonUtils
import com.server.common.utils.CommonUtils.fixedLoginOTPMap
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class OtpValidationProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var otpValidationRepository: OtpValidationRepository

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    fun getOtpValidation(absoluteMobileNumber: String): OtpValidation? =
        try {
            val users = otpValidationRepository.findAllByAbsoluteMobile(absoluteMobileNumber)
            if (users.size > 1) {
                error("More than one OtpValidation has same mobileNumber: $absoluteMobileNumber")
            }
            users.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting OtpValidation for $absoluteMobileNumber failed.")
            e.printStackTrace()
            null
        }

    fun getOrSaveOtpValidation(absoluteMobileNumber: String) : OTPValidationResult? {
        try {
            if (absoluteMobileNumber.isBlank()) {
                error("absoluteMobileNumber is blank.")
            }
            val existing = getOtpValidation(absoluteMobileNumber)
            if (existing != null && existing.expireAt > DateUtils.getEpochNow()) {
                // Not expired yet.So send the old one.
                return OTPValidationResult (
                    otpValidation = existing,
                    resendOtpIsEnable = true
                )
            }
            // Not sent yet or already expired, so create a new one
            val otp = fixedLoginOTPMap.getOrDefault(absoluteMobileNumber, CommonUtils.getOtp(6))
            val otpValidation = otpValidationRepository.save(OtpValidation(
                absoluteMobile = absoluteMobileNumber,
                createdAt = DateUtils.getEpochNow(),
                expireAt = DateUtils.getEpoch(DateUtils.getInstantNow().plusSeconds(10 * 60)), // After 10 minutes
                otp = otp,
                loginSequenceId = uniqueIdProvider.getUniqueIdAfterSaving(ReadableIdPrefix.OTP.name),
            ))
            return OTPValidationResult (
                otpValidation = otpValidation,
                resendOtpIsEnable = false
            )
        } catch (e: Exception) {
            logger.error("Saving OtpValidation for absoluteMobileNumber: $absoluteMobileNumber failed.")
            e.printStackTrace()
            return null
        }
    }

}

data class OTPValidationResult (
    val otpValidation: OtpValidation,
    val resendOtpIsEnable: Boolean,
)
