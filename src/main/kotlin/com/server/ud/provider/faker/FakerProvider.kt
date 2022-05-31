package com.server.ud.provider.faker

import com.algolia.search.SearchClient
import com.github.javafaker.Faker
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import com.server.common.client.RedisClient
import com.server.common.dto.*
import com.server.common.enums.*
import com.server.common.model.MediaDetailsV2
import com.server.common.model.SingleMediaDetail
import com.server.common.model.convertToString
import com.server.common.model.sampleMedia
import com.server.common.provider.CassandraTableModificationProvider
import com.server.common.provider.SecurityProvider
import com.server.common.provider.UniqueIdProvider
import com.server.common.provider.communication.CommunicationProvider
import com.server.common.utils.DateUtils
import com.server.common.utils.ExperimentManager
import com.server.ud.dao.bookmark.BookmarkRepository
import com.server.ud.dao.like.LikeRepository
import com.server.ud.dao.location.LocationRepository
import com.server.ud.dao.post.PostRepository
import com.server.ud.dao.reply.CommentReplyRepository
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
import com.server.ud.entities.user.getPreferredCategories
import com.server.ud.entities.user.getProfiles
import com.server.ud.enums.*
import com.server.ud.model.sampleHashTagsIds
import com.server.ud.provider.automation.AutomationProvider
import com.server.ud.provider.bookmark.BookmarkProvider
import com.server.ud.provider.bookmark.BookmarksByResourceProvider
import com.server.ud.provider.bookmark.BookmarksByUserProvider
import com.server.ud.provider.cache.UDCacheProvider
import com.server.ud.provider.comment.CommentProvider
import com.server.ud.provider.integration.IntegrationProvider
import com.server.ud.provider.like.LikeProvider
import com.server.ud.provider.like.LikesByResourceProvider
import com.server.ud.provider.like.LikesByUserProvider
import com.server.ud.provider.location.LocationProvider
import com.server.ud.provider.location.LocationsByUserProvider
import com.server.ud.provider.location.LocationsByZipcodeProvider
import com.server.ud.provider.location.NearbyZipcodesByZipcodeProvider
import com.server.ud.provider.notification.DeviceNotificationProvider
import com.server.ud.provider.one_off.OneOffIndexUsersToAlgolia
import com.server.ud.provider.one_off.OneOffSaveChatsV2ToFirestore
import com.server.ud.provider.one_off.OneOffSaveDataToFirestore
import com.server.ud.provider.post.*
import com.server.ud.provider.reply.RepliesByPostProvider
import com.server.ud.provider.reply.ReplyProvider
import com.server.ud.provider.social.FollowersByUserProvider
import com.server.ud.provider.social.FollowingsByUserProvider
import com.server.ud.provider.social.SocialRelationProvider
import com.server.ud.provider.user.UserV2Provider
import com.server.ud.provider.user.UsersByProfileCategoryProvider
import com.server.ud.provider.user.UsersByProfileTypeProvider
import com.server.ud.provider.user.UsersByZipcodeProvider
import com.server.ud.provider.view.ResourceViewsCountByResourceProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
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
    private lateinit var uniqueIdProvider: UniqueIdProvider

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
            val id = uniqueIdProvider.getUniqueIdAfterSaving(ReadableIdPrefix.FKE.name)
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
            userV2Provider.updateUserV2Location(
                UpdateUserV2LocationRequest (
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
            socialRelationProvider.processSocialRelation(
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

    @Autowired
    private lateinit var integrationProvider: IntegrationProvider

    @Autowired
    private lateinit var commentReplyRepository: CommentReplyRepository

    @Autowired
    private lateinit var repliesByPostProvider: RepliesByPostProvider

    @Autowired
    private lateinit var automationProvider: AutomationProvider

    @Autowired
    private lateinit var postsByPostTypeProvider: PostsByPostTypeProvider


    @Autowired
    private lateinit var resourceViewsCountByResourceProvider: ResourceViewsCountByResourceProvider

    @Autowired
    private lateinit var deviceNotificationProvider: DeviceNotificationProvider

    private fun matchViewsCount() {
        val postIdToViewCountMap = mapOf(

            "PST004e6d2f-3ff0-4432-9ca1-4508d0c22118" to 47,
            "PST006D0E18F" to 18596,
            "PST01BC1FAB8" to 17,
            "PST0238B7E02" to 24,
            "PST024202C06" to 20661,
            "PST02BE8DF50" to 16,
            "PST02F6F3A92" to 14176,
            "PST034D18EF4" to 18858,
            "PST04292EA17" to 12,
            "PST0518408EE" to 8920,
            "PST053a1ee7-bcd5-4922-8763-89367a09b6a8" to 22984,
            "PST0554D50AA" to 872,
            "PST057403f8-b743-4b43-80ba-b8d448721ae9" to 8,
            "PST05CA3FF5C" to 10625,
            "PST0606B701F" to 11359,
            "PST06ce395d-0238-42af-b417-b92c945b1298" to 8373,
            "PST0737C4A75" to 438,
            "PST074769de-828d-424f-a246-0bccf46b0f93" to 9458,
            "PST0784bee6-8b9e-4277-9b5c-2d8218b8ac2c" to 31534,
            "PST082D67472" to 18388,
            "PST0850B0E07" to 21,
            "PST0867BCD99" to 8,
            "PST08ac3712-1fdf-46eb-8b88-e63d292b3971" to 6515,
            "PST093168C1B" to 8472,
            "PST093485ae-8e5d-4db1-bf0d-f41224ba473c" to 35,
            "PST093E93DB6" to 7183,
            "PST09C4B6D70" to 23,
            "PST09E6434C7" to 107,
            "PST0A37E1814" to 8407,
            "PST0B7FD8141" to 20733,
            "PST0BF8BF9E7" to 14070,
            "PST0C24BBE8D" to 8,
            "PST0C3869EA5" to 12,
            "PST0CC703182" to 177,
            "PST0D3983185" to 17580,
            "PST0D3D1ABC8" to 14718,
            "PST0D6C57AEC" to 15160,
            "PST0E7370A91" to 8,
            "PST0F3E25545" to 12835,
            "PST0c5b68e8-63d1-4a0a-a05a-a1045f4e2330" to 32670,
            "PST0ce3bc73-2960-43cd-86d6-db61abf13845" to 30215,
            "PST0dfef09c-4cab-408a-afd1-5a6578d104b5" to 8646,
            "PST0e392c1d-3efc-437d-9060-30119cdf073c" to 18302,
            "PST0e50327e-1faf-4c42-9af8-d7f3253eec4b" to 3202,
            "PST0fb4158b-a54a-413f-b408-2cb5f63ee5d0" to 12,
            "PST10A96558D" to 9951,
            "PST11C222A42" to 12390,
            "PST124ABD1D6" to 8866,
            "PST129455f4-2b0d-480f-8ff9-009b933ec41c" to 6303,
            "PST12bdfffd-798f-4561-b8b9-b3f85bda9c89" to 43,
            "PST134b7788-2a11-439f-a3d0-d7641f58e9b3" to 1178,
            "PST13889E28F" to 3323,
            "PST13e4be41-36de-439e-88c5-1278bf54dc5d" to 19,
            "PST15991B8B2" to 16751,
            "PST16050306-828d-4d47-b249-ca89b96f4758" to 12597,
            "PST1615AF556" to 17590,
            "PST164daaf9-7432-453b-9e3a-53b6db5c2a72" to 20053,
            "PST16E52CA78" to 9045,
            "PST173A9EB24" to 8813,
            "PST17DE25B25" to 10209,
            "PST182EDD6AA" to 52,
            "PST18d2f3ee-bef1-49e9-ae70-9408d2fa5f5a" to 5051,
            "PST1920898e-ad4e-43f7-9a06-24175070484e" to 5868,
            "PST19B888BB2" to 7246,
            "PST19a2db28-1db9-44ba-ae41-99d6231bfc53" to 6374,
            "PST1A84C526D" to 4551,
            "PST1AFD5BF30" to 14426,
            "PST1B3EEFEB6" to 9848,
            "PST1BA1377F4" to 5560,
            "PST1BA75714D" to 15132,
            "PST1C0D866FB" to 23615,
            "PST1D15277A9" to 886,
            "PST1DE131983" to 12836,
            "PST1DEB366D4" to 6212,
            "PST1EC6E1669" to 8524,
            "PST1af4999c-081c-4932-9e6c-892d9a735ad8" to 6792,
            "PST1b76a154-5d6f-4a57-bdaf-2afb6de5e999" to 21044,
            "PST1b8c35df-da29-40ce-b511-dec74173adc1" to 20799,
            "PST1baf2b5a-6fec-4c25-88bc-8dd96f2d1ced" to 5697,
            "PST1bd0ca33-98c8-4b02-b2ba-382298bc26d0" to 20672,
            "PST1c2b0f20-f954-4b15-8268-f523497aaa6a" to 29921,
            "PST1cb00bec-d5b3-4871-adc4-5c6b4a2b868d" to 7249,
            "PST1cb4484c-263f-46cf-aef6-5bc4a2bc0b5b" to 11663,
            "PST1e651515-00d9-440d-9b47-143bb9d2a9e0" to 35,
            "PST1f558b67-d039-4458-866a-e3f705261e45" to 4556,
            "PST1f9d4f42-1676-4673-a5d4-9a07baeb22bb" to 29392,
            "PST2023d48c-96ac-4518-a4aa-cca924f2e454" to 12,
            "PST212692EFC" to 3071,
            "PST216A52EF1" to 13846,
            "PST219B4EE3C" to 8,
            "PST228A1C576" to 12,
            "PST229be228-21f3-4512-adab-caa7394457f8" to 26,
            "PST23091d2f-e6ee-4530-a4f1-ea309ff72ef0" to 7943,
            "PST2355A5396" to 8620,
            "PST23AAF7AA5" to 4,
            "PST23fb0a19-ae2e-427b-8744-b9c036835ea6" to 6334,
            "PST24173407-7b32-494f-8f32-b476bace05a9" to 7676,
            "PST244438238" to 28322,
            "PST25151C216" to 8857,
            "PST25361343-1a54-4ac5-ba93-9887781e70ce" to 31291,
            "PST25BF8CF47" to 1987,
            "PST260B01343" to 37,
            "PST26253c11-bed5-49b6-b701-413abf0054dd" to 36424,
            "PST2681f05e-a8c2-474b-b73b-a627ab6a9eba" to 5842,
            "PST27949f6e-5c32-4092-bbd7-62707f6b2e02" to 12702,
            "PST285A9E31F" to 13818,
            "PST285FC93F9" to 887,
            "PST28644049-4d06-4e7a-a9dd-a07318090bff" to 35,
            "PST28FF254DA" to 15297,
            "PST28b0ae71-5bbe-4ecc-865a-b0f7580e367c" to 7406,
            "PST28f5ad9c-b207-4599-bc63-c5e8cfa631a1" to 5499,
            "PST29401A128" to 10415,
            "PST29794D30F" to 7866,
            "PST29DF60CE8" to 13315,
            "PST2B2B94FF6" to 8,
            "PST2C30DDEBF" to 16,
            "PST2D9B19FCE" to 552,
            "PST2DAE68558" to 6455,
            "PST2E48FF6CD" to 3858,
            "PST2EC6B7FFB" to 17745,
            "PST2F5A1105B" to 10446,
            "PST2a256e11-ea2e-4590-803f-036763d8d016" to 6088,
            "PST2c801963-0030-489b-8a8a-2b97e62fa0ad" to 8068,
            "PST2d798fa4-4e30-435f-af89-79a16ba2bcb6" to 514,
            "PST2ea6876c-9768-4d98-95f6-059809dc70be" to 5323,
            "PST2f530248-952d-4def-af71-fc8a030c7bf0" to 8,
            "PST2fc24717-54b7-4b93-a460-b8ee227f5064" to 29165,
            "PST2fd2c651-a33d-491c-a2a7-48f068951856" to 151,
            "PST3094E5BA9" to 905,
            "PST31B72B1F8" to 13382,
            "PST3216FA8E7" to 14694,
            "PST323A9F654" to 21404,
            "PST325B353B0" to 13171,
            "PST3278660E1" to 31846,
            "PST32B6A4031" to 8545,
            "PST32c09cd5-7fa6-4a64-ab33-5ee3ae2b9d67" to 6415,
            "PST34356DE1D" to 26419,
            "PST344ea08e-a69b-4038-b391-eaa8763c2daf" to 9221,
            "PST34F59C2C9" to 28,
            "PST3539BF381" to 8,
            "PST35458ae0-8234-40a5-b3c2-99c6622147d4" to 7973,
            "PST35E020909" to 19008,
            "PST35E407F44" to 9887,
            "PST3662b298-8fcf-4660-9965-52c825f9280e" to 22013,
            "PST36bcb159-6d17-4756-9477-712cfdcae4b1" to 84,
            "PST37183c46-346a-497c-90b7-0453fbc958bb" to 58,
            "PST3748B962E" to 16,
            "PST378e6d39-352d-41b9-a771-20e90e321c1d" to 724,
            "PST38df4810-2afe-48a1-ab51-582b2a578915" to 12,
            "PST3AC00F007" to 9130,
            "PST3C5C3B9FB" to 11079,
            "PST3E3EEBA3E" to 10721,
            "PST3E769DDEB" to 18137,
            "PST3F083BF57" to 438,
            "PST3F0B452CE" to 7255,
            "PST3FDD7F19C" to 1290,
            "PST3a0490e1-7027-4676-bc54-f0918c38072b" to 769,
            "PST3c4f9179-20e3-40ac-ad75-3b3e1e15a1de" to 12109,
            "PST3cb177da-1e81-4005-994e-89c194475dd8" to 11807,
            "PST3d1bb348-b54a-46f2-8b9a-192d55844722" to 16,
            "PST3d97742d-64d4-4499-b99d-c0ab1df6151c" to 25326,
            "PST3db6891d-318c-4e7b-9efc-5019fa3a31b5" to 19,
            "PST3e7e2c51-9f30-4df2-bd34-24d76667ea21" to 33346,
            "PST3ec96d20-3b65-4632-844d-b272f6df398b" to 8,
            "PST3fb2cd00-798a-4176-b22c-29967065d04f" to 6593,
            "PST401842848" to 843,
            "PST4068a52f-2a93-492b-b671-3feb4b84049b" to 16,
            "PST418B294C2" to 8323,
            "PST420ebb00-d91e-42e4-a241-80bb2f489d3e" to 6955,
            "PST424b3f42-8676-416e-bb1f-d749c86a02aa" to 34102,
            "PST42D20F398" to 20059,
            "PST430D7DD37" to 28,
            "PST43B1AADFA" to 11269,
            "PST43b28522-b4d4-4fa4-bab4-1ec962d66b89" to 32,
            "PST4440CB697" to 14357,
            "PST445d14fa-c79d-4b5d-b11d-a108c11b4170" to 5941,
            "PST45A6E6BFA" to 894,
            "PST4687AB31E" to 16,
            "PST46EBB8489" to 23161,
            "PST473893322" to 345,
            "PST49cc9bd4-ceb0-4b01-b281-532072deb051" to 24,
            "PST49fef422-d580-4120-9c85-64c37ce58422" to 26,
            "PST4A6388CB5" to 752,
            "PST4A72ED56E" to 9181,
            "PST4B6C1A87B" to 13505,
            "PST4BA171E5D" to 3536,
            "PST4CB416245" to 52,
            "PST4CF62082E" to 1943,
            "PST4DB74545E" to 27393,
            "PST4F0E266B9" to 11479,
            "PST4F1B5D4DE" to 14128,
            "PST4a684d7b-315d-4b11-881d-b5352cf935ff" to 46,
            "PST4c329f6f-a5e4-4a95-9e69-383e7fdabaaf" to 28562,
            "PST4ec0d78a-afbc-47f3-b752-60333be093ba" to 7104,
            "PST4f0a8330-f0f2-4d0e-a475-0390a8e71970" to 32597,
            "PST50ba111f-2e91-4127-9999-97cb51975069" to 29748,
            "PST52DBF9762" to 8051,
            "PST534B724CD" to 925,
            "PST536C9791A" to 58,
            "PST539CF6ABF" to 10079,
            "PST53a00cdb-8918-472c-9ec3-a2c846168868" to 26525,
            "PST5406A820F" to 5636,
            "PST545289F6D" to 9,
            "PST55b3d2a3-cc47-4b48-8c98-1997b4657936" to 21,
            "PST56e96841-9f69-474a-b11c-dc49fadaca66" to 5783,
            "PST5722f95e-3820-4586-8a54-4ec8296e6f86" to 772,
            "PST57810DDAC" to 7867,
            "PST57C0B4EFF" to 16,
            "PST58ad96d3-0e69-4d15-918e-029ebac01afd" to 5684,
            "PST594b90c8-1b85-4865-a87b-ff005e64a4c7" to 8418,
            "PST596a77c5-bb3d-415d-8b0d-10e4c4f34bb4" to 6291,
            "PST596b12f6-d1d9-4d7d-a3e6-bfe3edc4e67f" to 31208,
            "PST5977c36a-699c-4f9a-a6fb-47f75ade1eae" to 32993,
            "PST59AB680EB" to 6746,
            "PST59c00c5d-7322-4b64-86fa-98220179057f" to 4020,
            "PST5A40B2A96" to 885,
            "PST5A534B922" to 3133,
            "PST5ACA69421" to 337,
            "PST5B5B27BF4" to 16,
            "PST5C0CBD706" to 14704,
            "PST5C399DB8C" to 1923,
            "PST5C433FA37" to 11888,
            "PST5E35A54E6" to 18182,
            "PST5F621DDC7" to 15115,
            "PST5aa2e154-dfb9-486f-a64f-b8c9f1a8e505" to 9724,
            "PST5aaaafb0-4ed6-41d9-970b-c616bd6e7ca4" to 12268,
            "PST5b9e4611-9331-416a-b1d9-8b1282125d84" to 7423,
            "PST5c23247c-e930-450b-9fd7-d0ec1bcaf0ca" to 39,
            "PST5db94238-063d-45dc-b633-31df92365dc1" to 1889,
            "PST5dd65174-13c2-43d5-b6b5-82558c10c4bc" to 33721,
            "PST5ea47780-da34-4ebd-a4d8-3c5f6198d5b2" to 4,
            "PST5fae5733-9749-45da-b2ac-05dc638d8449" to 5413,
            "PST601bd6d4-1be1-41cc-8bae-797439718e4a" to 20887,
            "PST6021e860-d562-4fab-a60b-ee6d67bd49c0" to 3460,
            "PST6022597a-f248-4c84-b3b2-2dd6ca007d42" to 36,
            "PST603743152" to 21130,
            "PST606297da-96d0-4b88-b301-a37b26e0fce8" to 9279,
            "PST6089AACD9" to 13272,
            "PST609eb052-3957-4c4a-9247-4552205e2900" to 6750,
            "PST60c4edcc-7fdd-40e5-9ae4-011c7b8dc40b" to 787,
            "PST6139a205-7f44-485d-9242-899cb41024bc" to 342,
            "PST61e320cc-b96b-47f6-ae32-5e81b6b2befc" to 8178,
            "PST626f7dfe-c16c-4465-9bd5-df1df7fd01ea" to 17382,
            "PST62B87218E" to 729,
            "PST62ee495d-2b10-4929-b7b8-47d96785cf70" to 13592,
            "PST631A1414F" to 1535,
            "PST63FA8971E" to 16,
            "PST6410E3D12" to 8773,
            "PST6484163f-095e-457d-80be-3150a9349c77" to 23252,
            "PST6568C279F" to 8648,
            "PST65B749005" to 13362,
            "PST6678d123-c502-4e46-98b1-b0075f884bf4" to 9483,
            "PST66B44A5AF" to 15193,
            "PST68545e6e-0174-4d74-88ed-aa61255bf7cb" to 31534,
            "PST69B2FC433" to 264,
            "PST69dd01b0-c892-4a6f-988a-588cb8feb31e" to 12,
            "PST6B3267ADF" to 9707,
            "PST6ac02d36-65cc-43e1-aebc-1ed621e64fc0" to 4717,
            "PST6bec9430-8ed5-41b6-95db-5bd81bf58a03" to 11549,
            "PST6c2b43de-bbdf-412e-b46f-030ca5126a10" to 4,
            "PST6d2193b9-4111-4d82-8442-5d7660305186" to 9475,
            "PST6d676d2b-23a0-4cb8-9773-b11d7ef7ac26" to 8,
            "PST6d682cd1-aedc-4889-8156-96104be7a546" to 11891,
            "PST6df8d6ad-f83b-4881-964b-c4c266d098aa" to 4970,
            "PST6e0c5ac3-1212-47b7-8cfd-d3c582d42b25" to 35,
            "PST6e36ca99-639d-4faf-b2b4-9edee7a2c2cd" to 130,
            "PST6ea09c1c-c678-40c8-a864-3d358c393773" to 6535,
            "PST6f8de3fc-75dc-4e88-8de2-c76e3a49e92c" to 9103,
            "PST7020527e-6368-46fd-9265-3940eb5cc004" to 9144,
            "PST7038285d-f41a-4fdf-9f16-8fdeee42fda3" to 18829,
            "PST7057a239-e278-4f69-873e-c129df1958f5" to 4,
            "PST7115B99A3" to 6514,
            "PST7128E60D7" to 2749,
            "PST716d9f62-2436-4e5d-8014-da9c2e46163c" to 8662,
            "PST71B898572" to 12616,
            "PST725321FF0" to 202,
            "PST72e3db6f-d586-4674-88e7-1c32d1a78a12" to 12,
            "PST734f79d2-5020-42cf-9b28-1f0e0f66918f" to 8,
            "PST73EFB4241" to 28,
            "PST73bb2dd3-16f8-401e-a6e1-926b210e3b5c" to 6864,
            "PST74BD8E2B5" to 9545,
            "PST750d859f-ec96-488a-9ce6-1c1deaf8fc2c" to 31728,
            "PST755a69c6-a184-457d-be89-54f26e95f566" to 7172,
            "PST75725871C" to 9221,
            "PST75767B76E" to 21,
            "PST75e3588a-3aef-4eea-9c97-294b452c2a1f" to 32115,
            "PST760C0C6CC" to 52,
            "PST766569aa-7531-4bc6-b44e-0abe67b85ae5" to 21,
            "PST769c2ace-395c-497a-9304-ca5408943f10" to 2561,
            "PST76a93bc8-9d72-467b-ae26-a16d18fb3fdf" to 6,
            "PST7706A7215" to 9129,
            "PST775af38e-2323-4cb9-b336-b747c6c91639" to 11434,
            "PST787f6221-0190-4170-8cf6-8789f478eb70" to 12,
            "PST78a02be2-9c0a-47c6-8b5c-2a58b25e0066" to 16,
            "PST79153fe4-b643-47e8-98c4-96888d31598c" to 285,
            "PST79e13a9d-26f0-41ec-927b-ce37f38687f6" to 8,
            "PST79e7bb68-e612-416e-aff2-ef120b42fd4c" to 22,
            "PST7B2D61F6F" to 13804,
            "PST7C508EE71" to 381,
            "PST7D77B04A7" to 581,
            "PST7D816D0FF" to 14309,
            "PST7DA0438E4" to 3668,
            "PST7bcb372e-709b-42ba-8c40-1626653dd17a" to 3579,
            "PST7d1f93e9-181c-4a9e-84fd-edb29d48bce4" to 8112,
            "PST7d2a032f-3596-4b68-9ab4-1d3db92538c8" to 62,
            "PST7eb3e5b2-5ce8-4b01-9fbf-9afe5ce9a7b3" to 20322,
            "PST80adb10f-dda2-42d1-beea-a05fb9773bbd" to 8,
            "PST811F45860" to 12958,
            "PST819a55d0-584a-42e4-ac79-24ac704c82c3" to 20317,
            "PST81B3EA888" to 56,
            "PST81F2FA09B" to 10999,
            "PST81b3931a-8e9a-4228-b649-db04f1aa989e" to 2956,
            "PST81e1a26c-83c4-4265-becc-86630622f923" to 1524,
            "PST8256D4683" to 1781,
            "PST82A4EA9A9" to 16,
            "PST82C535099" to 15881,
            "PST82c7aa0b-3850-4cc2-83cf-e80ff6783d39" to 483,
            "PST832bf8c6-b361-4674-ac6a-8881dd38dd2c" to 3787,
            "PST8384E9F41" to 16,
            "PST83B958444" to 2477,
            "PST83BAAF74D" to 11432,
            "PST83D7C7CF2" to 3389,
            "PST84611b45-e64f-4579-9b0c-dd03b10b493c" to 8,
            "PST85457261-0ffb-43dc-8215-8103c6bbf0e8" to 6462,
            "PST85e9148d-45a0-4719-bfb4-df2488bd2017" to 5859,
            "PST860D17F11" to 922,
            "PST86785f6e-1eaa-47fa-a565-f570861521ba" to 8,
            "PST86b6937e-75b7-42ba-aa3d-a06a986bcf30" to 5582,
            "PST8700BF765" to 8220,
            "PST882a6b13-588a-4987-b264-8ea1d16693e8" to 9252,
            "PST884da74d-c97b-4d46-b9d2-4a4b663277cd" to 25664,
            "PST8911E166C" to 12410,
            "PST893EC693A" to 62,
            "PST8972fbf4-9e01-4b82-a4ce-4f2f90466211" to 20,
            "PST89EF35704" to 10927,
            "PST89acb138-573f-4bd9-8291-47a42dd23b00" to 262,
            "PST89d2b668-cc8f-4f7a-b6f1-51704d853e51" to 13192,
            "PST8A366697B" to 12673,
            "PST8A422CFF3" to 13211,
            "PST8A6D2B92C" to 480,
            "PST8AD34709C" to 11326,
            "PST8AFA3A0F4" to 8,
            "PST8B062AE2A" to 15126,
            "PST8BA40782C" to 8237,
            "PST8CA5B513D" to 4,
            "PST8CD580534" to 15508,
            "PST8E4BBB53E" to 26,
            "PST8E715583D" to 8,
            "PST8E7FBAF7D" to 183,
            "PST8EF166803" to 489,
            "PST8a472e20-5eb5-4bd6-9bba-f06a26571e44" to 9636,
            "PST8a81d74c-8ecc-42a0-9413-0c73fe34a47c" to 5422,
            "PST8ac64f7c-2926-4612-b427-1d2db1f974be" to 5431,
            "PST8dd89050-b12c-4d36-800a-4ea8b4fbdd39" to 34657,
            "PST8debb55f-822f-4ece-981f-079e9e72fbdf" to 10789,
            "PST9048019d-de24-4c91-a36b-d5542a684351" to 64,
            "PST905b84c1-fcb8-40cf-864b-79110f4cba16" to 537,
            "PST907A0EB73" to 34573,
            "PST91C4BAD12" to 4,
            "PST91a1cfcb-8116-4ea5-a668-6f0256054722" to 768,
            "PST9339FC588" to 3674,
            "PST934196370" to 36,
            "PST934c2701-7a6d-401f-ad6c-9458b33f88b8" to 13600,
            "PST93953973-5a02-4fb3-aee7-eacc1bf9677e" to 4067,
            "PST939F2AE6B" to 8201,
            "PST94217ab6-e666-4619-8029-3acb1c26db14" to 36,
            "PST94480d39-eeb2-43ac-8615-18e23ad1c738" to 3449,
            "PST94544155-1cea-460f-9d2c-035e640a4a12" to 33478,
            "PST947d7176-459a-4b26-9f33-cf364c6b7b21" to 7074,
            "PST94C9B53F6" to 935,
            "PST94fdd6a5-1349-4909-96ae-df552f0d6897" to 14303,
            "PST95695995-bb50-4f0e-be7a-0f9f3e9471bf" to 4,
            "PST9611E5101" to 21462,
            "PST96A616352" to 13258,
            "PST97072B2DD" to 190,
            "PST9709e447-c514-4fe7-b440-03c9ab15b8d5" to 25,
            "PST9760e60d-842f-4f14-8828-0a80923d6f01" to 32,
            "PST9783a9b6-c06b-4b8a-b1b5-3a707c3936b2" to 32791,
            "PST9784d773-4fab-48ee-a822-0a7b4423c1f2" to 767,
            "PST9785b581-f179-4040-841f-40fb345d7aae" to 542,
            "PST97B502EA4" to 8692,
            "PST98DE0E654" to 8496,
            "PST98F8D8E4E" to 12462,
            "PST98d0b0d3-30c6-48f0-b318-b5febbd0c00f" to 8,
            "PST990C629B0" to 19629,
            "PST99BE8283A" to 712,
            "PST99BEA1B08" to 332,
            "PST99E3491EB" to 8223,
            "PST99b72c21-ec8d-4183-b966-9fe25dc2731b" to 31883,
            "PST99e11e63-9c0a-4256-9efb-77b544ed849e" to 36498,
            "PST9A0DA27F7" to 8888,
            "PST9B484B22A" to 9559,
            "PST9C8C9B6F1" to 8980,
            "PST9CB3B3FDE" to 18517,
            "PST9CF712651" to 14648,
            "PST9D04B59B2" to 20657,
            "PST9D220A55F" to 1325,
            "PST9DC1326F9" to 8024,
            "PST9DE64313F" to 11460,
            "PST9E08C0D6A" to 7257,
            "PST9F54C7A31" to 7710,
            "PST9FA857F6C" to 6213,
            "PST9b31273f-1369-47c7-9000-3d9dc96b71f8" to 33857,
            "PST9b58cf08-6718-4b4f-9079-dee4d2d6b3db" to 19836,
            "PST9c54b242-f349-48e5-83ba-fcfccbad2a23" to 11215,
            "PST9d01bbb4-56af-4163-aeae-cdd5328bab19" to 29935,
            "PST9d6c838d-94a7-4b16-a1c0-5144a7117c8b" to 8,
            "PST9de507aa-9a30-4edf-9b2e-ec1c88f12cdf" to 147,
            "PST9f21bb34-a6b2-4843-839c-e17695a9075f" to 33170,
            "PST9f2f20a7-ef02-4e93-864e-0bca2b832057" to 26,
            "PSTA0550C671" to 8293,
            "PSTA12414BEF" to 3415,
            "PSTA497E4EAB" to 23432,
            "PSTA4DF37B83" to 705,
            "PSTA6CC1E3F8" to 22661,
            "PSTA7636F55D" to 17323,
            "PSTA88BD317C" to 495,
            "PSTA91E4F43E" to 7633,
            "PSTA95DD0FAD" to 16809,
            "PSTA9857B06F" to 7151,
            "PSTA9F03F45E" to 17176,
            "PSTAAAF0DCAC" to 1914,
            "PSTAB6F14791" to 9160,
            "PSTACCCE202F" to 13,
            "PSTADDA788C4" to 6078,
            "PSTAE2D38BE0" to 7739,
            "PSTAE9CD1C20" to 258,
            "PSTB0149369E" to 20409,
            "PSTB0E25E9EE" to 477,
            "PSTB11DA27C8" to 3080,
            "PSTB3B548ACF" to 16,
            "PSTB50E0CBAA" to 29,
            "PSTB6CE70D11" to 30412,
            "PSTB781A2573" to 8785,
            "PSTB87A3C161" to 9275,
            "PSTB936387D3" to 12535,
            "PSTB9A2F69F6" to 125,
            "PSTBBA3CB7F6" to 40,
            "PSTBC9E3498E" to 18,
            "PSTBE4679260" to 8451,
            "PSTBF508B1E1" to 5095,
            "PSTC0C35A784" to 888,
            "PSTC17314EDC" to 2821,
            "PSTC271E7998" to 5850,
            "PSTC2BA36E33" to 20696,
            "PSTC3545C261" to 19525,
            "PSTC4C0FE4B9" to 148,
            "PSTC575EDD4C" to 21353,
            "PSTC586E6D7B" to 56,
            "PSTC72FE01FD" to 2085,
            "PSTC7F7A241B" to 11209,
            "PSTC8C4B8917" to 74,
            "PSTC8DD3024B" to 22645,
            "PSTC9BB6E305" to 14048,
            "PSTC9F08D07B" to 16194,
            "PSTCB11C3B91" to 30707,
            "PSTCE937E289" to 4086,
            "PSTD0E931679" to 9537,
            "PSTD1C17BA64" to 6023,
            "PSTD2FD5AE35" to 4,
            "PSTD74ACD1A3" to 16,
            "PSTD82A2B3B1" to 18503,
            "PSTDAA169E53" to 5,
            "PSTDAE27D46A" to 23222,
            "PSTDC214C9AD" to 12141,
            "PSTDDFB81F4F" to 8924,
            "PSTDECC563A2" to 8021,
            "PSTDF318584F" to 23251,
            "PSTDFBD6A933" to 2645,
            "PSTE1D45AC89" to 10713,
            "PSTE1FCFB7F1" to 8356,
            "PSTE33E4A3A2" to 8647,
            "PSTE345785A1" to 7635,
            "PSTE36242989" to 23579,
            "PSTE3660F30B" to 8,
            "PSTE416B83E4" to 3218,
            "PSTE45839FC0" to 14,
            "PSTE4B895945" to 3749,
            "PSTE4C453ECB" to 14604,
            "PSTE53DC7CC6" to 14712,
            "PSTE552A4BF2" to 57,
            "PSTE6210DB24" to 7347,
            "PSTE6D1D73D9" to 182,
            "PSTEC2524B45" to 32,
            "PSTECE682C6A" to 16,
            "PSTEF5A73E71" to 5837,
            "PSTEFABC0E3E" to 57,
            "PSTF09BE65EF" to 19083,
            "PSTF0A58B064" to 8356,
            "PSTF1288B40B" to 8802,
            "PSTF3ACA224A" to 1348,
            "PSTF3DFB8F98" to 11769,
            "PSTF634F6A4C" to 34,
            "PSTF73C46C36" to 16,
            "PSTF82FDDD8D" to 7346,
            "PSTF8D46543E" to 19,
            "PSTF92EB445A" to 12764,
            "PSTFA1C48FB6" to 23209,
            "PSTFA3FA93B6" to 3178,
            "PSTFA45A8030" to 9756,
            "PSTFEE865031" to 6289,
            "PSTFF1A3A289" to 27,
            "PSTFF4B7988A" to 65,
            "PSTa13f7ca5-8336-4b9b-aaef-46240adeae70" to 65,
            "PSTa2b1a917-dd9e-4e8e-a0ce-058488b73ef3" to 12357,
            "PSTa37320a5-9520-4fb8-a15f-4cdcc872d91b" to 4420,
            "PSTa3c69fa6-e443-4f89-9b58-2d4305ed7384" to 33200,
            "PSTa3e23226-403b-474f-b61c-06c1449de935" to 7308,
            "PSTa43df05f-f4d7-4f0b-9d87-2d430b186ef8" to 8,
            "PSTa708013c-d52f-4a80-ba9f-15a7688ea793" to 41,
            "PSTa744dfc1-b9de-41e2-9bf5-14b7b009a848" to 6901,
            "PSTa76e6308-57b9-4ecb-9743-1d8d30afc8f3" to 769,
            "PSTa8667149-05de-4bbd-9ce1-650b01f6ea14" to 21,
            "PSTa86cee72-b714-4142-9640-1708aa59a4d0" to 31934,
            "PSTa92e9233-0b13-4510-a27b-f8e4da8dc8f4" to 769,
            "PSTaa1f1f17-d726-426f-8485-5244afdd2fea" to 11045,
            "PSTaa2262ef-3799-4177-9cc3-ad625906f40f" to 16073,
            "PSTaa962549-c163-4fd5-a8b7-7fb1dc4e7a76" to 808,
            "PSTab484c46-e6e0-41a7-858a-2e8aad512b65" to 1774,
            "PSTab880b2f-6dc9-475c-a632-206407986593" to 331,
            "PSTabad2449-5d4c-4c58-b462-8d4adc835367" to 4092,
            "PSTac7834e0-4df1-4c21-a5e6-958f8ee803bd" to 9814,
            "PSTade2125a-5f27-490f-b85c-8a6032e26534" to 3660,
            "PSTaf03067f-d404-4a7a-b8f8-a1b8a401b339" to 8264,
            "PSTaf77604c-da95-4e94-b080-ab2d81b770fc" to 29299,
            "PSTaf90d631-2b51-4c63-af27-72dd4dcb0ad0" to 306,
            "PSTafaae585-8073-4437-9da1-08fcd1fb4fc3" to 4,
            "PSTb0e022f6-862b-40a5-9341-d4f0524b2128" to 3595,
            "PSTb1d68bac-0ff9-46e2-9c45-e66f6796d94d" to 8853,
            "PSTb2fa61d4-4fc9-40ca-94e2-947a476a076d" to 8778,
            "PSTb333fe05-860b-47a1-90a8-ad554ebb3100" to 32,
            "PSTb3664b0a-0145-4ad5-8668-896286fa0154" to 26,
            "PSTb3e8d2f9-aa13-4f0a-82c7-65d1ce1fd1a1" to 28081,
            "PSTb4c73e44-3ad4-4137-9533-835c40247cfa" to 735,
            "PSTb4ecd6be-2228-4c6c-92b4-8537f3281ad0" to 8,
            "PSTb5d15f43-ac51-4f0c-ae28-f7ad6de7d25f" to 7501,
            "PSTb62e653d-a7b1-461a-8b7c-54b589e21c3e" to 779,
            "PSTb7d10b3f-65e6-410d-80ae-2872b22abd89" to 4,
            "PSTb912a3da-cd60-4238-aaf0-e9b322c248be" to 5760,
            "PSTb94bab78-a72c-48cb-8a6c-2a8bcbe5a88c" to 334,
            "PSTb9553c7d-5dc1-4e14-8719-da9ad7d466c7" to 64,
            "PSTb99be657-506d-456f-8922-bc5cece30067" to 26,
            "PSTba34cf37-d7a2-4aff-a5a7-2ec31350b7bc" to 11595,
            "PSTbb5f213f-167a-4d1d-b1d9-6548a944521d" to 20915,
            "PSTbbcb7c5a-5826-4705-afc8-b23aa2695ea5" to 8,
            "PSTbc79c5c4-b3ff-43c5-accf-abae55ad3ce2" to 33439,
            "PSTbd2c93b8-4817-4c67-8532-e4b25d4f84ff" to 767,
            "PSTc063fe9e-86dc-4a8e-a3a5-d3e6339a0b2c" to 350,
            "PSTc08e14a3-8fff-42f0-b98c-066bc9a43331" to 25,
            "PSTc12cb34b-6c4b-4c5a-85ea-38692cbbab4d" to 230,
            "PSTc22597e5-d280-4121-b0b2-4a10959afb37" to 19397,
            "PSTc41bf789-d3a4-4987-a564-1327ddad2eaa" to 4827,
            "PSTc4ff542f-2309-44dc-87cd-cf348f8c2f48" to 771,
            "PSTc6282d1f-98af-41d8-8069-44a5274e1351" to 117,
            "PSTc62c2609-39a5-4c4e-b868-30298dfc1ba0" to 117,
            "PSTc63a22b0-f89a-46ec-97fa-1409a9433b1c" to 28,
            "PSTc6e2d07a-3338-4604-bef3-091d25c13889" to 14680,
            "PSTc7cd445d-d427-4605-8354-1c78e3407926" to 66,
            "PSTc8872636-68e7-44c7-944e-e97f9c17837a" to 37194,
            "PSTcbab4ca9-b369-40ec-a08c-d7dda46d8237" to 36,
            "PSTcbb026bd-695e-4b4a-ad6f-15c981331a3a" to 423,
            "PSTcf415782-be12-4ad6-a3fb-de5477deeb7c" to 16,
            "PSTd11a158e-7d38-48b6-b709-499ad5b604a2" to 33183,
            "PSTd1b1286f-d355-4ead-8fe0-711f0dc5d28c" to 4295,
            "PSTd1c1b505-1abd-469b-9e1f-6ce07b9da5d3" to 11126,
            "PSTd1ccf606-b915-4f3b-a2f0-328974ece4fc" to 24381,
            "PSTd2c59c07-d714-499d-ae05-ca8714c3b304" to 10865,
            "PSTd2f850d2-493a-4987-bb9a-92ef6d1613c9" to 6056,
            "PSTd31d55b5-95f3-4e7f-97fe-669d05bd30e1" to 774,
            "PSTd5e17156-7b4e-4158-b180-a67e0d0c73ee" to 18,
            "PSTd610b9e0-b419-4dae-832d-5d68a8fa140e" to 26,
            "PSTd72aef67-3371-4907-93cd-b8b585ddc8e1" to 53,
            "PSTd9504b40-4a89-45c7-9918-c3a9ac9ea356" to 5612,
            "PSTd99013a6-94aa-4511-9867-f8cf5a8288da" to 6,
            "PSTda3d9f21-0ad0-4179-8cd7-5df3cc436457" to 15667,
            "PSTda692c82-ea99-44b9-83a2-61a2565c0e5b" to 5055,
            "PSTda6b53af-c8bb-4ae5-b833-e95795a6f0f2" to 3752,
            "PSTdb6fa632-ce15-43f7-b99a-18bf0e0fba19" to 9080,
            "PSTdc8cf329-cbf3-4b98-8e97-e597a7f26450" to 34369,
            "PSTdcc3fcd3-e4c5-491c-bb95-869bd4612622" to 9538,
            "PSTded61c38-ca1b-4525-86cd-20e8e6b330a3" to 5583,
            "PSTdf05d592-8c93-4c99-a5af-20d161743837" to 19363,
            "PSTdff3145f-0e60-40aa-994d-34b3f2943153" to 4630,
            "PSTe3a25013-6114-4bd5-bddf-42ecbe115e2c" to 6109,
            "PSTe471b19a-ab70-4884-8d20-f7beb9c391e1" to 34015,
            "PSTe50aaab6-cd86-4699-839b-c3a2127d8a9c" to 10,
            "PSTe532e8ba-7a51-47bc-867f-032237fe23f4" to 17865,
            "PSTe6e20246-5528-4124-8d0f-c2491914c285" to 22,
            "PSTe7c4efdc-9d6d-4951-8e1d-f58d46b66c02" to 13533,
            "PSTe8c2af6d-6d85-497a-b780-5903b8af5aee" to 198,
            "PSTe940ce67-0560-441f-a6b2-0249fdc76a80" to 15872,
            "PSTeb4f2df1-906b-4718-8a7e-94eb9dcec493" to 5411,
            "PSTeba253d9-5f3c-4e59-b6c1-70fe7bde8dcf" to 2203,
            "PSTebb30ca1-41d4-4b6e-ace7-b24083a1d808" to 20,
            "PSTebfc2ab8-559f-4e38-a2de-e4bc60e2df17" to 16,
            "PSTec0203a2-8e45-4d73-b96a-cafd68107183" to 1641,
            "PSTecb71627-d5ff-450c-8cc2-b9033d7eae50" to 769,
            "PSTeebf4888-41de-4900-8dd7-12e5df2cdec4" to 32292,
            "PSTefd32375-c94f-43b6-bb4e-2a3812f27a0a" to 12402,
            "PSTf1b9afb9-e7d0-420b-a94d-ed99b0fcea06" to 19,
            "PSTf215cf77-cdeb-4566-a1bc-ecf4e3ef3090" to 18812,
            "PSTf32283d5-1375-43ae-96cc-55c1f48203c6" to 3301,
            "PSTf33ac61d-d6f4-49c3-8d0c-48a773e43710" to 33700,
            "PSTf3a22aca-4e86-4f5f-bf9d-ee3d86f927a9" to 92,
            "PSTf4d604d5-691e-49d2-8537-686980d1ce70" to 6194,
            "PSTf51f12d5-395c-4209-b193-a3fd39c7981e" to 9141,
            "PSTf55de689-9be7-4ef4-bf6a-d4df0cb22385" to 4636,
            "PSTf561ee2e-697e-4f08-a6ba-305fd0be6384" to 18,
            "PSTf63a8983-9c38-417a-96e5-3b5fd4c65271" to 23748,
            "PSTf64ab6b7-9a2e-4f39-8d17-fd9cfe084aef" to 17839,
            "PSTf6b8dcfe-bb48-4ae7-b688-573065331e18" to 8,
            "PSTf6ba6256-2b5b-414d-bddd-91038e1b3198" to 2436,
            "PSTf6e18592-c239-4410-8803-5132d4b28059" to 32630,
            "PSTf75d1f0d-57d9-4ada-a401-68b96132cab0" to 4630,
            "PSTf7dbc572-602d-4843-bf54-c6f1f62338de" to 16,
            "PSTf8eb7859-faaf-4eed-9abb-6552f27fee87" to 7145,
            "PSTf93c61a1-5484-4bb4-98bc-0a819c7bdf6d" to 18398,
            "PSTfa0a0190-bcda-4b97-973f-8c78842d1adc" to 4366,
            "PSTfa5cf73c-72ac-4b07-b2eb-6ad3bd98ab04" to 33,
            "PSTfa735f7e-286b-4240-a7da-b8a95b0ca580" to 36442,
            "PSTfa73f276-b9dd-4bf5-bc72-7d55a975a52e" to 12,
            "PSTfb80a9bb-4a0f-495c-8e78-908523dc47cb" to 436,
            "PSTfcdd45bf-9334-40e7-afc0-34f540d90588" to 3430,
            "PSTfd8558ed-5c37-4ce4-97d6-232483dadbe3" to 2323,
            )
        postIdToViewCountMap.map {
            val currentCount = resourceViewsCountByResourceProvider.getResourceViewsCountByResource(it.key)?.viewsCount ?: 0
            val toBeIncrementedCount = if (it.value - currentCount >= 0) it.value - currentCount else 0
            logger.info("ResourceId: ${it.key}, CurrentCount: $currentCount, ToBeIncrementedCount: $toBeIncrementedCount")
            increaseCountTillNumber(it.key, toBeIncrementedCount)
        }

    }

    private fun increaseCountTillNumber(resourceId: String, number: Long) {
        var count = number
        while (count > 0) {
            logger.info("Increasing view count for ResourceId: $resourceId, Count: $count")
            resourceViewsCountByResourceProvider.incrementResourceViewCount(resourceId)
            logger.info("Increased view count for ResourceId: $resourceId, Count: $count")
            count--
        }
    }


    fun doSomething(): Any {

        val users = userV2Provider.getTotalPlatformUsers()
        val experimentName = "ShopExperiment_Enabled"
        val enabled = users.filter {
            ExperimentManager.isExperimentEnabled(
                name = experimentName,
                userId = it.userId
            )
        }

        return "Total users count: ${users.size}. $experimentName experiment Enabled count: ${enabled.size}."

//        GlobalScope.launch {

//            deviceNotificationProvider.notifyLiveEventUsers()
//
//
//            val dataKey1 = "userActivityType"
//            val dataKey2 = "userAggregateActivityType"
//            val dataKey3 = "id"
//            val message = Message
//                .builder()
//                .setNotification(
//                    Notification
//                        .builder()
//                        .setTitle("Hello Post")
//                        .setBody("Post Like")
////                            .setImage(mediaURL)
//                        .build())
//                .putData(dataKey1, "POST_LIKED")
//                .putData(dataKey2, "LIKED")
//                .putData(dataKey3, "PSTB30EAD093")
//                .putData("fallbackUrl", "https://letsunbox.in/live/LIV78F0BBA31")
//                .setToken("emgxocr7TkyErBv-1y9J1w:APA91bG9jX93Fn3su7V-ltBKpBJmYE0saO8BsarHlrclB6atZiTG4MVtW5sWVnC4RdjtASyMRvgWnUvm_Gcuelnj1dq7g2AYrMVCDeYU1hEqsq30qAYR0Zc3wrtqgDsDAPE-bYs1Dqsk")
//                .build()
//
//            val message2 = Message
//                .builder()
//                .setNotification(
//                    Notification
//                        .builder()
//                        .setTitle("Hello Live")
//                        .setBody("Go Live")
////                            .setImage(mediaURL)
//                        .build())
//                .putData("fallbackUrl", "https://letsunbox.in/live/LIV78F0BBA31")
//                .setToken("emgxocr7TkyErBv-1y9J1w:APA91bG9jX93Fn3su7V-ltBKpBJmYE0saO8BsarHlrclB6atZiTG4MVtW5sWVnC4RdjtASyMRvgWnUvm_Gcuelnj1dq7g2AYrMVCDeYU1hEqsq30qAYR0Zc3wrtqgDsDAPE-bYs1Dqsk")
//                .build()
//            val response = FirebaseMessaging.getInstance().send(message)
//            val response2 = FirebaseMessaging.getInstance().send(message2)
//
//            logger.info("response: ${response.toString()}")
//            logger.info("response2: ${response2.toString()}")






//            matchViewsCount()
//        }

//        GlobalScope.launch {
//            userV2Repository.findAll().filterNotNull().forEach {
//                val profileCategories = it.getProfiles().profileTypes.map { it.category }
//                val isContactVisible = profileCategories.contains(ProfileCategory.OWNER).not()
//                userV2Provider.updateContactVisibility(it.userId, isContactVisible)
//            }
//        }

//        GlobalScope.launch {
//            postRepository.findAll().filterNotNull().map {
//                postsByPostTypeProvider.save(it)
//            }
//        }


//        val oldPostDataList = listOf<OldPostData>(
//            OldPostData(
//                zipcodes = setOf(
//                    "562125",
//
//                ),
//                postType = PostType.GENERIC_POST,
//                postId = "PST59c00c5d-7322-4b64-86fa-98220179057f"
//            ),
//        )
//
//        oldPostDataList.map {
//            postProvider.deleteOlderPosts(
//                it.zipcodes,
//                it.postType,
//                it.postId
//            )
//        }


//        userV2Repository.findAll().filterNotNull().filterNot {
//            it.handle.isNullOrBlank()
//        }.map {
//            uniqueIdProvider.saveId(it.handle!!, ReadableIdPrefix.USR.name)
//        }

//        postProvider.updatePostAlgoliaData()

        //postProvider.updateSourceMediaForAll()
//        automationProvider.sendTestSlackMessage()

//        bookmarkProvider.deletePostExpandedData("PSTf2dd423b-3e15-461e-b92f-3c0d485af7dc")
//        commentProvider.deletePostExpandedData("PST5e15e8f0-a3e3-46b8-8658-4c140c1dc65b")

//        cassandraTableModificationProvider.addNewColumns()
//        likeProvider.processAllLikes();

//        commentReplyRepository.findAll().filterNotNull().map {
//            repliesByPostProvider.save(it)
//        }

//        postProvider.deletePost("PST3766ABFFB")

//        replyProvider.deletePostExpandedData("PSTee53124b-314f-4e19-9fb8-2e3b3322e7e2")

//        likeProvider.deletePostExpandedData("PST79e7bb68-e612-416e-aff2-ef120b42fd4c")

//        val data = integrationProvider.getIntegrationAccountInfoByUserId("USREg7UVXtfVeZ4aFQSBwKmvuFa46A3", IntegrationPlatform.INSTAGRAM, "17841412235410524") ?: error("No data")
//
//
//        integrationProvider.scheduleJobs(data)



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

//        return nearbyPostsByZipcodeProvider.getAllPostsTracker("PSTD34E9EB2B")

//        return "Something was done..."
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

data class OldPostData(
    val zipcodes: Set<String>,
    val postType: PostType,
    val postId: String,
)
