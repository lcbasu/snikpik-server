package com.server.shop.pagination

import com.server.shop.dto.AllSavedProductV3Response
import com.server.shop.dto.toSaveProductV3Response
import com.server.shop.entities.ProductV3
import org.springframework.data.domain.Slice
import javax.validation.constraints.Max
import javax.validation.constraints.Min


open class SQLPaginationRequest (
    @Min(0)
    open val page: Int = 0,
    @Min(1)
    @Max(1000)
    open val limit: Int = 10,
)

open class SQLPaginationResponse (
    // Deprecating this one as we want to rely onf Slice instead of Page.
    // We never have a use case where we want to show count of all the results.
//    @Min(1)
//    open val totalPages: Long? = null,
    open val askedForPage: Int,
    open val askedForLimit: Int,
    @Min(0)
    open val nextPage: Int,
    @Min(0)
    open val numFound: Int,
    open val hasNext: Boolean,
)

class SQLSlice<T>(slice: Slice<T>) {
    var nextPage: Int = 0
    var content: List<T?> = emptyList()
    var numFound: Int = 0
    var hasNext: Boolean = false
    var askedForPage: Int = 0
    var askedForLimit: Int = 0
    init {
        content = slice.content ?: emptyList()
        numFound = content?.size ?: 0
        hasNext = slice.hasNext()
        nextPage = if (slice.hasNext()) slice.nextPageable().pageNumber else slice.pageable.pageNumber
        askedForPage = slice.pageable.pageNumber
        askedForLimit = slice.pageable.pageSize
    }
}

fun SQLSlice<ProductV3>.toAllSavedProductV3Response(): AllSavedProductV3Response {
    this.apply {
        return AllSavedProductV3Response(
            products = content.mapNotNull { it?.toSaveProductV3Response() },
            askedForPage = askedForPage,
            askedForLimit = askedForLimit,
            nextPage = nextPage,
            numFound = numFound,
            hasNext = hasNext
        )
    }
}
