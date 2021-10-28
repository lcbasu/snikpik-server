package com.server.ud.provider

import com.github.javafaker.Faker
import com.server.common.entities.User
import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.UniqueIdProvider
import com.server.dk.service.schedule.ProcessPostSchedulerService
import com.server.ud.dao.PostRepository
import com.server.ud.dto.SavePostRequest
import com.server.ud.entities.Post
import com.server.ud.enums.PostType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class PostProvider {

    @Autowired
    private lateinit var postRepository: PostRepository

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Autowired
    private lateinit var processPostSchedulerService: ProcessPostSchedulerService

    fun savePost(user: User, request: SavePostRequest) : Post? {
        try {
            val post = Post()
            post.postId = uniqueIdProvider.getUniqueId(ReadableIdPrefix.PST.name)
            post.userId = user.id
            post.createdAt = Instant.now()
            post.postType = request.postType
            post.title = request.title
            post.description = request.description
            val savedPost = postRepository.save(post)
            processPostSchedulerService.createPostProcessingJob(savedPost)
            return savedPost
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun fakeSaveUserPost(user: User): List<Post> {
        val totalFakePost = 25
        val posts = mutableListOf<Post?>()
        for (i in 1..totalFakePost) {
            val faker = Faker()

            val req = SavePostRequest(
                postType = PostType.GENERIC_POST,
                title = faker.book().title(),
                description = faker.book().publisher()
            )

            posts.add(savePost(user, req))
        }
        return posts.filterNotNull()
    }

}
