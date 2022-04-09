package com.server.ud.dao.post

import com.server.common.utils.DateUtils
import com.server.ud.entities.post.PostsByFollowing
import com.server.ud.entities.post.PostsByFollowingTracker
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
interface PostsByFollowingRepository : CassandraRepository<PostsByFollowing?, String?> {

//    @Query("SELECT * FROM posts_by_following where post_id = ?0 allow filtering")
//    fun findAllByPostId_V2(postId: String): List<PostsByFollowing>


//    fun findAllByUserIdAndFollowingUserIdAndPostTypeAndCreatedAtAndPostId(userId: String, followingUserId: String, postType: PostType, createdAt: Instant, postId: String): List<PostsByFollowing>

}

@Repository
interface PostsByFollowingTrackerRepository : CassandraRepository<PostsByFollowingTracker?, String?> {
    fun findAllByPostId(postId: String, pageable: Pageable): Slice<PostsByFollowingTracker>
}
