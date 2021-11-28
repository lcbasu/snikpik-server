//package com.server.ud.provider.post
//
//import org.springframework.stereotype.Component
//
//@Component
//class ESPostProvider {
//
////    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)
////
////    @Autowired
////    private lateinit var esPostRepository: ESPostRepository
////
////    fun save(post: Post) : ESPost? {
////        try {
////            val esPost = ESPost(
////                postId = post.postId,
////                userId = post.userId,
////                createdAt = DateUtils.getEpoch(post.createdAt),
////                postType = post.postType,
////                title = post.title,
////                description = post.description,
////                media = post.media,
////                tags = post.getHashTags().tags,
//////                categories = post.getCategories(),
////                locationId = post.locationId,
////                zipcode = post.zipcode,
////                locationLat = post.locationLat,
////                locationLng = post.locationLng,
////                locationName = post.locationName,
////                geoPoint = post.getGeoPointData(),
////                userHandle = post.userHandle,
////                userMobile = post.userMobile,
////                userName = post.userName,
////                userCountryCode = post.userCountryCode,
////                userProfile = post.getUserProfiles().firstOrNull()
////            )
////            val savedESPost = esPostRepository.save(esPost)
////
////            logger.info("Saved post to elastic search postId: ${savedESPost.postId}")
////            return savedESPost
////        } catch (e: Exception) {
////            e.printStackTrace()
////            logger.error("Saving post to elastic search failed for postId: ${post.postId}")
////            return null
////        }
////    }
//
//}
