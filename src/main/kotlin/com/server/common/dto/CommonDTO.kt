package com.server.common.dto

open class AuditableResponse (
    open val createdAt: Long,
    open val createdBy: String?,
    open val lastModifiedAt: Long,
    open val lastModifiedBy: String?,
    open val version : Long,
    open val deleted: Boolean,
)
