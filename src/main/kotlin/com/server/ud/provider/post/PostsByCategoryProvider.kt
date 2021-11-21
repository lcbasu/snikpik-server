package com.server.ud.provider.post

import com.server.common.utils.DateUtils
import com.server.ud.dao.post.PostsByCategoryRepository
import com.server.ud.dto.ExploreFeedRequest
import com.server.ud.entities.post.Post
import com.server.ud.entities.post.PostsByCategory
import com.server.ud.enums.CategoryV2
import com.server.ud.enums.PostType
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.utils.pagination.PaginationRequestUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class PostsByCategoryProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var postsByCategoryRepository: PostsByCategoryRepository

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    val getFeedFromPastDays = 7L

    fun save(post: Post, categoryId: CategoryV2): PostsByCategory? {
        try {
            val postsByZipcode = PostsByCategory(
                categoryId = categoryId,
                forDate = DateUtils.getInstantDate(post.createdAt),
                createdAt = post.createdAt,
                postId = post.postId,
                postType = post.postType,
                userId = post.userId,
                mediaPresenceType = post.mediaPresenceType,
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

    fun getFeedForCategory(request: ExploreFeedRequest): CassandraPageV2<PostsByCategory> {
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(request.limit, request.pagingState)
         val posts = postsByCategoryRepository.findAllByCategoryIdAndPostTypeAndForDate(request.category, PostType.GENERIC_POST, DateUtils.getInstantFromLocalDateTime(DateUtils.parseStandardDate(request.forDate)), pageRequest as Pageable)
        return CassandraPageV2(posts)
    }

}
