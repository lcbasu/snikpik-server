package com.server.ud.dao.comment

import com.server.ud.entities.comment.CommentsCountByPost
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface CommentsCountByPostRepository : CassandraRepository<CommentsCountByPost?, String?> {

    @Query("select * from comments_count_by_post where post_id = ?0")
    fun findAllByPostId(postId: String?): List<CommentsCountByPost>

    @Query("UPDATE comments_count_by_post SET comments_count = comments_count + 1 WHERE post_id = ?0")
    fun incrementCommentCount(postId: String)
}
