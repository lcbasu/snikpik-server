package com.server.ud.provider.post

import com.github.javafaker.Faker
import com.server.common.entities.User
import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.UniqueIdProvider
import com.server.ud.service.post.ProcessPostSchedulerService
import com.server.ud.dao.post.PostRepository
import com.server.ud.dto.SavePostRequest
import com.server.ud.entities.post.Post
import com.server.ud.enums.PostType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class PostProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var postRepository: PostRepository

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Autowired
    private lateinit var processPostSchedulerService: ProcessPostSchedulerService

    fun save(user: User, request: SavePostRequest) : Post? {
        try {
            val post = Post(
                postId = uniqueIdProvider.getUniqueId(ReadableIdPrefix.PST.name),
                userId = user.id,
                createdAt = Instant.now(),
                postType = request.postType,
                title = request.title,
                description = request.description,
                media = "",
                tags = "TID1,TID2",
                categories = "CID1,CID2",
                locationId = "LID1",
                locationLat = 0.0,
                locationLng = 0.0,
                locationName = "Bangalore",)
            val savedPost = postRepository.save(post)
            processPostSchedulerService.createPostProcessingJob(savedPost)
            return savedPost
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun fakeSave(user: User): List<Post> {
        val totalFakePost = 25
        val posts = mutableListOf<Post?>()
        for (i in 1..totalFakePost) {
            val faker = Faker()

            val req = SavePostRequest(
                postType = PostType.GENERIC_POST,
                title = faker.book().title(),
                description = faker.book().publisher()
            )

            posts.add(save(user, req))
        }
        return posts.filterNotNull()
    }

    fun postProcessPost(postId: String) {
        logger.info("Do post processing for postId: $postId")
    }

}
