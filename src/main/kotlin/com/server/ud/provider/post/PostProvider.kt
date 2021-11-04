package com.server.ud.provider.post

import com.github.javafaker.Faker
import com.server.common.entities.User
import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.UniqueIdProvider
import com.server.ud.dao.post.PostRepository
import com.server.ud.dto.PaginatedRequest
import com.server.ud.dto.SavePostRequest
import com.server.ud.dto.sampleLocationRequests
import com.server.ud.entities.post.Post
import com.server.ud.enums.CategoryV2
import com.server.ud.enums.PostType
import com.server.ud.model.HashTagData
import com.server.ud.model.HashTagsList
import com.server.ud.model.convertToString
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.provider.like.LikesCountByResourceProvider
import com.server.ud.provider.location.LocationProvider
import com.server.ud.service.post.ProcessPostSchedulerService
import com.server.ud.utils.pagination.PaginationRequestUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.time.Instant
import kotlin.random.Random

@Component
class PostProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var postRepository: PostRepository

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Autowired
    private lateinit var locationProvider: LocationProvider

    @Autowired
    private lateinit var processPostSchedulerService: ProcessPostSchedulerService

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    @Autowired
    private lateinit var likesCountByResourceProgression: LikesCountByResourceProvider

    fun getPost(postId: String): Post? =
        try {
            val posts = postRepository.findAllByPostId(postId)
            if (posts.size > 1) {
                error("More than one post has same postId: $postId")
            }
            posts.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting Post for $postId failed.")
            e.printStackTrace()
            null
        }


    fun save(user: User, request: SavePostRequest) : Post? {
        try {
            val location = request.locationRequest?.let {
                locationProvider.save(user, it)
            }
            val post = Post(
                postId = uniqueIdProvider.getUniqueId(ReadableIdPrefix.PST.name),
                userId = user.id,
                createdAt = Instant.now(),
                postType = request.postType,
                title = request.title,
                description = request.description,
                media = "",
                tags = request.tags.convertToString(),
                categories = request.categories.joinToString(","),
                locationId = location?.locationId,
                zipcode = location?.zipcode,
                locationLat = location?.lat,
                locationLng = location?.lng,
                locationName = location?.name,
                googlePlaceId = location?.googlePlaceId
            )
            val savedPost = postRepository.save(post)
            processPostSchedulerService.createPostProcessingJob(savedPost)
            return savedPost
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun fakeSave(user: User, countOfPost: Int): List<Post> {
        val posts = mutableListOf<Post?>()
        for (i in 1..countOfPost) {
            val faker = Faker()
            val req = SavePostRequest(
                postType = PostType.GENERIC_POST,
                title = faker.book().title(),
                description = faker.book().publisher(),
                tags = HashTagsList(listOf(
                    HashTagData(
                        tagId = "TID1",
                        displayName = "Tag ID 1",
                    ),
                    HashTagData(
                        tagId = "TID2",
                        displayName = "Tag ID 2",
                    )
                )),
                categories = setOf(CategoryV2.KITCHEN, CategoryV2.EXTERIOR),
                locationRequest = sampleLocationRequests[Random.nextInt(sampleLocationRequests.size)]
            )
            posts.add(save(user, req))
        }
        posts.filterNotNull().map {
            // Simulate liking
            val randomCount = Random.nextInt(4, 20)
            for (i in 1..randomCount) {
                likesCountByResourceProgression.increaseLike(it.postId)
            }
            // Try decreasing likes more than it has been increased and check behaviour
            for (i in 1..(randomCount+1)) {
                likesCountByResourceProgression.decreaseLike(it.postId)
            }
        }
        return posts.filterNotNull()
    }

    fun getPosts(request: PaginatedRequest): CassandraPageV2<Post?>? {
        return getPageOfPosts(request.limit, request.pagingState)
    }

    fun getPageOfPosts(limit: Int, pagingState: String?): CassandraPageV2<Post?>? {
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
        return getPageOfUsers(pageRequest)
    }

    fun getPageOfUsers(cassandraPageRequest: CassandraPageRequest?): CassandraPageV2<Post?>? {
        val userSlice = postRepository.findAll(cassandraPageRequest as Pageable)
        return CassandraPageV2(userSlice)
    }

}
