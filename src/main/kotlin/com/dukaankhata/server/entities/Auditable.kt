package com.dukaankhata.server.entities

import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import java.time.ZoneId
import javax.persistence.Column

open class Auditable {
    @CreatedDate
    @Column(name = "created_at")
    var createdAt: LocalDateTime? = null

    @CreatedBy
    @Column(name = "created_by")
    var createdBy: String? = null

    @LastModifiedDate
    @Column(name = "last_modified_at")
    var lastModifiedAt: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC"))

    @LastModifiedBy
    @Column(name = "last_modified_by")
    var lastModifiedBy: String? = null
}
