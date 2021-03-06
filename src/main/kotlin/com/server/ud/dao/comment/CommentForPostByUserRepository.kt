package com.server.ud.dao.comment

import com.server.ud.entities.comment.CommentForPostByUser
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentForPostByUserRepository : CassandraRepository<CommentForPostByUser?, String?> {
//    @Query("select * from comment_for_post_by_user where post_id = ?0 and user_id = ?1")
    fun findAllByPostIdAndUserId(postId: String, userId: String): List<CommentForPostByUser>

//    @AllowFiltering
//    fun findAllByPostId(postId: String): List<CommentForPostByUser>

    fun deleteAllByPostIdAndUserId(postId: String, userId: String)
}
