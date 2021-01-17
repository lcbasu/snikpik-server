package com.dukaankhata.server.entities

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

open class Auditable {
    @CreatedDate
    var createdAt: LocalDateTime? = null

    @LastModifiedDate
    var lastModifiedAt: LocalDateTime? = null
}
