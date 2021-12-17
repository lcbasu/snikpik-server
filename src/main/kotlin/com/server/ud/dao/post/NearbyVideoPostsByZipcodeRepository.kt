package com.server.ud.dao.post

import com.server.ud.entities.post.NearbyVideoPostsByZipcode
import com.server.ud.enums.PostType
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface NearbyVideoPostsByZipcodeRepository : CassandraRepository<NearbyVideoPostsByZipcode?, String?> {

    fun findAllByZipcodeAndPostType(zipcode: String, postType: PostType, pageable: Pageable): Slice<NearbyVideoPostsByZipcode>

    @AllowFiltering
    fun findAllByPostId(postId: String): List<NearbyVideoPostsByZipcode>

}
