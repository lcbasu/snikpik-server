package com.server.ud.dao.post

import com.server.ud.entities.post.PostMongoDB
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface PostMongoDBRepository : MongoRepository<PostMongoDB?, String?> {
}
