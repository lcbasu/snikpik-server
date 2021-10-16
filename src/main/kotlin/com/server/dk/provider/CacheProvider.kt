package com.server.dk.provider

import com.server.dk.cache.AttendanceSummaryForEmployeeResponseCacheLoader
import com.server.dk.cache.KeyBuilder
import com.server.dk.cache.ThirdPartyImageSearchResponseCacheLoader
import com.server.dk.dto.AttendanceSummaryForEmployeeRequest
import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class CacheProvider(private var attendanceProvider: AttendanceProvider, private var imageSearchProvider: ImageSearchProvider) {

    private val attendanceSummaryForEmployeeResponseCacheLoader =
        AttendanceSummaryForEmployeeResponseCacheLoader(attendanceProvider)

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
        ThirdPartyImageSearchResponseCacheLoader(imageSearchProvider)

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
