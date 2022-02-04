package com.server.ud.provider.faker

import com.algolia.search.SearchClient
import com.github.javafaker.Faker
import com.server.common.client.RedisClient
import com.server.common.dto.AllProfileTypeResponse
import com.server.common.dto.convertToString
import com.server.common.dto.toProfileTypeResponse
import com.server.common.enums.*
import com.server.common.model.MediaDetailsV2
import com.server.common.model.SingleMediaDetail
import com.server.common.model.convertToString
import com.server.common.model.sampleMedia
import com.server.common.provider.CassandraTableModificationProvider
import com.server.common.provider.RandomIdProvider
import com.server.common.provider.SecurityProvider
import com.server.common.provider.communication.CommunicationProvider
import com.server.common.utils.DateUtils
import com.server.ud.dao.bookmark.BookmarkRepository
import com.server.ud.dao.like.LikeRepository
import com.server.ud.dao.location.LocationRepository
import com.server.ud.dao.post.PostRepository
import com.server.ud.dao.social.SocialRelationRepository
import com.server.ud.dao.user.UserV2Repository
import com.server.ud.dto.*
import com.server.ud.entities.bookmark.Bookmark
import com.server.ud.entities.comment.Comment
import com.server.ud.entities.like.Like
import com.server.ud.entities.post.Post
import com.server.ud.entities.reply.Reply
import com.server.ud.entities.social.SocialRelation
import com.server.ud.entities.user.UserV2
import com.server.ud.enums.*
import com.server.ud.model.sampleHashTagsIds
import com.server.ud.provider.bookmark.BookmarkProvider
import com.server.ud.provider.bookmark.BookmarksByResourceProvider
import com.server.ud.provider.bookmark.BookmarksByUserProvider
import com.server.ud.provider.cache.UDCacheProvider
import com.server.ud.provider.comment.CommentProvider
import com.server.ud.provider.like.LikeProvider
import com.server.ud.provider.like.LikesByResourceProvider
import com.server.ud.provider.like.LikesByUserProvider
import com.server.ud.provider.location.LocationProvider
import com.server.ud.provider.location.LocationsByUserProvider
import com.server.ud.provider.location.LocationsByZipcodeProvider
import com.server.ud.provider.location.NearbyZipcodesByZipcodeProvider
import com.server.ud.provider.one_off.OneOffIndexUsersToAlgolia
import com.server.ud.provider.one_off.OneOffSaveChatsV2ToFirestore
import com.server.ud.provider.one_off.OneOffSaveDataToFirestore
import com.server.ud.provider.post.*
import com.server.ud.provider.reply.ReplyProvider
import com.server.ud.provider.social.FollowersByUserProvider
import com.server.ud.provider.social.FollowingsByUserProvider
import com.server.ud.provider.social.SocialRelationProcessingProvider
import com.server.ud.provider.social.SocialRelationProvider
import com.server.ud.provider.user.UserV2Provider
import com.server.ud.provider.user.UsersByProfileCategoryProvider
import com.server.ud.provider.user.UsersByProfileTypeProvider
import com.server.ud.provider.user.UsersByZipcodeProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import redis.clients.jedis.JedisPool
import kotlin.random.Random

