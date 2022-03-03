package com.server.common.provider.communication

import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.Unirest
import com.messagebird.MessageBirdClient
import com.messagebird.objects.Message
import com.server.common.properties.Msg91Properties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CommunicationProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var messageBirdClient: MessageBirdClient

    @Autowired
    private lateinit var msg91Properties: Msg91Properties

    fun sendSMS(phoneNumber: String, messageStr: String) {
        logger.info("Sending SMS to $phoneNumber with message $messageStr")

        val message = Message(
            "TestMessage",
            messageStr,
            phoneNumber
        )
        val response = messageBirdClient.sendMessage(message)

        logger.info("SMS sent successfully with response ${response.toString()}")
    }

    fun sendOTP(phoneNumber: String, otp: String, resendOtpIsEnable: Boolean = false): Boolean {
        if (resendOtpIsEnable) {
            return resendOTPUsingMessageBird(phoneNumber, otp)
        }
        logger.info("Sending OTP SMS to $phoneNumber with otp $otp")
        return try {
            val response: HttpResponse<String> = Unirest.post("https://api.msg91.com/api/v5/flow/")
                .header("authkey", msg91Properties.apiKey)
                .header("content-type", "application/JSON")
                .body("{\n  \"flow_id\": \"${msg91Properties.flowId}\",\n  \"sender\": \"${msg91Properties.senderId}\",\n  \"mobiles\": \"${phoneNumber.replace("+", "")}\",\n  \"otp\": \"${otp}\"\n}")
                .asString()
            logger.info("OTP SMS response: ${response.body}")
            response.status == 200
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("Error while sending OTP SMS to $phoneNumber with otp $otp. Error: ${e.message}")
            false
        }
    }

    fun resendOTPUsingMessageBird(phoneNumber: String, otp: String): Boolean {
        logger.info("Re-try sending OTP SMS to $phoneNumber with otp $otp using message bird.")
        return try {
            sendSMS(
                phoneNumber = phoneNumber,
                messageStr = "<#> Unbox login OTP is: $otp\nFUQvhHBBP7x"
            )
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("Error while retrying sending OTP SMS to $phoneNumber with otp $otp. Error: ${e.message}")
            false
        }
    }

}
