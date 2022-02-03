package com.server.ud.dao.post

import com.server.ud.entities.post.NearbyPostsByZipcode
import com.server.ud.enums.PostType
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface NearbyPostsByZipcodeRepository : CassandraRepository<NearbyPostsByZipcode?, String?> {

    fun findAllByZipcodeAndPostType(zipcode: String, postType: PostType, pageable: Pageable): Slice<NearbyPostsByZipcode>
    fun findAllByZipcodeAndPostTypeAndCreatedAtAndPostId(zipcode: String, postType: PostType, createdAt: Instant, postId: String): List<NearbyPostsByZipcode>

}
