package com.server.ud.dao.integration

import com.server.ud.entities.integration.common.IntegrationAccountInfoByPlatformAccountId
import com.server.ud.entities.integration.common.IntegrationAccountInfoByUserId
import com.server.ud.enums.IntegrationPlatform
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface IntegrationAccountInfoByUserIdRepository : CassandraRepository<IntegrationAccountInfoByUserId?, String?> {
    fun findAllByUserIdAndPlatformAndAccountId(userId: String, platform: IntegrationPlatform, accountId: String): List<IntegrationAccountInfoByUserId>
    fun findAllByUserId(userId: String): List<IntegrationAccountInfoByUserId>
}


@Repository
interface IntegrationAccountInfoByPlatformAccountIdRepository : CassandraRepository<IntegrationAccountInfoByPlatformAccountId?, String?> {
    fun findAllByAccountId(accountId: String): List<IntegrationAccountInfoByPlatformAccountId>
}
