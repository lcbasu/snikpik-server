package com.server.ud.dao.post

import com.server.ud.entities.post.NearbyVideoPostsByZipcode
import com.server.ud.entities.post.NearbyVideoPostsByZipcodeTracker
import com.server.ud.enums.PostType
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface NearbyVideoPostsByZipcodeRepository : CassandraRepository<NearbyVideoPostsByZipcode?, String?> {
    fun findAllByZipcodeAndPostType(zipcode: String, postType: PostType, pageable: Pageable): Slice<NearbyVideoPostsByZipcode>
//    fun deleteByZipcodeAndPostTypeAndCreatedAtAndPostIdAndUserId(
//        zipcode: String,
//        postType: PostType,
//        createdAt: Instant,
//        postId: String,
//        userId: String
//    )
    @Query("SELECT * FROM nearby_video_posts_by_zipcode where post_type = ?1 and post_id = ?2 and zipcode = ?0 allow filtering")
    fun getAll(zipcode: String, postType: PostType, postId: String): List<NearbyVideoPostsByZipcode>
}

@Repository
interface NearbyVideoPostsByZipcodeTrackerRepository : CassandraRepository<NearbyVideoPostsByZipcodeTracker?, String?> {
    fun findAllByPostId(postId: String, pageable: Pageable): Slice<NearbyVideoPostsByZipcodeTracker>
}
