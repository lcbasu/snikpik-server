package com.server.ud.provider.post

import com.github.javafaker.Faker
import com.server.common.entities.User
import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.UniqueIdProvider
import com.server.ud.dao.es.article.ESArticleRepository
import com.server.ud.dao.post.PostRepository
import com.server.ud.dto.SavePostRequest
import com.server.ud.entities.es.article.Article
import com.server.ud.entities.es.article.Author
import com.server.ud.entities.post.Post
import com.server.ud.enums.PostType
import com.server.ud.model.HashTagData
import com.server.ud.model.HashTagsList
import com.server.ud.model.convertToString
import com.server.ud.service.post.ProcessPostSchedulerService
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
    private lateinit var esArticleRepository: ESArticleRepository

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Autowired
    private lateinit var processPostSchedulerService: ProcessPostSchedulerService

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
            val tags = HashTagsList(
                tags = listOf(
                    HashTagData(
                        tagId = "TID1",
                        displayName = "Tag ID 1",
                    ),
                    HashTagData(
                        tagId = "TID2",
                        displayName = "Tag ID 2",
                    )
                )
            )
            val post = Post(
                postId = uniqueIdProvider.getUniqueId(ReadableIdPrefix.PST.name),
                userId = user.id,
                createdAt = Instant.now(),
                postType = request.postType,
                title = request.title,
                description = request.description,
                media = "",
                tags = tags.convertToString(),
                categories = "EXTERIOR,KITCHEN",
                locationId = "LID1",
                zipcode = "562125",
                locationLat = 1.1,
                locationLng = 2.2,
                locationName = "Bangalore",
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
                description = faker.book().publisher()
            )

            posts.add(save(user, req))
        }

        val article = Article(
            id = uniqueIdProvider.getUniqueId(ReadableIdPrefix.PST.name),
            title = "Spring Data Elasticsearch",
            authors = listOf(
                Author("John Smith"),
                Author("John Doe")
            ))
        esArticleRepository.save(article)
        return posts.filterNotNull()
    }

}
