package com.server.dk.cache

import com.server.dk.dto.AttendanceSummaryForEmployeeResponse
import com.server.dk.provider.AttendanceProvider
import com.github.benmanes.caffeine.cache.CacheLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.future
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

class AttendanceSummaryForEmployeeResponseCacheLoader(private val attendanceProvider: AttendanceProvider): CacheLoader<String, AttendanceSummaryForEmployeeResponse?> {
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)
    override fun reload(key: String, oldValue: AttendanceSummaryForEmployeeResponse): AttendanceSummaryForEmployeeResponse? = loadAsync(key).get()

    override fun asyncReload(key: String, oldValue: AttendanceSummaryForEmployeeResponse, executor: Executor): CompletableFuture<AttendanceSummaryForEmployeeResponse?> = loadAsync(key)

    override fun asyncLoad(key: String, executor: Executor): CompletableFuture<AttendanceSummaryForEmployeeResponse?> = loadAsync(key)

    override fun load(key: String): AttendanceSummaryForEmployeeResponse? = loadAsync(key).get()

    private fun loadAsync(key: String): CompletableFuture<AttendanceSummaryForEmployeeResponse?> {
        val request = KeyBuilder.parseKeyForAttendanceSummaryForEmployeeResponseCache(key)
        logger.info("run loadAsync for key: $key")
        return CoroutineScope(Dispatchers.Default).future {
            attendanceProvider.getAttendanceSummaryForEmployee(
                employeeId = request.employeeId,
                forYear = request.forYear,
                forMonth = request.forMonth
            )
        }
    }
}
