package com.dukaankhata.server.utils

import com.dukaankhata.server.cache.AttendanceSummaryForEmployeeResponseCacheLoader
import com.dukaankhata.server.cache.KeyBuilder
import com.dukaankhata.server.cache.ThirdPartyImageSearchResponseCacheLoader
import com.dukaankhata.server.dto.AttendanceSummaryForEmployeeRequest
import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class CacheUtils(private var attendanceUtils: AttendanceUtils, private var imageSearchUtils: ImageSearchUtils) {

    private val attendanceSummaryForEmployeeResponseCacheLoader =
        AttendanceSummaryForEmployeeResponseCacheLoader(attendanceUtils)

    private val attendanceSummaryForEmployeeResponseCache by lazy {
        Caffeine
            .newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(4, TimeUnit.HOURS)
            .buildAsync(attendanceSummaryForEmployeeResponseCacheLoader)
    }

    fun getAttendanceSummaryForEmployee(attendanceSummaryForEmployeeRequest: AttendanceSummaryForEmployeeRequest) =
        attendanceSummaryForEmployeeResponseCache.get(KeyBuilder.getKeyForAttendanceSummaryForEmployeeResponseCache(attendanceSummaryForEmployeeRequest))


    private val thirdPartyImageSearchResponseCacheLoader =
        ThirdPartyImageSearchResponseCacheLoader(imageSearchUtils)

    // Keep this for 7 days later move it to 1 month after checking pod size in AWS
    private val thirdPartyImageSearchResponseCache by lazy {
        Caffeine
            .newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(7, TimeUnit.DAYS)
            .buildAsync(thirdPartyImageSearchResponseCacheLoader)
    }

    fun getThirdPartyImageSearchResponse(query: String) =
        thirdPartyImageSearchResponseCache.get(KeyBuilder.getKeyForThirdPartyImageSearchResponseCache(query))

}
