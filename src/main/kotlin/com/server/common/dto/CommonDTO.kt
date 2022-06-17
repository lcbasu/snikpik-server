package com.server.common.dto

import javax.validation.constraints.Max
import javax.validation.constraints.Min

open class AuditableResponse (
    open val createdAt: Long,
    open val createdBy: String?,
    open val lastModifiedAt: Long,
    open val lastModifiedBy: String?,
    open val version : Long,
    open val deleted: Boolean,
)

open class PaginationRequest (
    @Min(1)
    @Max(1000)
    open val limit: Int = 10,
    open val pagingState: String? = null,
)

open class PaginationResponse (
    open val count: Int? = null,
    open val pagingState: String? = null,
    open val hasNext: Boolean? = null
)
