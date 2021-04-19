package com.dukaankhata.server.entities

import com.dukaankhata.server.utils.DateUtils
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.EntityListeners
import javax.persistence.MappedSuperclass

@MappedSuperclass
@EntityListeners(value = [AuditingEntityListener::class])
abstract class Auditable : Serializable {
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime = DateUtils.dateTimeNow()

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    var createdBy: String? = null

    @LastModifiedDate
    @Column(name = "last_modified_at")
    var lastModifiedAt: LocalDateTime = DateUtils.dateTimeNow()

    @LastModifiedBy
    @Column(name = "last_modified_by")
    var lastModifiedBy: String? = null

//    @Version
    @Column(nullable = false)
    var version : Long = 0

    @Column(nullable = false)
    var deleted: Boolean = false
}
