package com.server.ud.dao.social

import com.server.ud.entities.social.FollowersCountByUser
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface FollowersCountByUserRepository : CassandraRepository<FollowersCountByUser?, String?> {

//    @Query("select * from followers_count_by_user where user_id = ?0")
    fun findAllByUserId(userId: String?): List<FollowersCountByUser>

    @Query("UPDATE followers_count_by_user SET followers_count = followers_count + 1 WHERE user_id = ?0")
    fun incrementFollowers(userId: String)

    @Query("UPDATE followers_count_by_user SET followers_count = followers_count - 1 WHERE user_id = ?0")
    fun decrementFollowers(userId: String)
}
