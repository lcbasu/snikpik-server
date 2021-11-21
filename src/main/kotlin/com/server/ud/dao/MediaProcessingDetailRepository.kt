package com.server.ud.dao

import com.server.ud.entities.MediaProcessingDetail
import com.server.ud.entities.user.UserV2
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MediaProcessingDetailRepository : CassandraRepository<MediaProcessingDetail?, String?> {
    @Query("select * from media_processing_detail where file_unique_id = ?0")
    fun findAllByFileUniqueId(fileUniqueId: String): List<MediaProcessingDetail>
}
