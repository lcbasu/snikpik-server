package com.server.ud.dao.post

import com.server.ud.entities.post.NearbyPostsByZipcode
import com.server.ud.entities.post.NearbyPostsByZipcodeTracker
import com.server.ud.enums.PostType
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface NearbyPostsByZipcodeRepository : CassandraRepository<NearbyPostsByZipcode?, String?> {
    fun findAllByZipcodeAndPostType(zipcode: String, postType: PostType, pageable: Pageable): Slice<NearbyPostsByZipcode>


    @Query("SELECT * FROM nearby_posts_by_zipcode where post_type = ?1 and post_id = ?2 and zipcode = ?0 allow filtering")
    fun getAll(zipcode: String, postType: PostType, postId: String): List<NearbyPostsByZipcode>

//    fun deleteByZipcodeAndPostTypeAndCreatedAtAndPostIdAndUserId(
//        zipcode: String,
//        postType: PostType,
//        createdAt: Instant,
//        postId: String,
//        userId: String
//    )
}

@Repository
interface NearbyPostsByZipcodeTrackerRepository : CassandraRepository<NearbyPostsByZipcodeTracker?, String?> {
    fun findAllByPostId(postId: String, pageable: Pageable): Slice<NearbyPostsByZipcodeTracker>
}
