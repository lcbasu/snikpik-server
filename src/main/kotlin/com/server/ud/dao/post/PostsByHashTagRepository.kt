package com.server.ud.dao.post

import com.server.ud.entities.post.PostsByHashTag
import com.server.ud.entities.post.PostsByHashTagTracker
import com.server.ud.enums.PostType
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface PostsByHashTagRepository : CassandraRepository<PostsByHashTag?, String?> {
//    fun findAllByHashTagIdAndPostTypeAndCreatedAtAndPostIdAndUserId(
//            hashTagId: String,
//            postType: PostType,
//            createdAt: Instant,
//            postId: String,
//            userId: String,
//    ): List<PostsByHashTag>

}

@Repository
interface PostsByHashTagTrackerRepository : CassandraRepository<PostsByHashTagTracker?, String?> {
    fun findAllByPostId(postId: String, pageable: Pageable): Slice<PostsByHashTagTracker>
}
