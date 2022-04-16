package com.server.ud.dao.post

import com.server.ud.entities.post.PostsByPostType
import com.server.ud.entities.post.PostsByPostTypeTracker
import com.server.ud.enums.PostType
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface PostsByPostTypeRepository : CassandraRepository<PostsByPostType?, String?> {
    fun findAllByPostType(postType: PostType, pageable: Pageable): Slice<PostsByPostType>
}

@Repository
interface PostsByPostTypeTrackerRepository : CassandraRepository<PostsByPostTypeTracker?, String?> {
    fun findAllByPostId(postId: String, pageable: Pageable): Slice<PostsByPostTypeTracker>
}