@Component
class FakerProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    private val minUsersToFake = 2
    private val maxUsersToFake = 5
    private val minPostToFake = 2
    private val maxPostToFake = 5
    private val minCommentsToFake = 2
    private val maxCommentsToFake = 3
    private val minRepliesToFake = 2
    private val maxRepliesToFake = 3

    @Autowired
    private lateinit var userV2Provider: UserV2Provider

    @Autowired
    private lateinit var randomIdProvider: RandomIdProvider

    @Autowired
    private lateinit var postProvider: PostProvider

    @Autowired
    private lateinit var commentProvider: CommentProvider

    @Autowired
    private lateinit var replyProvider: ReplyProvider

    @Autowired
    private lateinit var likeProvider: LikeProvider

    @Autowired
    private lateinit var bookmarkProvider: BookmarkProvider

    @Autowired
    private lateinit var socialRelationProvider: SocialRelationProvider

    @Autowired
    private lateinit var socialRelationProcessingProvider: SocialRelationProcessingProvider

    @Autowired
    private lateinit var searchClient: SearchClient

    @Autowired
    private lateinit var udCacheProvider: UDCacheProvider

    @Autowired
    private lateinit var locationProvider: LocationProvider

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    fun createFakeDataRandomly(): List<Any> {

        val result = mutableListOf<Any?>()

        // Create some fake users -> Done
        // Create some fake professionals -> Done
        // Create some fake suppliers -> Done
        // Create some fake following and followers -> Done
        // Create some fake posts
        // Create some fake comments
        // Create some fake likes
        // Create some fake bookmarks

        val faker = Faker()

        // Use ONLY provider methods to change values
        // This will help in testing the flow as well

        val usersToCreate = Random.nextInt(minUsersToFake, maxUsersToFake)
        val userLocations = locationProvider.getSampleLocationRequestsFromCities(LocationFor.USER)
        val usersV2 = mutableListOf<UserV2>()
        for (i in 1..usersToCreate) {
            val profiles = ProfileType.values().toList().shuffled().take(Random.nextInt(1, ProfileType.values().size))
            val location = userLocations.shuffled().first()
            val id = randomIdProvider.getRandomIdFor(ReadableIdPrefix.FKE)
            val userV2 = userV2Provider.saveUserV2(UserV2 (
                userId = "${ReadableIdPrefix.USR}$id",
                createdAt = DateUtils.getInstantNow(),
                absoluteMobile = "",
                countryCode = "",
                handle = faker.name().username(),
                dp = MediaDetailsV2(listOf(SingleMediaDetail(
                    mediaUrl = "https://i.pravatar.cc/150?u=${id}",
                    mediaType = MediaType.IMAGE,
                    width = 150,
                    height = 150,
                    mediaQualityType = MediaQualityType.HIGH,
                    mimeType = "jpg"
                ))).convertToString(),
                uid = id,
                anonymous = false,
                verified = Random.nextInt(1, 100) % 5 == 0,
                profiles = AllProfileTypeResponse(
                    profiles.map { it.toProfileTypeResponse() }
                ).convertToString(),
                fullName = faker.name().fullName(),
                notificationToken = null,
                notificationTokenProvider = NotificationTokenProvider.FIREBASE
            ), ProcessingType.NO_PROCESSING) ?: error("Error saving userV2 for userId: ${id}")
            // This save will also take care of creating the job to process location data
            userV2Provider.updateUserV2Location(UpdateUserV2LocationRequest (
                updateTypes = setOf(UserLocationUpdateType.CURRENT, UserLocationUpdateType.PERMANENT),
                lat = location.lat!!,
                lng = location.lng!!,
                zipcode = location.zipcode!!,
                name = location.name,
                googlePlaceId = location.googlePlaceId,
            ), userV2.userId)
            usersV2.add(userV2)
        }

        // Follow random people
        val socialRelations = mutableListOf<SocialRelation?>()
        for(userV2 in usersV2) {
            val usersToFollow = usersV2.shuffled().take(Random.nextInt(1, usersV2.size))
            for(userToFollow in usersToFollow) {
                if (userToFollow.userId != userV2.userId) { // Do not follow oneself
                    socialRelations.add(socialRelationProvider.save(
                        fromUserId = userV2.userId,
                        toUserId = userToFollow.userId,
                        following = true,
                        scheduleJob = false
                    ))
                }
            }
        }
        result.addAll(socialRelations)

        socialRelations.filterNotNull().map {
            socialRelationProcessingProvider.processSocialRelation(
                fromUserId = it.fromUserId,
                toUserId = it.toUserId,
            )
        }

        val usersToCreatePostsFor = usersV2.shuffled().take(Random.nextInt(1, usersV2.size))
        for (userV2 in usersToCreatePostsFor) {
            result.addAll(createFakeData(
                userId = userV2.userId,
                request = FakerRequest(
                    countOfPost = Random.nextInt(minPostToFake, maxPostToFake)
                ),
            ))
        }

        return result.filterNotNull()
    }

    fun createFakeData(userId: String, request: FakerRequest): List<Any> {
        if (request.countOfPost > maxPostToFake) {
            error("Max of $maxPostToFake fake data points in any category is allowed are allowed to be created at one time")
        }

        if (request.countOfPost < 1) {
            error("Minimum value required is 1 for posts.")
        }

        val posts = mutableListOf<Post?>()
        val comments = mutableListOf<Comment?>()
        val replies = mutableListOf<Reply?>()
        val likes = mutableListOf<Like?>()
        val bookmarks = mutableListOf<Bookmark?>()
        val postLocations = locationProvider.getSampleLocationRequestsFromCities(LocationFor.USER)
        val faker = Faker()

        for (i in 1..request.countOfPost) {
            val postType = PostType.values().toList().shuffled().first()
            val categories = CategoryV2.values().filter { it != CategoryV2.ALL }.toList().shuffled().take(Random.nextInt(1, CategoryV2.values().size))
            val location = postLocations.shuffled().first().copy(
                locationFor = if (postType == PostType.GENERIC_POST) LocationFor.GENERIC_POST else LocationFor.COMMUNITY_WALL_POST,
            )
            val req = SavePostRequest(
                postType = postType,
                title = faker.book().title(),
                description = faker.lorem().sentence(300),
                tags = sampleHashTagsIds.shuffled().take(Random.nextInt(1, sampleHashTagsIds.size)).toSet(),
                categories = categories.toSet(),
                locationRequest = location,
                mediaDetails = sampleMedia.shuffled()[Random.nextInt(sampleMedia.size)]
            )
            posts.add(postProvider.save(userId, req))
        }

        posts.filterNotNull().map {
            val randomCount = Random.nextInt(0, maxCommentsToFake)
            for (i in 1..randomCount) {
                comments.add(commentProvider.save(userId, SaveCommentRequest(
                    postId = it.postId,
                    postType = it.postType,
                    text = faker.lorem().sentence(),
                )))
            }
        }

        comments.filterNotNull().map {
            val randomCount = Random.nextInt(0, maxRepliesToFake)
            for (i in 1..randomCount) {
                replies.add(replyProvider.save(userId, SaveCommentReplyRequest(
                    commentId = it.commentId,
                    postId = it.postId,
                    postType = it.postType,
                    text = faker.lorem().sentence(),
                )))
            }
        }


        posts.filterNotNull().map {
            likes.add(likeProvider.save(userId, SaveLikeRequest(
                resourceType = getResourcePostType(it.postType),
                resourceId = it.postId,
                action = LikeUpdateAction.ADD,
            )))
            bookmarks.add(bookmarkProvider.save(userId, SaveBookmarkRequest(
                    resourceType = getResourcePostType(it.postType),
                    resourceId = it.postId,
                    action = BookmarkUpdateAction.ADD,
            )))
        }

        comments.filterNotNull().map {
            likes.add(likeProvider.save(userId, SaveLikeRequest(
                resourceType = getResourceCommentType(it.postType),
                resourceId = it.commentId,
                action = LikeUpdateAction.ADD,
            )))
            bookmarks.add(bookmarkProvider.save(userId, SaveBookmarkRequest(
                resourceType = getResourceCommentType(it.postType),
                resourceId = it.commentId,
                action = BookmarkUpdateAction.ADD,
            )))
        }

        replies.filterNotNull().map {
            likes.add(likeProvider.save(userId, SaveLikeRequest(
                resourceType = getResourceReplyType(it.postType),
                resourceId = it.replyId,
                action = LikeUpdateAction.ADD,
            )))
            bookmarks.add(bookmarkProvider.save(userId, SaveBookmarkRequest(
                resourceType = getResourceReplyType(it.postType),
                resourceId = it.replyId,
                action = BookmarkUpdateAction.ADD,
            )))
        }


        val result = mutableListOf<Any?>()

        posts.map { result.add(it) }
        comments.map { result.add(it) }
        replies.map { result.add(it) }
        likes.map { result.add(it) }
        bookmarks.map { result.add(it) }

        return result.filterNotNull()

    }

    @Autowired
    private lateinit var bookmarkRepository: BookmarkRepository

    @Autowired
    private lateinit var bookmarksByResourceProvider: BookmarksByResourceProvider

    @Autowired
    private lateinit var bookmarksByUserProvider: BookmarksByUserProvider

    @Autowired
    private lateinit var likeRepository: LikeRepository

    @Autowired
    private lateinit var likesByResourceProvider: LikesByResourceProvider

    @Autowired
    private lateinit var likesByUserProvider: LikesByUserProvider

    @Autowired
    private lateinit var locationRepository: LocationRepository

    @Autowired
    private lateinit var locationsByUserProvider: LocationsByUserProvider

    @Autowired
    private lateinit var locationsByZipcodeProvider: LocationsByZipcodeProvider

    @Autowired
    private lateinit var postRepository: PostRepository

    @Autowired
    private lateinit var nearbyPostsByZipcodeProvider: NearbyPostsByZipcodeProvider

    @Autowired
    private lateinit var nearbyVideoPostsByZipcodeProvider: NearbyVideoPostsByZipcodeProvider

    @Autowired
    private lateinit var postsByCategoryProvider: PostsByCategoryProvider

    @Autowired
    private lateinit var postsByHashTagProvider: PostsByHashTagProvider

    @Autowired
    private lateinit var postsByZipcodeProvider: PostsByZipcodeProvider

    @Autowired
    private lateinit var nearbyZipcodesByZipcodeProvider: NearbyZipcodesByZipcodeProvider

    @Autowired
    private lateinit var socialRelationRepository: SocialRelationRepository

    @Autowired
    private lateinit var followersByUserProvider: FollowersByUserProvider

    @Autowired
    private lateinit var followingsByUserProvider: FollowingsByUserProvider

    @Autowired
    private lateinit var userV2Repository: UserV2Repository

    @Autowired
    private lateinit var usersByProfileCategoryProvider: UsersByProfileCategoryProvider

    @Autowired
    private lateinit var usersByProfileTypeProvider: UsersByProfileTypeProvider

    @Autowired
    private lateinit var usersByZipcodeProvider: UsersByZipcodeProvider


    @Autowired
    private lateinit var cassandraTableModificationProvider: CassandraTableModificationProvider

    @Autowired
    private lateinit var oneOffSaveDataToFirestore: OneOffSaveDataToFirestore

    @Autowired
    private lateinit var oneOffIndexUsersToAlgolia: OneOffIndexUsersToAlgolia

    @Autowired
    private lateinit var oneOffSaveChatsV2ToFirestore: OneOffSaveChatsV2ToFirestore

    @Autowired
    private lateinit var communicationProvider: CommunicationProvider

    @Autowired
    private lateinit var redisClient: RedisClient

    fun doSomething(): Any {
//        recoverDeletedData()
//        cassandraTableModificationProvider.addNewColumns()
//        oneOffSaveDataToFirestore.saveMetadataToFirestore()
//       oneOffIndexUsersToAlgolia.saveUsersToAlgolia()
//        oneOffSaveChatsV2ToFirestore.saveChatsV2ToFirestore()
//        communicationProvider.sendSMS("+919742097429", "This is a test message")
//        userV2Provider.saveAllForAuthV2()

//        tryoutCache("SomeKey")

//        redisClient.set("SomeKey111", "SomeValue777")
//        redisClient.set("SomeKey333", "SomeValue444")
//        return "Something was done... ${redisClient.get("SomeKey111")} - ${redisClient.get("SomeKey333")}"

        return "Something was done..."
    }

