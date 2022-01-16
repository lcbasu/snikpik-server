package com.server.ud.provider.view

import com.google.firebase.cloud.FirestoreClient
import com.server.ud.dao.view.ResourceViewsCountByResourceRepository
import com.server.ud.entities.view.ResourceViewsCountByResource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ResourceViewsCountByResourceProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var repository: ResourceViewsCountByResourceRepository

//    @Autowired
//    private lateinit var ablyProvider: AblyProvider

    fun getResourceViewsCountByResource(resourceId: String): ResourceViewsCountByResource? =
        try {
            val resourceResourceViewCount = repository.findAllByResourceId(resourceId)
            if (resourceResourceViewCount.size > 1) {
                error("More than one views has same resourceId: $resourceId")
            }
            resourceResourceViewCount.getOrElse(0) {
                val resourceViewsCountByResource = ResourceViewsCountByResource()
                resourceViewsCountByResource.viewsCount = 0
                resourceViewsCountByResource.resourceId = resourceId
                resourceViewsCountByResource
            }
        } catch (e: Exception) {
            logger.error("Getting ResourceViewsCountByResource for $resourceId failed.")
            e.printStackTrace()
            null
        }

    fun incrementResourceViewCount(resourceId: String) {
        repository.incrementResourceViewCount(resourceId)
//        getResourceViewsCountByResource(resourceId)?.let {
//            ablyProvider.publishToChannel(
//                channelName = "resource-views-count",
//                "update",
//                it
//            )
//        }
        logger.warn("Increased views for resourceId: $resourceId")

        // Not required to be realtime
//        saveResourceViewsCountByResourceToFirestore(getResourceViewsCountByResource(resourceId))
    }

    private fun saveResourceViewsCountByResourceToFirestore (resourceViewsCountByResource: ResourceViewsCountByResource?) {
        GlobalScope.launch {
            if (resourceViewsCountByResource?.resourceId == null) {
                logger.error("No resource id found in resourceViewsCountByResource. So skipping saving it to firestore.")
                return@launch
            }
            FirestoreClient.getFirestore()
                .collection("resource_views_count_by_resource")
                .document(resourceViewsCountByResource.resourceId!!)
                .set(resourceViewsCountByResource)
        }
    }

//    fun saveAllToFirestore() {
//        repository.findAll().forEach {
//            saveResourceViewsCountByResourceToFirestore(it)
//        }
//    }

}
