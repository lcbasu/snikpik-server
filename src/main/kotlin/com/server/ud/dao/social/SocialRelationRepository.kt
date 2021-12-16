package com.server.ud.dao.social

import com.server.ud.entities.social.SocialRelation
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface SocialRelationRepository : CassandraRepository<SocialRelation?, String?> {
    fun findAllByFromUserIdAndToUserId(fromUserId: String, toUserId: String): List<SocialRelation>
//    @Query("select * from social_relation where from_user_id = ?0 and to_user_id = ?1")
//    fun getAllByUserAndOtherUser(fromUserId: String, toUserId: String): List<SocialRelation>
}
