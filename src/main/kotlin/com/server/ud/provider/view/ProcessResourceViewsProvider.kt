package com.server.ud.provider.view

import com.server.ud.dto.toResourceViewRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ProcessResourceViewsProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var resourceViewByUserProvider: ResourceViewByUserProvider

    @Autowired
    private lateinit var resourceViewsCountByResourceProvider: ResourceViewsCountByResourceProvider

    fun processResourceView(viewId: String) {
        logger.info("Start processing for viewId: $viewId")
        val savedView = resourceViewByUserProvider.save(viewId.toResourceViewRequest())
        savedView?.let {
            resourceViewsCountByResourceProvider.incrementResourceViewCount(savedView.resourceId)
        }
    }

}
