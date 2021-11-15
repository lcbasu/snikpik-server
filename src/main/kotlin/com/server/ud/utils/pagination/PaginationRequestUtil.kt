package com.server.ud.utils.pagination

import com.datastax.oss.protocol.internal.util.Bytes
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import java.nio.ByteBuffer

@Component
class PaginationRequestUtil {
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)
    fun createCassandraPageRequest(limit: Int, pagingState: String?): CassandraPageRequest? {
        return CassandraPageRequest.of(PageRequest.of(0, limit), getPageStateByteBuffer(pagingState))
    }

    fun getPageStateByteBuffer(pagingState: String?): ByteBuffer? {
        return try {
            pagingState?.let { Bytes.fromHexString(it) }
        } catch (e: Exception) {
            logger.error("Error while getting paging state for pagingState: $pagingState")
//            e.printStackTrace()
            null
        }
    }
}
