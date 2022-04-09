package com.server.ud.dao.post

import com.server.ud.entities.post.PostsByZipcode
import com.server.ud.entities.post.PostsByZipcodeTracker
import com.server.ud.enums.PostType
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface PostsByZipcodeRepository : CassandraRepository<PostsByZipcode?, String?> {
//
//    fun deleteAllByUserIdAndPostTypeAndPostId(userId: String, postType: PostType, postId: String)
//
//    fun findAllByUserIdAndPostTypeAndPostId(userId: String, postType: PostType, postId: String): List<PostsByZipcode>
//
////    @Query("SELECT * FROM bookmarked_posts_by_user where post_id = ?0 allow filtering")
////    fun findAllByPostId_V2(postId: String): List<BookmarkedPostsByUser>
}

@Repository
interface PostsByZipcodeTrackerRepository : CassandraRepository<PostsByZipcodeTracker?, String?> {
    fun findAllByPostId(postId: String, pageable: Pageable): Slice<PostsByZipcodeTracker>
}
