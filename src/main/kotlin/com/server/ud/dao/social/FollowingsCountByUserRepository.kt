package com.server.ud.dao.social

import com.server.ud.entities.social.FollowingsCountByUser
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface FollowingsCountByUserRepository : CassandraRepository<FollowingsCountByUser?, String?> {

//    @Query("select * from followings_count_by_user where user_id = ?0")
    fun findAllByUserId(userId: String?): List<FollowingsCountByUser>

    @Query("UPDATE followings_count_by_user SET followings_count = followings_count + 1 WHERE user_id = ?0")
    fun incrementFollowings(userId: String)

    @Query("UPDATE followings_count_by_user SET followings_count = followings_count - 1 WHERE user_id = ?0")
    fun decrementFollowings(userId: String)
}
