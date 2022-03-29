package com.server.ud.provider.post

import com.server.common.model.getMediaTypeDetailsFromJsonString
import com.server.common.utils.DateUtils
import com.server.ud.dao.post.PostMongoDBRepository
import com.server.ud.entities.post.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PostMongoDBProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var postMongoDBRepository: PostMongoDBRepository

    fun savePostToMongoDB(savedPost: Post): PostMongoDB {
        val postInMongoDB = PostMongoDB (

            postId = savedPost.postId,

            createdAt = DateUtils.toDate(savedPost.createdAt),

            userId = savedPost.userId,

            postType = savedPost.postType,

            mediaTypeDetail = getMediaTypeDetailsFromJsonString(savedPost.sourceMedia),

            labels = savedPost.labels,

            title = savedPost.title,

            userHandle = savedPost.userHandle,

            userName = savedPost.userName,

            userMobile = savedPost.userMobile,

            userCountryCode = savedPost.userCountryCode,

            userProfiles = savedPost.getUserProfiles(),

            description = savedPost.description,

            media = savedPost.getMediaDetails(),

            sourceMedia = savedPost.getSourceMediaDetails(),

            tags = savedPost.getHashTags(),

            categories = savedPost.getCategories(),

            geoPoint = getMongoGeoPoint(lat = savedPost.locationLat, lng = savedPost.locationLng),

            locationId = savedPost.locationId,

            googlePlaceId = savedPost.googlePlaceId,

            zipcode = savedPost.zipcode,

            locationName = savedPost.locationName,

            locationLat = savedPost.locationLat,

            locationLng = savedPost.locationLng,

            locality = savedPost.locality,

            subLocality = savedPost.subLocality,

            route = savedPost.route,

            city = savedPost.city,

            state = savedPost.state,

            country = savedPost.country,

            countryCode = savedPost.countryCode,

            completeAddress = savedPost.completeAddress,
        )

        return postMongoDBRepository.save(postInMongoDB)
    }

}
