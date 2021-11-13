package com.server.common.dao

import com.server.common.entities.MediaProcessingDetail
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MediaProcessingDetailRepository : JpaRepository<MediaProcessingDetail?, String?>
