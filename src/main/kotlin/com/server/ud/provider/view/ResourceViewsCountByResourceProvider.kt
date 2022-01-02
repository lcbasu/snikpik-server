package com.server.ud.provider.view

import com.server.common.provider.AblyProvider
import com.server.ud.dao.view.ResourceViewsCountByResourceRepository
import com.server.ud.entities.view.ResourceViewsCountByResource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ResourceViewsCountByResourceProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var repository: ResourceViewsCountByResourceRepository

    @Autowired
    private lateinit var ablyProvider: AblyProvider

    fun getResourceViewsCountByResource(resourceId: String): ResourceViewsCountByResource? =
        try {
            val resourceResourceViewCount = repository.findAllByResourceId(resourceId)
            if (resourceResourceViewCount.size > 1) {
                error("More than one views has same resourceId: $resourceId")
            }
            resourceResourceViewCount.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting ResourceViewsCountByResource for $resourceId failed.")
            e.printStackTrace()
            null
        }

    fun incrementResourceViewCount(resourceId: String) {
        repository.incrementResourceViewCount(resourceId)
        getResourceViewsCountByResource(resourceId)?.let {
            ablyProvider.publishToChannel(
                channelName = "resource-views-count",
                "update",
                it
            )
        }
        logger.warn("Increased views for resourceId: $resourceId")
    }

}
