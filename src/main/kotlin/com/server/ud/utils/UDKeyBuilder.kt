package com.server.ud.utils

import com.server.common.utils.CommonUtils
import com.server.ud.entities.integration.common.IntegrationAccountInfoByUserId

object UDKeyBuilder {
    fun getJobKeyForIntegrationAccountInfoByUserId(integrationAccountInfoByUserId: IntegrationAccountInfoByUserId): String =
        "${integrationAccountInfoByUserId.userId}${CommonUtils.STRING_SEPARATOR}${integrationAccountInfoByUserId.platform}${CommonUtils.STRING_SEPARATOR}${integrationAccountInfoByUserId.accountId}"

    fun parseJobKeyForIntegrationAccountInfoByUserId(key: String): ParsedKeyForIntegrationAccountInfoByUserId {
        val values = key.split(CommonUtils.STRING_SEPARATOR)
        return ParsedKeyForIntegrationAccountInfoByUserId(
            userId = values[0],
            platform = values[1],
            accountId = values[2]
        )
    }
}

data class ParsedKeyForIntegrationAccountInfoByUserId(
    val userId: String,
    val platform: String,
    val accountId: String
)
