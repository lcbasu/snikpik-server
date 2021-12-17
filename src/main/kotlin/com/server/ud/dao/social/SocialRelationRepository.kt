package com.server.ud.dao.social

import com.server.ud.entities.social.SocialRelation
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface SocialRelationRepository : CassandraRepository<SocialRelation?, String?> {
    fun findAllByFromUserIdAndToUserId(fromUserId: String, toUserId: String): List<SocialRelation>

    @AllowFiltering
    @Query("SELECT * FROM social_relation")
    fun getAll(): List<SocialRelation>
}
