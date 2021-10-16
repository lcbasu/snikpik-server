package com.server.ud.provider

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.server.dk.entities.User
import com.server.dk.enums.ReadableIdPrefix
import com.server.dk.provider.UniqueIdProvider
import com.server.dk.utils.DateUtils
import com.server.ud.dao.UserPostRepository
import com.server.ud.dto.SaveUserPostRequest
import com.server.ud.entities.UserPost
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class UserPostProvider {

    @Autowired
    private lateinit var userPostRepository: UserPostRepository

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Autowired
    private lateinit var amazonDynamoDB: AmazonDynamoDB

    fun saveProduct(user: User, request: SaveUserPostRequest) : UserPost? {
        try {
//            val tableRequest = DynamoDBMapper(amazonDynamoDB)
//                .generateCreateTableRequest(UserPost::class.java)
//            tableRequest.provisionedThroughput = ProvisionedThroughput(1L, 1L)
//            amazonDynamoDB.createTable(tableRequest)

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

}