//    private fun recoverDeletedData() {
//        GlobalScope.launch {
//            // Bookmark
//            val bookmark = async {
//                bookmarkRepository.getAll().forEach {
//                    bookmarksByUserProvider.save(it)
//                    bookmarksByResourceProvider.save(it)
//                }
//            }
//
//
//            // Likes
//            val likes = async {
//                likeRepository.getAll().forEach {
//                    likesByUserProvider.save(it)
//                    likesByResourceProvider.save(it)
//                }
//            }
//
//
//            // Location
//            val location = async {
//                locationRepository.getAll().forEach {
//                    locationsByUserProvider.save(it)
//                    locationsByZipcodeProvider.save(it)
//                }
//            }
//
//
//            // Post
//            val post = async {
//                postRepository.getAll().forEach { post ->
//                    post.getCategories().categories
//                        .map { postsByCategoryProvider.save(post, it.id) }
//
//                    postsByCategoryProvider.save(post, CategoryV2.ALL)
//
//                    post.getHashTags().tags
//                        .map { postsByHashTagProvider.save(post, it) }
//
//                    postsByZipcodeProvider.save(post)
//
//                    post.zipcode?.let {
//                        val nearbyZipcodes = nearbyZipcodesByZipcodeProvider.getNearbyZipcodesByZipcode(it)
//                        nearbyPostsByZipcodeProvider.save(post, nearbyZipcodes)
//                        nearbyVideoPostsByZipcodeProvider.save(post, nearbyZipcodes)
//                    }
//                }
//            }
//
//
//            // Social
//            val social = async {
//                socialRelationRepository.getAll().forEach {
//                    val fromUser = userV2Provider.getUser(it.fromUserId)
//                        ?: error("Error getting user for fromUserId: ${it.fromUserId}")
//                    val toUser =
//                        userV2Provider.getUser(it.toUserId) ?: error("Error getting user for toUserId: ${it.toUserId}")
//                    followersByUserProvider.save(user = toUser, follower = fromUser)
//                    followingsByUserProvider.save(user = fromUser, following = toUser)
//                }
//            }
//
//
//            // User
//            val user = async {
//                userV2Repository.getAll().forEach {
//                    usersByProfileCategoryProvider.save(it)
//                    usersByProfileTypeProvider.save(it)
//                    usersByZipcodeProvider.save(it)
//                }
//            }
//
//            bookmark.await()
//            likes.await()
//            location.await()
//            post.await()
//            social.await()
//            user.await()
//        }
//    }

}
