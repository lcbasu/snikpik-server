package com.server.ud.provider

import com.github.javafaker.Faker
import com.server.common.entities.User
import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.UniqueIdProvider
import com.server.common.utils.DateUtils
import com.server.ud.dao.UserPostRepository
import com.server.ud.dto.SaveUserPostRequest
import com.server.ud.entities.UserPost
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Component


@Component
class UserPostProvider {

    @Autowired
    private lateinit var userPostRepository: UserPostRepository

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    fun getPosts(user: User, request: SaveUserPostRequest) : Slice<UserPost?> {
        val pageRequest: Pageable = CassandraPageRequest.of(0, 5)
        return userPostRepository.findAll(pageRequest)
    }

    fun savePost(user: User, request: SaveUserPostRequest) : UserPost? {
        try {
            val userPost = UserPost()
            userPost.id = uniqueIdProvider.getUniqueId(ReadableIdPrefix.PST.name)
            userPost.userId = user.id
            userPost.postedAt = DateUtils.getCurrentTimeInEpoch()
            userPost.title = request.title
            userPost.description = request.description
            return userPostRepository.save(userPost)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun fakeSaveUserPost(user: User): List<UserPost> {
        val totalFakePost = 100
        val posts = mutableListOf<UserPost?>()
        for (i in 1..totalFakePost) {
            val faker = Faker()

            val req = SaveUserPostRequest(
                title = faker.book().title(),
                description = faker.book().publisher()
            )

            posts.add(savePost(user, req))
        }
        return posts.filterNotNull()
    }

}
