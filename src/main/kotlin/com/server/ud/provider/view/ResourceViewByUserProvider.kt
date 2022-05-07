package com.server.ud.provider.view

import com.server.common.utils.DateUtils
import com.server.ud.dao.view.ResourceViewsByUserRepository
import com.server.ud.dto.ResourceViewRequest
import com.server.ud.entities.view.ResourceViewsByUser
import com.server.ud.enums.PostType
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.utils.pagination.PaginationRequestUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class ResourceViewByUserProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    // 5 Minutes
    private val timeBetweenUniqueViewsInMinutes = 5

    @Autowired
    private lateinit var resourceViewsByUserRepository: ResourceViewsByUserRepository

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    fun getLastView(request: ResourceViewRequest): ResourceViewsByUser? =
        try {
            // Get the last saved view
            val pageRequest = paginationRequestUtil.createCassandraPageRequest(1, null)
            val views = resourceViewsByUserRepository.findAllByUserIdAndResourceIdOrderByCreatedAt(
                userId = request.userId,
                resourceId = request.resourceId,
                pageable = pageRequest as Pageable)
            val result = CassandraPageV2(views)
            // Get the first result as that is the most recent one
            logger.info("ResourceViewByUserProvider getLastView: result count: ${result.count}")
            logger.info("ResourceViewByUserProvider getLastView: result: ${result.content}")
            result.content?.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting views for userId: ${request.userId} & resourceId: ${request.resourceId} failed.")
            e.printStackTrace()
            null
        }

    fun save(request: ResourceViewRequest) : ResourceViewsByUser? {
        try {
            // Check if the last view happened within the last 5 minutes
            val lastView = getLastView(request)
            val isLastViewRecent = lastView?.let {
                val lastViewTime = it.createdAt
                val currentTime = DateUtils.getInstantNow()
                logger.info("ResourceViewByUserProvider save: lastViewTime: $lastViewTime, currentTime: $currentTime")
                logger.info("ResourceViewByUserProvider save: lastViewTime.plusSeconds(timeBetweenUniqueViewsInMinutes.toLong() * 60): ${lastViewTime.plusSeconds(timeBetweenUniqueViewsInMinutes.toLong() * 60)}")
                lastViewTime.plusSeconds(timeBetweenUniqueViewsInMinutes.toLong() * 60).isAfter(currentTime)
            } ?: false
            if (isLastViewRecent) {
                logger.info("Not saving view this as new view as the last view was done very recently for userId: ${request.userId} & resourceId: ${request.resourceId}")
                return null
            }
            logger.info("View is new or repeat view after the recency threshold for userId: ${request.userId} & resourceId: ${request.resourceId}")
            return resourceViewsByUserRepository.save(
                ResourceViewsByUser(
                    userId = request.userId,
                    resourceId = request.resourceId,
                    createdAt = DateUtils.getInstantNow()
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

}
