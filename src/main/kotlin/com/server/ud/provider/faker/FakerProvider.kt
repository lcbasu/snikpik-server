package com.server.ud.provider.faker

import com.algolia.search.SearchClient
import com.github.javafaker.Faker
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

    private fun matchViewsCount() {
        val postIdToViewCountMap = mapOf(
            "PST004e6d2f-3ff0-4432-9ca1-4508d0c22118" to 6,
            "PST006D0E18F" to 2499,
            "PST01BC1FAB8" to 2,
            "PST0238B7E02" to 2,
            "PST024202C06" to 2676,
            "PST02BE8DF50" to 1,
            "PST02F6F3A92" to 1866,
            "PST034D18EF4" to 2570,
            "PST04292EA17" to 1,
            "PST0518408EE" to 1218,
            "PST053a1ee7-bcd5-4922-8763-89367a09b6a8" to 2587,
            "PST0554D50AA" to 15,
            "PST057403f8-b743-4b43-80ba-b8d448721ae9" to 2,
            "PST05CA3FF5C" to 1610,
            "PST0606B701F" to 1629,
            "PST06ce395d-0238-42af-b417-b92c945b1298" to 1499,
            "PST0737C4A75" to 58,
            "PST074769de-828d-424f-a246-0bccf46b0f93" to 1700,
            "PST0784bee6-8b9e-4277-9b5c-2d8218b8ac2c" to 4569,
            "PST082D67472" to 2123,
            "PST0850B0E07" to 4,
            "PST0867BCD99" to 1,
            "PST08ac3712-1fdf-46eb-8b88-e63d292b3971" to 1001,
            "PST093168C1B" to 1165,
            "PST093485ae-8e5d-4db1-bf0d-f41224ba473c" to 5,
            "PST093E93DB6" to 997,
            "PST09C4B6D70" to 1,
            "PST09E6434C7" to 7,
            "PST0A37E1814" to 1151,
            "PST0B7FD8141" to 2502,
            "PST0BF8BF9E7" to 1991,
            "PST0C24BBE8D" to 1,
            "PST0C3869EA5" to 1,
            "PST0CC703182" to 24,
            "PST0D3983185" to 2319,
            "PST0D3D1ABC8" to 2066,
            "PST0D6C57AEC" to 1928,
            "PST0E7370A91" to 1,
            "PST0F3E25545" to 1786,
            "PST0c5b68e8-63d1-4a0a-a05a-a1045f4e2330" to 4348,
            "PST0ce3bc73-2960-43cd-86d6-db61abf13845" to 4322,
            "PST0dfef09c-4cab-408a-afd1-5a6578d104b5" to 1588,
            "PST0e392c1d-3efc-437d-9060-30119cdf073c" to 1700,
            "PST0e50327e-1faf-4c42-9af8-d7f3253eec4b" to 571,
            "PST0fb4158b-a54a-413f-b408-2cb5f63ee5d0" to 2,
            "PST10A96558D" to 1509,
            "PST11C222A42" to 1730,
            "PST124ABD1D6" to 1218,
            "PST129455f4-2b0d-480f-8ff9-009b933ec41c" to 1193,
            "PST12bdfffd-798f-4561-b8b9-b3f85bda9c89" to 5,
            "PST134b7788-2a11-439f-a3d0-d7641f58e9b3" to 196,
            "PST13889E28F" to 423,
            "PST13e4be41-36de-439e-88c5-1278bf54dc5d" to 3,
            "PST15991B8B2" to 2132,
            "PST16050306-828d-4d47-b249-ca89b96f4758" to 2092,
            "PST1615AF556" to 2263,
            "PST164daaf9-7432-453b-9e3a-53b6db5c2a72" to 1613,
            "PST16E52CA78" to 1260,
            "PST173A9EB24" to 1207,
            "PST17DE25B25" to 1298,
            "PST182EDD6AA" to 5,
            "PST18d2f3ee-bef1-49e9-ae70-9408d2fa5f5a" to 769,
            "PST1920898e-ad4e-43f7-9a06-24175070484e" to 930,
            "PST19B888BB2" to 1002,
            "PST19a2db28-1db9-44ba-ae41-99d6231bfc53" to 1082,
            "PST1A84C526D" to 596,
            "PST1AFD5BF30" to 2030,
            "PST1B3EEFEB6" to 1275,
            "PST1BA1377F4" to 639,
            "PST1BA75714D" to 2058,
            "PST1C0D866FB" to 2812,
            "PST1D15277A9" to 16,
            "PST1DE131983" to 1781,
            "PST1DEB366D4" to 920,
            "PST1EC6E1669" to 1352,
            "PST1af4999c-081c-4932-9e6c-892d9a735ad8" to 1169,
            "PST1b76a154-5d6f-4a57-bdaf-2afb6de5e999" to 2273,
            "PST1b8c35df-da29-40ce-b511-dec74173adc1" to 1671,
            "PST1baf2b5a-6fec-4c25-88bc-8dd96f2d1ced" to 1058,
            "PST1bd0ca33-98c8-4b02-b2ba-382298bc26d0" to 2251,
            "PST1c2b0f20-f954-4b15-8268-f523497aaa6a" to 3637,
            "PST1cb00bec-d5b3-4871-adc4-5c6b4a2b868d" to 1181,
            "PST1cb4484c-263f-46cf-aef6-5bc4a2bc0b5b" to 1301,
            "PST1e651515-00d9-440d-9b47-143bb9d2a9e0" to 4,
            "PST1f558b67-d039-4458-866a-e3f705261e45" to 917,
            "PST1f9d4f42-1676-4673-a5d4-9a07baeb22bb" to 4417,
            "PST2023d48c-96ac-4518-a4aa-cca924f2e454" to 2,
            "PST212692EFC" to 370,
            "PST216A52EF1" to 1821,
            "PST219B4EE3C" to 1,
            "PST228A1C576" to 1,
            "PST229be228-21f3-4512-adab-caa7394457f8" to 4,
            "PST23091d2f-e6ee-4530-a4f1-ea309ff72ef0" to 1131,
            "PST2355A5396" to 1163,
            "PST23AAF7AA5" to 1,
            "PST23fb0a19-ae2e-427b-8744-b9c036835ea6" to 893,
            "PST24173407-7b32-494f-8f32-b476bace05a9" to 1395,
            "PST244438238" to 3743,
            "PST25151C216" to 1200,
            "PST25361343-1a54-4ac5-ba93-9887781e70ce" to 4567,
            "PST25BF8CF47" to 222,
            "PST260B01343" to 2,
            "PST26253c11-bed5-49b6-b701-413abf0054dd" to 5133,
            "PST2681f05e-a8c2-474b-b73b-a627ab6a9eba" to 899,
            "PST27949f6e-5c32-4092-bbd7-62707f6b2e02" to 1063,
            "PST285A9E31F" to 1837,
            "PST285FC93F9" to 16,
            "PST28644049-4d06-4e7a-a9dd-a07318090bff" to 6,
            "PST28FF254DA" to 1913,
            "PST28b0ae71-5bbe-4ecc-865a-b0f7580e367c" to 1089,
            "PST28f5ad9c-b207-4599-bc63-c5e8cfa631a1" to 971,
            "PST29401A128" to 1579,
            "PST29794D30F" to 1146,
            "PST29DF60CE8" to 1801,
            "PST2B2B94FF6" to 1,
            "PST2C30DDEBF" to 1,
            "PST2D9B19FCE" to 81,
            "PST2DAE68558" to 827,
            "PST2E48FF6CD" to 516,
            "PST2EC6B7FFB" to 2439,
            "PST2F5A1105B" to 1573,
            "PST2a256e11-ea2e-4590-803f-036763d8d016" to 728,
            "PST2c801963-0030-489b-8a8a-2b97e62fa0ad" to 1514,
            "PST2d798fa4-4e30-435f-af89-79a16ba2bcb6" to 9,
            "PST2ea6876c-9768-4d98-95f6-059809dc70be" to 808,
            "PST2f530248-952d-4def-af71-fc8a030c7bf0" to 2,
            "PST2fc24717-54b7-4b93-a460-b8ee227f5064" to 4319,
            "PST2fd2c651-a33d-491c-a2a7-48f068951856" to 17,
            "PST3094E5BA9" to 15,
            "PST31B72B1F8" to 1838,
            "PST3216FA8E7" to 2065,
            "PST323A9F654" to 2571,
            "PST325B353B0" to 1873,
            "PST3278660E1" to 3968,
            "PST32B6A4031" to 1332,
            "PST32c09cd5-7fa6-4a64-ab33-5ee3ae2b9d67" to 1268,
            "PST34356DE1D" to 3553,
            "PST344ea08e-a69b-4038-b391-eaa8763c2daf" to 1311,
            "PST34F59C2C9" to 5,
            "PST3539BF381" to 1,
            "PST35458ae0-8234-40a5-b3c2-99c6622147d4" to 1435,
            "PST35E020909" to 2409,
            "PST35E407F44" to 1263,
            "PST3662b298-8fcf-4660-9965-52c825f9280e" to 2441,
            "PST36bcb159-6d17-4756-9477-712cfdcae4b1" to 6,
            "PST37183c46-346a-497c-90b7-0453fbc958bb" to 6,
            "PST3748B962E" to 1,
            "PST378e6d39-352d-41b9-a771-20e90e321c1d" to 10,
            "PST38df4810-2afe-48a1-ab51-582b2a578915" to 2,
            "PST3AC00F007" to 1244,
            "PST3C5C3B9FB" to 1602,
            "PST3E3EEBA3E" to 1559,
            "PST3E769DDEB" to 2242,
            "PST3F083BF57" to 25,
            "PST3F0B452CE" to 1199,
            "PST3FDD7F19C" to 163,
            "PST3a0490e1-7027-4676-bc54-f0918c38072b" to 11,
            "PST3c4f9179-20e3-40ac-ad75-3b3e1e15a1de" to 1033,
            "PST3cb177da-1e81-4005-994e-89c194475dd8" to 1987,
            "PST3d1bb348-b54a-46f2-8b9a-192d55844722" to 2,
            "PST3d97742d-64d4-4499-b99d-c0ab1df6151c" to 2083,
            "PST3db6891d-318c-4e7b-9efc-5019fa3a31b5" to 3,
            "PST3e7e2c51-9f30-4df2-bd34-24d76667ea21" to 2694,
            "PST3ec96d20-3b65-4632-844d-b272f6df398b" to 2,
            "PST3fb2cd00-798a-4176-b22c-29967065d04f" to 912,
            "PST401842848" to 110,
            "PST4068a52f-2a93-492b-b671-3feb4b84049b" to 2,
            "PST418B294C2" to 1156,
            "PST420ebb00-d91e-42e4-a241-80bb2f489d3e" to 933,
            "PST424b3f42-8676-416e-bb1f-d749c86a02aa" to 3708,
            "PST42D20F398" to 2426,
            "PST430D7DD37" to 2,
            "PST43B1AADFA" to 1622,
            "PST43b28522-b4d4-4fa4-bab4-1ec962d66b89" to 4,
            "PST4440CB697" to 2003,
            "PST445d14fa-c79d-4b5d-b11d-a108c11b4170" to 1025,
            "PST45A6E6BFA" to 16,
            "PST4687AB31E" to 1,
            "PST46EBB8489" to 2739,
            "PST473893322" to 25,
            "PST49cc9bd4-ceb0-4b01-b281-532072deb051" to 4,
            "PST49fef422-d580-4120-9c85-64c37ce58422" to 4,
            "PST4A6388CB5" to 96,
            "PST4A72ED56E" to 1249,
            "PST4B6C1A87B" to 1742,
            "PST4BA171E5D" to 528,
            "PST4CB416245" to 1,
            "PST4CF62082E" to 153,
            "PST4DB74545E" to 3757,
            "PST4F0E266B9" to 1602,
            "PST4F1B5D4DE" to 2058,
            "PST4a684d7b-315d-4b11-881d-b5352cf935ff" to 1,
            "PST4c329f6f-a5e4-4a95-9e69-383e7fdabaaf" to 4152,
            "PST4ec0d78a-afbc-47f3-b752-60333be093ba" to 1063,
            "PST4f0a8330-f0f2-4d0e-a475-0390a8e71970" to 4571,
            "PST50ba111f-2e91-4127-9999-97cb51975069" to 2323,
            "PST52DBF9762" to 1113,
            "PST534B724CD" to 16,
            "PST536C9791A" to 1,
            "PST539CF6ABF" to 1535,
            "PST53a00cdb-8918-472c-9ec3-a2c846168868" to 2195,
            "PST5406A820F" to 702,
            "PST545289F6D" to 2,
            "PST55b3d2a3-cc47-4b48-8c98-1997b4657936" to 4,
            "PST56e96841-9f69-474a-b11c-dc49fadaca66" to 1000,
            "PST5722f95e-3820-4586-8a54-4ec8296e6f86" to 12,
            "PST57810DDAC" to 1119,
            "PST57C0B4EFF" to 1,
            "PST58ad96d3-0e69-4d15-918e-029ebac01afd" to 995,
            "PST594b90c8-1b85-4865-a87b-ff005e64a4c7" to 1588,
            "PST596a77c5-bb3d-415d-8b0d-10e4c4f34bb4" to 927,
            "PST596b12f6-d1d9-4d7d-a3e6-bfe3edc4e67f" to 3764,
            "PST5977c36a-699c-4f9a-a6fb-47f75ade1eae" to 4826,
            "PST59AB680EB" to 956,
            "PST59c00c5d-7322-4b64-86fa-98220179057f" to 664,
            "PST5A40B2A96" to 15,
            "PST5A534B922" to 417,
            "PST5ACA69421" to 25,
            "PST5B5B27BF4" to 3,
            "PST5C0CBD706" to 1920,
            "PST5C399DB8C" to 144,
            "PST5C433FA37" to 1730,
            "PST5E35A54E6" to 2493,
            "PST5F621DDC7" to 1928,
            "PST5aa2e154-dfb9-486f-a64f-b8c9f1a8e505" to 1779,
            "PST5aaaafb0-4ed6-41d9-970b-c616bd6e7ca4" to 2166,
            "PST5b9e4611-9331-416a-b1d9-8b1282125d84" to 1371,
            "PST5c23247c-e930-450b-9fd7-d0ec1bcaf0ca" to 5,
            "PST5db94238-063d-45dc-b633-31df92365dc1" to 213,
            "PST5dd65174-13c2-43d5-b6b5-82558c10c4bc" to 2869,
            "PST5ea47780-da34-4ebd-a4d8-3c5f6198d5b2" to 1,
            "PST5fae5733-9749-45da-b2ac-05dc638d8449" to 1047,
            "PST601bd6d4-1be1-41cc-8bae-797439718e4a" to 1260,
            "PST6021e860-d562-4fab-a60b-ee6d67bd49c0" to 611,
            "PST6022597a-f248-4c84-b3b2-2dd6ca007d42" to 5,
            "PST603743152" to 2674,
            "PST606297da-96d0-4b88-b301-a37b26e0fce8" to 1518,
            "PST6089AACD9" to 1768,
            "PST609eb052-3957-4c4a-9247-4552205e2900" to 1043,
            "PST60c4edcc-7fdd-40e5-9ae4-011c7b8dc40b" to 13,
            "PST6139a205-7f44-485d-9242-899cb41024bc" to 10,
            "PST61e320cc-b96b-47f6-ae32-5e81b6b2befc" to 1543,
            "PST626f7dfe-c16c-4465-9bd5-df1df7fd01ea" to 2080,
            "PST62B87218E" to 88,
            "PST62ee495d-2b10-4929-b7b8-47d96785cf70" to 2352,
            "PST631A1414F" to 232,
            "PST63FA8971E" to 1,
            "PST6410E3D12" to 1233,
            "PST6484163f-095e-457d-80be-3150a9349c77" to 2382,
            "PST6568C279F" to 1165,
            "PST65B749005" to 1804,
            "PST6678d123-c502-4e46-98b1-b0075f884bf4" to 1742,
            "PST66B44A5AF" to 1869,
            "PST68545e6e-0174-4d74-88ed-aa61255bf7cb" to 2343,
            "PST69B2FC433" to 11,
            "PST69dd01b0-c892-4a6f-988a-588cb8feb31e" to 2,
            "PST6B3267ADF" to 1211,
            "PST6ac02d36-65cc-43e1-aebc-1ed621e64fc0" to 265,
            "PST6bec9430-8ed5-41b6-95db-5bd81bf58a03" to 1487,
            "PST6c2b43de-bbdf-412e-b46f-030ca5126a10" to 1,
            "PST6d2193b9-4111-4d82-8442-5d7660305186" to 1189,
            "PST6d676d2b-23a0-4cb8-9773-b11d7ef7ac26" to 2,
            "PST6d682cd1-aedc-4889-8156-96104be7a546" to 1001,
            "PST6df8d6ad-f83b-4881-964b-c4c266d098aa" to 1039,
            "PST6e0c5ac3-1212-47b7-8cfd-d3c582d42b25" to 4,
            "PST6e36ca99-639d-4faf-b2b4-9edee7a2c2cd" to 24,
            "PST6ea09c1c-c678-40c8-a864-3d358c393773" to 945,
            "PST6f8de3fc-75dc-4e88-8de2-c76e3a49e92c" to 1572,
            "PST7020527e-6368-46fd-9265-3940eb5cc004" to 1499,
            "PST7038285d-f41a-4fdf-9f16-8fdeee42fda3" to 1860,
            "PST7057a239-e278-4f69-873e-c129df1958f5" to 1,
            "PST7115B99A3" to 1090,
            "PST7128E60D7" to 399,
            "PST716d9f62-2436-4e5d-8014-da9c2e46163c" to 1140,
            "PST71B898572" to 1736,
            "PST725321FF0" to 9,
            "PST72e3db6f-d586-4674-88e7-1c32d1a78a12" to 2,
            "PST734f79d2-5020-42cf-9b28-1f0e0f66918f" to 2,
            "PST73EFB4241" to 3,
            "PST73bb2dd3-16f8-401e-a6e1-926b210e3b5c" to 924,
            "PST74BD8E2B5" to 1215,
            "PST750d859f-ec96-488a-9ce6-1c1deaf8fc2c" to 3969,
            "PST755a69c6-a184-457d-be89-54f26e95f566" to 1017,
            "PST75725871C" to 1203,
            "PST75767B76E" to 3,
            "PST75e3588a-3aef-4eea-9c97-294b452c2a1f" to 2820,
            "PST760C0C6CC" to 7,
            "PST766569aa-7531-4bc6-b44e-0abe67b85ae5" to 4,
            "PST769c2ace-395c-497a-9304-ca5408943f10" to 429,
            "PST76a93bc8-9d72-467b-ae26-a16d18fb3fdf" to 1,
            "PST7706A7215" to 1177,
            "PST775af38e-2323-4cb9-b336-b747c6c91639" to 1582,
            "PST787f6221-0190-4170-8cf6-8789f478eb70" to 2,
            "PST78a02be2-9c0a-47c6-8b5c-2a58b25e0066" to 1,
            "PST79153fe4-b643-47e8-98c4-96888d31598c" to 17,
            "PST79e13a9d-26f0-41ec-927b-ce37f38687f6" to 2,
            "PST79e7bb68-e612-416e-aff2-ef120b42fd4c" to 3,
            "PST7B2D61F6F" to 1765,
            "PST7C508EE71" to 58,
            "PST7D77B04A7" to 49,
            "PST7D816D0FF" to 1857,
            "PST7DA0438E4" to 511,
            "PST7bcb372e-709b-42ba-8c40-1626653dd17a" to 588,
            "PST7d1f93e9-181c-4a9e-84fd-edb29d48bce4" to 1081,
            "PST7d2a032f-3596-4b68-9ab4-1d3db92538c8" to 8,
            "PST7eb3e5b2-5ce8-4b01-9fbf-9afe5ce9a7b3" to 1647,
            "PST80adb10f-dda2-42d1-beea-a05fb9773bbd" to 2,
            "PST811F45860" to 1781,
            "PST819a55d0-584a-42e4-ac79-24ac704c82c3" to 2227,
            "PST81B3EA888" to 1,
            "PST81F2FA09B" to 1581,
            "PST81b3931a-8e9a-4228-b649-db04f1aa989e" to 442,
            "PST81e1a26c-83c4-4265-becc-86630622f923" to 252,
            "PST8256D4683" to 266,
            "PST82A4EA9A9" to 1,
            "PST82C535099" to 2296,
            "PST82c7aa0b-3850-4cc2-83cf-e80ff6783d39" to 7,
            "PST832bf8c6-b361-4674-ac6a-8881dd38dd2c" to 619,
            "PST8384E9F41" to 1,
            "PST83B958444" to 332,
            "PST83BAAF74D" to 1689,
            "PST83D7C7CF2" to 370,
            "PST84611b45-e64f-4579-9b0c-dd03b10b493c" to 2,
            "PST85457261-0ffb-43dc-8215-8103c6bbf0e8" to 1295,
            "PST85e9148d-45a0-4719-bfb4-df2488bd2017" to 1066,
            "PST860D17F11" to 16,
            "PST86785f6e-1eaa-47fa-a565-f570861521ba" to 2,
            "PST86b6937e-75b7-42ba-aa3d-a06a986bcf30" to 1029,
            "PST8700BF765" to 1068,
            "PST882a6b13-588a-4987-b264-8ea1d16693e8" to 1699,
            "PST884da74d-c97b-4d46-b9d2-4a4b663277cd" to 3254,
            "PST8911E166C" to 1791,
            "PST893EC693A" to 1,
            "PST8972fbf4-9e01-4b82-a4ce-4f2f90466211" to 2,
            "PST89EF35704" to 1644,
            "PST89acb138-573f-4bd9-8291-47a42dd23b00" to 8,
            "PST89d2b668-cc8f-4f7a-b6f1-51704d853e51" to 2002,
            "PST8A366697B" to 1760,
            "PST8A422CFF3" to 1860,
            "PST8A6D2B92C" to 15,
            "PST8AD34709C" to 1617,
            "PST8AFA3A0F4" to 2,
            "PST8B062AE2A" to 2099,
            "PST8BA40782C" to 1309,
            "PST8CA5B513D" to 1,
            "PST8CD580534" to 2150,
            "PST8E4BBB53E" to 3,
            "PST8E715583D" to 2,
            "PST8E7FBAF7D" to 9,
            "PST8EF166803" to 34,
            "PST8a472e20-5eb5-4bd6-9bba-f06a26571e44" to 1267,
            "PST8a81d74c-8ecc-42a0-9413-0c73fe34a47c" to 1060,
            "PST8ac64f7c-2926-4612-b427-1d2db1f974be" to 994,
            "PST8dd89050-b12c-4d36-800a-4ea8b4fbdd39" to 4996,
            "PST8debb55f-822f-4ece-981f-079e9e72fbdf" to 1251,
            "PST9048019d-de24-4c91-a36b-d5542a684351" to 6,
            "PST905b84c1-fcb8-40cf-864b-79110f4cba16" to 14,
            "PST907A0EB73" to 4349,
            "PST91C4BAD12" to 1,
            "PST91a1cfcb-8116-4ea5-a668-6f0256054722" to 11,
            "PST9339FC588" to 458,
            "PST934196370" to 4,
            "PST934c2701-7a6d-401f-ad6c-9458b33f88b8" to 2349,
            "PST93953973-5a02-4fb3-aee7-eacc1bf9677e" to 606,
            "PST939F2AE6B" to 1135,
            "PST94217ab6-e666-4619-8029-3acb1c26db14" to 5,
            "PST94480d39-eeb2-43ac-8615-18e23ad1c738" to 556,
            "PST94544155-1cea-460f-9d2c-035e640a4a12" to 4679,
            "PST947d7176-459a-4b26-9f33-cf364c6b7b21" to 1253,
            "PST94C9B53F6" to 16,
            "PST94fdd6a5-1349-4909-96ae-df552f0d6897" to 902,
            "PST95695995-bb50-4f0e-be7a-0f9f3e9471bf" to 1,
            "PST9611E5101" to 2689,
            "PST96A616352" to 1987,
            "PST97072B2DD" to 32,
            "PST9709e447-c514-4fe7-b440-03c9ab15b8d5" to 4,
            "PST9760e60d-842f-4f14-8828-0a80923d6f01" to 4,
            "PST9783a9b6-c06b-4b8a-b1b5-3a707c3936b2" to 4782,
            "PST9784d773-4fab-48ee-a822-0a7b4423c1f2" to 11,
            "PST9785b581-f179-4040-841f-40fb345d7aae" to 9,
            "PST97B502EA4" to 1222,
            "PST98DE0E654" to 1169,
            "PST98F8D8E4E" to 1737,
            "PST98d0b0d3-30c6-48f0-b318-b5febbd0c00f" to 2,
            "PST990C629B0" to 2378,
            "PST99BE8283A" to 89,
            "PST99BEA1B08" to 30,
            "PST99E3491EB" to 1133,
            "PST99b72c21-ec8d-4183-b966-9fe25dc2731b" to 4838,
            "PST99e11e63-9c0a-4256-9efb-77b544ed849e" to 3386,
            "PST9A0DA27F7" to 1143,
            "PST9B484B22A" to 1479,
            "PST9C8C9B6F1" to 1235,
            "PST9CB3B3FDE" to 2282,
            "PST9CF712651" to 2021,
            "PST9D04B59B2" to 2742,
            "PST9D220A55F" to 182,
            "PST9DC1326F9" to 1114,
            "PST9DE64313F" to 1647,
            "PST9E08C0D6A" to 1030,
            "PST9F54C7A31" to 1114,
            "PST9FA857F6C" to 821,
            "PST9b31273f-1369-47c7-9000-3d9dc96b71f8" to 2787,
            "PST9b58cf08-6718-4b4f-9079-dee4d2d6b3db" to 2200,
            "PST9c54b242-f349-48e5-83ba-fcfccbad2a23" to 1437,
            "PST9d01bbb4-56af-4163-aeae-cdd5328bab19" to 4265,
            "PST9d6c838d-94a7-4b16-a1c0-5144a7117c8b" to 1,
            "PST9de507aa-9a30-4edf-9b2e-ec1c88f12cdf" to 27,
            "PST9f21bb34-a6b2-4843-839c-e17695a9075f" to 4512,
            "PST9f2f20a7-ef02-4e93-864e-0bca2b832057" to 3,
            "PSTA0550C671" to 1154,
            "PSTA12414BEF" to 486,
            "PSTA497E4EAB" to 2892,
            "PSTA4DF37B83" to 97,
            "PSTA6CC1E3F8" to 2751,
            "PSTA7636F55D" to 2187,
            "PSTA88BD317C" to 63,
            "PSTA91E4F43E" to 1080,
            "PSTA95DD0FAD" to 2201,
            "PSTA9857B06F" to 1061,
            "PSTA9F03F45E" to 2257,
            "PSTAAAF0DCAC" to 145,
            "PSTAB6F14791" to 1416,
            "PSTACCCE202F" to 2,
            "PSTADDA788C4" to 637,
            "PSTAE2D38BE0" to 1092,
            "PSTAE9CD1C20" to 36,
            "PSTB0149369E" to 2516,
            "PSTB0E25E9EE" to 66,
            "PSTB11DA27C8" to 403,
            "PSTB3B548ACF" to 1,
            "PSTB50E0CBAA" to 5,
            "PSTB6CE70D11" to 3747,
            "PSTB781A2573" to 1220,
            "PSTB87A3C161" to 1434,
            "PSTB936387D3" to 1577,
            "PSTB9A2F69F6" to 17,
            "PSTBBA3CB7F6" to 4,
            "PSTBC9E3498E" to 3,
            "PSTBE4679260" to 1188,
            "PSTBF508B1E1" to 637,
            "PSTC0C35A784" to 15,
            "PSTC17314EDC" to 354,
            "PSTC271E7998" to 753,
            "PSTC2BA36E33" to 2740,
            "PSTC3545C261" to 2625,
            "PSTC4C0FE4B9" to 18,
            "PSTC575EDD4C" to 2721,
            "PSTC586E6D7B" to 1,
            "PSTC72FE01FD" to 159,
            "PSTC7F7A241B" to 1596,
            "PSTC8C4B8917" to 9,
            "PSTC8DD3024B" to 2660,
            "PSTC9BB6E305" to 1837,
            "PSTC9F08D07B" to 2107,
            "PSTCB11C3B91" to 3736,
            "PSTCE937E289" to 533,
            "PSTD0E931679" to 1443,
            "PSTD1C17BA64" to 898,
            "PSTD2FD5AE35" to 1,
            "PSTD74ACD1A3" to 1,
            "PSTD82A2B3B1" to 2529,
            "PSTDAA169E53" to 1,
            "PSTDAE27D46A" to 2845,
            "PSTDC214C9AD" to 1567,
            "PSTDDFB81F4F" to 1199,
            "PSTDECC563A2" to 1098,
            "PSTDF318584F" to 2841,
            "PSTDFBD6A933" to 354,
            "PSTE1D45AC89" to 1570,
            "PSTE1FCFB7F1" to 1148,
            "PSTE33E4A3A2" to 1361,
            "PSTE345785A1" to 1088,
            "PSTE36242989" to 2781,
            "PSTE3660F30B" to 1,
            "PSTE416B83E4" to 424,
            "PSTE45839FC0" to 3,
            "PSTE4B895945" to 497,
            "PSTE4C453ECB" to 1781,
            "PSTE53DC7CC6" to 1866,
            "PSTE552A4BF2" to 1,
            "PSTE6210DB24" to 922,
            "PSTE6D1D73D9" to 24,
            "PSTEC2524B45" to 6,
            "PSTECE682C6A" to 1,
            "PSTEF5A73E71" to 874,
            "PSTEFABC0E3E" to 1,
            "PSTF09BE65EF" to 2488,
            "PSTF0A58B064" to 1061,
            "PSTF1288B40B" to 1207,
            "PSTF3ACA224A" to 151,
            "PSTF3DFB8F98" to 1730,
            "PSTF634F6A4C" to 5,
            "PSTF73C46C36" to 1,
            "PSTF82FDDD8D" to 1044,
            "PSTF8D46543E" to 4,
            "PSTF92EB445A" to 1776,
            "PSTFA1C48FB6" to 2730,
            "PSTFA3FA93B6" to 424,
            "PSTFA45A8030" to 1285,
            "PSTFEE865031" to 938,
            "PSTFF1A3A289" to 4,
            "PSTFF4B7988A" to 1,
            "PSTa13f7ca5-8336-4b9b-aaef-46240adeae70" to 4,
            "PSTa2b1a917-dd9e-4e8e-a0ce-058488b73ef3" to 823,
            "PSTa37320a5-9520-4fb8-a15f-4cdcc872d91b" to 696,
            "PSTa3c69fa6-e443-4f89-9b58-2d4305ed7384" to 2632,
            "PSTa3e23226-403b-474f-b61c-06c1449de935" to 1311,
            "PSTa43df05f-f4d7-4f0b-9d87-2d430b186ef8" to 1,
            "PSTa708013c-d52f-4a80-ba9f-15a7688ea793" to 6,
            "PSTa744dfc1-b9de-41e2-9bf5-14b7b009a848" to 1220,
            "PSTa76e6308-57b9-4ecb-9743-1d8d30afc8f3" to 11,
            "PSTa8667149-05de-4bbd-9ce1-650b01f6ea14" to 4,
            "PSTa86cee72-b714-4142-9640-1708aa59a4d0" to 4691,
            "PSTa92e9233-0b13-4510-a27b-f8e4da8dc8f4" to 11,
            "PSTaa1f1f17-d726-426f-8485-5244afdd2fea" to 1384,
            "PSTaa2262ef-3799-4177-9cc3-ad625906f40f" to 997,
            "PSTaa962549-c163-4fd5-a8b7-7fb1dc4e7a76" to 58,
            "PSTab484c46-e6e0-41a7-858a-2e8aad512b65" to 207,
            "PSTab880b2f-6dc9-475c-a632-206407986593" to 5,
            "PSTabad2449-5d4c-4c58-b462-8d4adc835367" to 636,
            "PSTac7834e0-4df1-4c21-a5e6-958f8ee803bd" to 1315,
            "PSTade2125a-5f27-490f-b85c-8a6032e26534" to 642,
            "PSTaf03067f-d404-4a7a-b8f8-a1b8a401b339" to 1173,
            "PSTaf77604c-da95-4e94-b080-ab2d81b770fc" to 4174,
            "PSTaf90d631-2b51-4c63-af27-72dd4dcb0ad0" to 10,
            "PSTafaae585-8073-4437-9da1-08fcd1fb4fc3" to 1,
            "PSTb0e022f6-862b-40a5-9341-d4f0524b2128" to 568,
            "PSTb1d68bac-0ff9-46e2-9c45-e66f6796d94d" to 1624,
            "PSTb2fa61d4-4fc9-40ca-94e2-947a476a076d" to 1586,
            "PSTb333fe05-860b-47a1-90a8-ad554ebb3100" to 4,
            "PSTb3664b0a-0145-4ad5-8668-896286fa0154" to 4,
            "PSTb3e8d2f9-aa13-4f0a-82c7-65d1ce1fd1a1" to 1872,
            "PSTb4c73e44-3ad4-4137-9533-835c40247cfa" to 11,
            "PSTb4ecd6be-2228-4c6c-92b4-8537f3281ad0" to 2,
            "PSTb5d15f43-ac51-4f0c-ae28-f7ad6de7d25f" to 1356,
            "PSTb62e653d-a7b1-461a-8b7c-54b589e21c3e" to 13,
            "PSTb7d10b3f-65e6-410d-80ae-2872b22abd89" to 1,
            "PSTb912a3da-cd60-4238-aaf0-e9b322c248be" to 872,
            "PSTb94bab78-a72c-48cb-8a6c-2a8bcbe5a88c" to 10,
            "PSTb9553c7d-5dc1-4e14-8719-da9ad7d466c7" to 4,
            "PSTb99be657-506d-456f-8922-bc5cece30067" to 2,
            "PSTba34cf37-d7a2-4aff-a5a7-2ec31350b7bc" to 1571,
            "PSTbb5f213f-167a-4d1d-b1d9-6548a944521d" to 1700,
            "PSTbbcb7c5a-5826-4705-afc8-b23aa2695ea5" to 2,
            "PSTbc79c5c4-b3ff-43c5-accf-abae55ad3ce2" to 3411,
            "PSTbd2c93b8-4817-4c67-8532-e4b25d4f84ff" to 11,
            "PSTc063fe9e-86dc-4a8e-a3a5-d3e6339a0b2c" to 10,
            "PSTc08e14a3-8fff-42f0-b98c-066bc9a43331" to 4,
            "PSTc12cb34b-6c4b-4c5a-85ea-38692cbbab4d" to 13,
            "PSTc22597e5-d280-4121-b0b2-4a10959afb37" to 2155,
            "PSTc41bf789-d3a4-4987-a564-1327ddad2eaa" to 750,
            "PSTc4ff542f-2309-44dc-87cd-cf348f8c2f48" to 12,
            "PSTc6282d1f-98af-41d8-8069-44a5274e1351" to 5,
            "PSTc62c2609-39a5-4c4e-b868-30298dfc1ba0" to 1,
            "PSTc63a22b0-f89a-46ec-97fa-1409a9433b1c" to 4,
            "PSTc6e2d07a-3338-4604-bef3-091d25c13889" to 1236,
            "PSTc7cd445d-d427-4605-8354-1c78e3407926" to 4,
            "PSTc8872636-68e7-44c7-944e-e97f9c17837a" to 4865,
            "PSTcbab4ca9-b369-40ec-a08c-d7dda46d8237" to 5,
            "PSTcbb026bd-695e-4b4a-ad6f-15c981331a3a" to 8,
            "PSTcf415782-be12-4ad6-a3fb-de5477deeb7c" to 2,
            "PSTd11a158e-7d38-48b6-b709-499ad5b604a2" to 4744,
            "PSTd1b1286f-d355-4ead-8fe0-711f0dc5d28c" to 690,
            "PSTd1c1b505-1abd-469b-9e1f-6ce07b9da5d3" to 1472,
            "PSTd1ccf606-b915-4f3b-a2f0-328974ece4fc" to 3075,
            "PSTd2c59c07-d714-499d-ae05-ca8714c3b304" to 1367,
            "PSTd2f850d2-493a-4987-bb9a-92ef6d1613c9" to 1155,
            "PSTd31d55b5-95f3-4e7f-97fe-669d05bd30e1" to 12,
            "PSTd5e17156-7b4e-4158-b180-a67e0d0c73ee" to 3,
            "PSTd610b9e0-b419-4dae-832d-5d68a8fa140e" to 3,
            "PSTd72aef67-3371-4907-93cd-b8b585ddc8e1" to 5,
            "PSTd9504b40-4a89-45c7-9918-c3a9ac9ea356" to 983,
            "PSTd99013a6-94aa-4511-9867-f8cf5a8288da" to 1,
            "PSTda3d9f21-0ad0-4179-8cd7-5df3cc436457" to 2083,
            "PSTda692c82-ea99-44b9-83a2-61a2565c0e5b" to 778,
            "PSTda6b53af-c8bb-4ae5-b833-e95795a6f0f2" to 168,
            "PSTdb6fa632-ce15-43f7-b99a-18bf0e0fba19" to 824,
            "PSTdc8cf329-cbf3-4b98-8e97-e597a7f26450" to 3522,
            "PSTdcc3fcd3-e4c5-491c-bb95-869bd4612622" to 1596,
            "PSTded61c38-ca1b-4525-86cd-20e8e6b330a3" to 856,
            "PSTdf05d592-8c93-4c99-a5af-20d161743837" to 1807,
            "PSTdff3145f-0e60-40aa-994d-34b3f2943153" to 730,
            "PSTe3a25013-6114-4bd5-bddf-42ecbe115e2c" to 1079,
            "PSTe471b19a-ab70-4884-8d20-f7beb9c391e1" to 3521,
            "PSTe50aaab6-cd86-4699-839b-c3a2127d8a9c" to 1,
            "PSTe532e8ba-7a51-47bc-867f-032237fe23f4" to 1805,
            "PSTe6e20246-5528-4124-8d0f-c2491914c285" to 3,
            "PSTe7c4efdc-9d6d-4951-8e1d-f58d46b66c02" to 2186,
            "PSTe8c2af6d-6d85-497a-b780-5903b8af5aee" to 6,
            "PSTe940ce67-0560-441f-a6b2-0249fdc76a80" to 2660,
            "PSTeb4f2df1-906b-4718-8a7e-94eb9dcec493" to 831,
            "PSTeba253d9-5f3c-4e59-b6c1-70fe7bde8dcf" to 391,
            "PSTebb30ca1-41d4-4b6e-ace7-b24083a1d808" to 2,
            "PSTebfc2ab8-559f-4e38-a2de-e4bc60e2df17" to 2,
            "PSTec0203a2-8e45-4d73-b96a-cafd68107183" to 256,
            "PSTecb71627-d5ff-450c-8cc2-b9033d7eae50" to 11,
            "PSTeebf4888-41de-4900-8dd7-12e5df2cdec4" to 2593,
            "PSTefd32375-c94f-43b6-bb4e-2a3812f27a0a" to 1020,
            "PSTf1b9afb9-e7d0-420b-a94d-ed99b0fcea06" to 2,
            "PSTf215cf77-cdeb-4566-a1bc-ecf4e3ef3090" to 1585,
            "PSTf32283d5-1375-43ae-96cc-55c1f48203c6" to 549,
            "PSTf33ac61d-d6f4-49c3-8d0c-48a773e43710" to 4806,
            "PSTf3a22aca-4e86-4f5f-bf9d-ee3d86f927a9" to 6,
            "PSTf4d604d5-691e-49d2-8537-686980d1ce70" to 1101,
            "PSTf51f12d5-395c-4209-b193-a3fd39c7981e" to 1652,
            "PSTf55de689-9be7-4ef4-bf6a-d4df0cb22385" to 940,
            "PSTf561ee2e-697e-4f08-a6ba-305fd0be6384" to 3,
            "PSTf63a8983-9c38-417a-96e5-3b5fd4c65271" to 2027,
            "PSTf64ab6b7-9a2e-4f39-8d17-fd9cfe084aef" to 2112,
            "PSTf6b8dcfe-bb48-4ae7-b688-573065331e18" to 2,
            "PSTf6ba6256-2b5b-414d-bddd-91038e1b3198" to 439,
            "PSTf6e18592-c239-4410-8803-5132d4b28059" to 2615,
            "PSTf75d1f0d-57d9-4ada-a401-68b96132cab0" to 721,
            "PSTf7dbc572-602d-4843-bf54-c6f1f62338de" to 2,
            "PSTf8eb7859-faaf-4eed-9abb-6552f27fee87" to 1173,
            "PSTf93c61a1-5484-4bb4-98bc-0a819c7bdf6d" to 2138,
            "PSTfa0a0190-bcda-4b97-973f-8c78842d1adc" to 613,
            "PSTfa5cf73c-72ac-4b07-b2eb-6ad3bd98ab04" to 5,
            "PSTfa735f7e-286b-4240-a7da-b8a95b0ca580" to 3361,
            "PSTfa73f276-b9dd-4bf5-bc72-7d55a975a52e" to 1,
            "PSTfb80a9bb-4a0f-495c-8e78-908523dc47cb" to 6,
            "PSTfcdd45bf-9334-40e7-afc0-34f540d90588" to 562,
            "PSTfd8558ed-5c37-4ce4-97d6-232483dadbe3" to 146,
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

        GlobalScope.launch {
            matchViewsCount()
        }

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

data class OldPostData(
    val zipcodes: Set<String>,
    val postType: PostType,
    val postId: String,
)
