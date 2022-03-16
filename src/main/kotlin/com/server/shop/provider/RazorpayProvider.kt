package com.server.shop.provider

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.TreeTraversingParser
import com.razorpay.RazorpayClient
import com.razorpay.RazorpayException
import com.server.common.enums.ReadableIdPrefix
import com.server.common.properties.PaymentProperties
import com.server.common.provider.SecurityProvider
import com.server.common.provider.UniqueIdProvider
import com.server.common.utils.CommonUtils
import com.server.shop.dao.ProductOrderV3Repository
import com.server.shop.dto.*
import com.server.shop.entities.AddressV3
import com.server.shop.entities.ProductOrderV3
import com.server.shop.entities.UserV3
import com.server.shop.enums.ProductOrderStatusV3
import com.server.shop.enums.ProductOrderType
import com.server.shop.model.OrderStateTransitionOutputV3
import com.server.shop.model.UpdatedCartDataV3
import org.apache.commons.codec.binary.Hex
import org.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.IOException
import org.springframework.transaction.annotation.Transactional
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.xor

@Component
class RazorpayProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var paymentProperties: PaymentProperties

    @Autowired
    private lateinit var productOrderV3Repository: ProductOrderV3Repository

    @Autowired
    private lateinit var securityProvider: SecurityProvider


    @Autowired
    private lateinit var userV3Provider: UserV3Provider


    @Autowired
    private lateinit var productVariantV3Provider: ProductVariantV3Provider

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Autowired
    private lateinit var cartItemsV3Provider: CartItemsV3Provider

    @Autowired
    private lateinit var productOrderStateChangeV3Provider: ProductOrderStateChangeV3Provider

    @Autowired
    private lateinit var saveForLaterProvider: SaveForLaterProvider

    @Autowired
    private lateinit var addressV3Provider: AddressV3Provider

    @Autowired
    private lateinit var razorpayClient: RazorpayClient

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    fun getRazorPayOrderResponse(productOrder: ProductOrderV3): RazorpayOrderResponse? {
        var razorpayOrderResponse: RazorpayOrderResponse? = null
        try {
            val orderRequest = JSONObject()
            orderRequest.put("amount", productOrder.priceOfCartItemsWithoutTaxInPaisa)
            orderRequest.put("currency", "INR")
            orderRequest.put("receipt", productOrder.id)

            val order = razorpayClient.Orders.create(orderRequest)

            val jsonObject: JSONObject = order.toJson()
            val jsonNode: JsonNode = CommonUtils.convertJsonFormat(jsonObject)
            razorpayOrderResponse = objectMapper.readValue(TreeTraversingParser(jsonNode), RazorpayOrderResponse::class.java)
        } catch (e: IOException) {
            e.printStackTrace()
            logger.error("Error while parsing razorpay order response: ${e.message}")
        }
        return razorpayOrderResponse
    }

    @Throws(RazorpayException::class)
    fun verifyOrderPaymentSignature(expectedSignature: String, paymentId: String, orderId: String): Boolean {
        val payload = "$orderId|$paymentId"
        return verifySignature(payload, expectedSignature)
    }

    @Throws(RazorpayException::class)
    private fun verifySignature(payload: String, expectedSignature: String): Boolean {
        val actualSignature = getHash(payload, paymentProperties.razorpay.secret)
        return isEqual(actualSignature.toByteArray(), expectedSignature.toByteArray())
    }

    @Throws(RazorpayException::class)
    private fun getHash(payload: String, secret: String): String {
        val sha256_HMAC: Mac
        return try {
            sha256_HMAC = Mac.getInstance("HmacSHA256")
            val secret_key = SecretKeySpec(secret.toByteArray(charset("UTF-8")), "HmacSHA256")
            sha256_HMAC.init(secret_key)
            val hash = sha256_HMAC.doFinal(payload.toByteArray())
            String(Hex.encodeHex(hash))
        } catch (e: java.lang.Exception) {
            throw RazorpayException(e.message)
        }
    }

    /**
     * We are not using String.equals() method because of security issue mentioned in
     * [StackOverflow](http://security.stackexchange.com/a/83670)
     *
     * @param a
     * @param b
     * @return boolean
     */
    private fun isEqual(a: ByteArray, b: ByteArray): Boolean {
        if (a.size != b.size) {
            return false
        }
        var result = 0
        for (i in a.indices) {
            result = result or (a[i] xor b[i]).toInt()
        }
        return result == 0
    }


}
