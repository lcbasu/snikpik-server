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

    fun findAllByZipcodeAndPostTypeAndForDate(zipcode: String, postType: PostType, forDate: Instant, pageable: Pageable): Slice<NearbyPostsByZipcode>

    @AllowFiltering
    fun findAllByPostId(postId: String): List<NearbyPostsByZipcode>

}
