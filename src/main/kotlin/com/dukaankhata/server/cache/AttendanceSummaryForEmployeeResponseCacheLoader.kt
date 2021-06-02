package com.dukaankhata.server.cache

import com.dukaankhata.server.dto.AttendanceSummaryForEmployeeResponse
import com.dukaankhata.server.utils.AttendanceUtils
import com.github.benmanes.caffeine.cache.CacheLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.future
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

class AttendanceSummaryForEmployeeResponseCacheLoader(private val attendanceUtils: AttendanceUtils): CacheLoader<String, AttendanceSummaryForEmployeeResponse?> {
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)
    override fun reload(key: String, oldValue: AttendanceSummaryForEmployeeResponse): AttendanceSummaryForEmployeeResponse? = loadAsync(key).get()

    override fun asyncReload(key: String, oldValue: AttendanceSummaryForEmployeeResponse, executor: Executor): CompletableFuture<AttendanceSummaryForEmployeeResponse?> = loadAsync(key)

    override fun asyncLoad(key: String, executor: Executor): CompletableFuture<AttendanceSummaryForEmployeeResponse?> = loadAsync(key)

    override fun load(key: String): AttendanceSummaryForEmployeeResponse? = loadAsync(key).get()

    private fun loadAsync(key: String): CompletableFuture<AttendanceSummaryForEmployeeResponse?> {
        val request = KeyBuilder.parseKeyForAttendanceSummaryForEmployeeResponseCache(key)
        logger.info("run loadAsync for key: $key")
        return CoroutineScope(Dispatchers.Default).future {
            attendanceUtils.getAttendanceSummaryForEmployee(
                employeeId = request.employeeId,
                forYear = request.forYear,
                forMonth = request.forMonth
            )
        }
    }
}
