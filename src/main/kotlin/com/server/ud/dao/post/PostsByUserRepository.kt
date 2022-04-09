package com.server.ud.dao.post

import com.server.common.utils.DateUtils
import com.server.ud.entities.post.PostsByUser
import com.server.ud.entities.post.PostsByUserTracker
import com.server.ud.enums.PostType
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface PostsByUserRepository : CassandraRepository<PostsByUser?, String?> {
    fun findAllByUserIdAndPostType(userId: String, postType: PostType, pageable: Pageable): Slice<PostsByUser>

//    fun findAllByUserIdAndPostTypeAndCreatedAtAndPostId(userId: String, postType: PostType, createdAt: Instant, postId: String): List<PostsByUser>
}

@Repository
interface PostsByUserTrackerRepository : CassandraRepository<PostsByUserTracker?, String?> {
    fun findAllByPostId(postId: String, pageable: Pageable): Slice<PostsByUserTracker>
}
