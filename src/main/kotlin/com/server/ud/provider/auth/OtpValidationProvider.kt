package com.server.ud.provider.auth

import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.UniqueIdProvider
import com.server.common.utils.DateUtils
import com.server.ud.dao.auth.OtpValidationRepository
import com.server.ud.entities.auth.OtpValidation
import com.server.ud.utils.UDCommonUtils
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

    fun getOrSaveOtpValidation(absoluteMobileNumber: String) : OtpValidation? {
        try {
            if (absoluteMobileNumber.isBlank()) {
                error("absoluteMobileNumber is blank.")
            }
            val existing = getOtpValidation(absoluteMobileNumber)
            if (existing != null && existing.expireAt > DateUtils.getEpochNow()) {
                // Not expired yet.So send the old one.
                return existing
            }
            // Not sent yet or already expired, so create a new one
            return otpValidationRepository.save(OtpValidation(
                absoluteMobile = absoluteMobileNumber,
                createdAt = DateUtils.getEpochNow(),
                expireAt = DateUtils.getEpoch(DateUtils.getInstantNow().plusSeconds(10 * 60)), // After 10 minutes
                otp = UDCommonUtils.getOtp(6),
                loginSequenceId = uniqueIdProvider.getUniqueId(ReadableIdPrefix.OTP.name),
            ))
        } catch (e: Exception) {
            logger.error("Saving OtpValidation for absoluteMobileNumber: $absoluteMobileNumber failed.")
            e.printStackTrace()
            return null
        }
    }

}
