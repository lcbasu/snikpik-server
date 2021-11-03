package com.server.ud.pagination

import com.datastax.oss.protocol.internal.util.Bytes
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.data.domain.Slice

class CassandraPageV2<T>(slice: Slice<T>) {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    var count: Int? = null
    var content: List<T?>? = null
    var pagingState: String? = null
    var hasNext: Boolean? = null

    init {
        content = slice.content
        count = content?.size
        hasNext = slice.hasNext()
        pagingState = getPagingStateFromSlice(slice)
    }

    private fun <T> getPagingStateFromSlice(slice: Slice<T>): String? {
        return try {
            if (slice.hasNext()) {
                (slice.nextPageable() as CassandraPageRequest).pagingState?.let {
                    Bytes.toHexString(it)
                }
            } else {
                null
            }
        } catch (e: Exception) {
            logger.error("Error while getting paging state for slice: ${slice}")
            e.printStackTrace()
            null
        }
    }
}
