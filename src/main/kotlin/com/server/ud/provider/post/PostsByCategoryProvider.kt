package com.server.ud.provider.post

import com.server.common.utils.DateUtils
import com.server.ud.dao.post.PostsByCategoryRepository
import com.server.ud.entities.post.Post
import com.server.ud.entities.post.PostsByCategory
import com.server.ud.enums.CategoryV2
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PostsByCategoryProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var postsByCategoryRepository: PostsByCategoryRepository

    fun save(post: Post, categoryId: CategoryV2): PostsByCategory? {
        try {
            val postsByZipcode = PostsByCategory(
                categoryId = categoryId,
                forDate = DateUtils.toStringForDate(DateUtils.dateTimeNow()),
                createdAt = DateUtils.getInstantNow(),
                postId = post.postId,
                postType = post.postType,
                userId = post.userId,
                locationId = post.locationId,
                zipcode = post.zipcode,
                locationName = post.locationName,
                locationLat = post.locationLat,
                locationLng = post.locationLng,
                title = post.title,
                description = post.description,
                media = post.media,
                tags = post.tags,
                categories = post.categories,
            )
            return postsByCategoryRepository.save(postsByZipcode)
        } catch (e: Exception) {
            logger.error("Saving _root_ide_package_.com.server.ud.entities.post.PostsByCategory filed for ${post.postId}.")
            e.printStackTrace()
            return null
        }
    }

}
