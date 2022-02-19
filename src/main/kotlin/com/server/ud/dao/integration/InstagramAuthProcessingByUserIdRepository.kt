package com.server.ud.dao.integration

import com.server.ud.entities.integration.common.InstagramAuthProcessingByUserId
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface InstagramAuthProcessingByUserIdRepository : CassandraRepository<InstagramAuthProcessingByUserId?, String?> {

}
