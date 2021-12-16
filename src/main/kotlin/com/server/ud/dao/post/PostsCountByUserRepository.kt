package com.server.ud.dao.post

import com.server.ud.entities.user.PostsCountByUser
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PostsCountByUserRepository : CassandraRepository<PostsCountByUser?, String?> {

//    @Query("select * from posts_count_by_user where user_id = ?0")
    fun findAllByUserId(userId: String?): List<PostsCountByUser>

    @Query("UPDATE posts_count_by_user SET posts_count = posts_count + 1 WHERE user_id = ?0")
    fun incrementPostCount(userId: String)

    @Query("UPDATE posts_count_by_user SET posts_count = posts_count - 1 WHERE user_id = ?0")
    fun decrementPostCount(userId: String)
}
