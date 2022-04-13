package com.server.ud.provider.faker

import com.algolia.search.SearchClient
import com.github.javafaker.Faker
import com.server.common.client.RedisClient
import com.server.common.dto.AllProfileTypeResponse
import com.server.common.dto.UpdateUserV2LocationRequest
import com.server.common.dto.convertToString
import com.server.common.dto.toProfileTypeResponse
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


    fun doSomething(): Any {


        val oldPostDataList = listOf<OldPostData>(
            OldPostData(
                zipcodes = setOf(
                    "20301",
                    "671314",
                    "686668",
                    "460443",
                    "42",
                    "673027",
                    "680311",
                    "691554",
                    "400703",
                    "141203",
                    "627005",
                    "691001",
                    "282003",
                    "400091",
                    "676104",
                    "679324",
                    "680012",
                    "560026",
                    "94115",
                    "683514",
                    "421001",
                    "679322",
                    "500092",
                    "95014",
                    "682019",
                    "670358",
                    "500034",
                    "686001",
                    "670591",
                    "452002",
                    "679586",
                    "690502",
                    "691505",
                    "465441",
                    "678632",
                    "673017",
                    "682507",
                    "560102",
                    "680006",
                    "20120",
                    "676517",
                    "691015",
                    "382350",
                    "680682",
                    "682030",
                    "380001",
                    "ZZZZZZ",
                    "400052",
                    "670104",
                    "360510",
                    "500025",
                    "680001",
                    "673018",
                    "670003",
                    "520010",
                    "679325",
                    "680302",
                    "390007",
                    "682002",
                    "400014",
                    "680513",
                    "641011",
                    "520004",
                    "691008",
                    "680020",
                    "638009",
                    "160055",
                    "690107",
                    "676101",
                    "400080",
                    "110001",
                    "305009",
                    "676505",
                    "600001",
                    "560001",
                    "685501",
                    "95052",
                    "362001",
                    "671316",
                    "505002",
                    "679321",
                    "51502",
                    "682036",
                    "673121",
                    "110007",
                    "682506",
                    "400070",
                    "560030",
                    "411005",
                    "360311",
                    "679313",
                    "401402",
                    "421102",
                    "679337",
                    "679105",
                    "140307",
                    "695526",
                    "682022",
                    "331024",
                    "676123",
                    "689648",
                    "232101",
                    "686020",
                    "575013",
                    "21202",
                    "400037",
                    "201307",
                    "600028",
                    "560034",
                    "60000-000",
                    "178957",
                    "673004",
                    "680007",
                    "131403",
                    "500044",
                    "688011",
                    "686503",
                    "682301",
                    "400063",
                    "695502",
                    "679513",
                    "20153",
                    "503145",
                    "560002",
                    "695020",
                    "560105",
                    "394375",
                    "670631",
                    "676121",
                    "691511",
                    "689581",
                    "605007",
                    "686103",
                    "683104",
                    "600009",
                    "682304",
                    "676503",
                    "695584",
                    "680308",
                    "370201",
                    "682024",
                    "695011",
                    "140308",
                    "500075",
                    "335523",
                    "570031",
                    "560093",
                    "382042",
                    "679329",
                    "360405",
                    "691019",
                    "800020",
                    "400007",
                    "700012",
                    "302001",
                    "695141",
                    "27",
                    "680010",
                    "302021",
                    "466001",
                    "24012",
                    "688535",
                    "160047",
                    "689602",
                    "110054",
                    "683574",
                    "695311",
                    "680511",
                    "676302",
                    "673631",
                    "673014",
                    "560075",
                    "678001",
                    "679122",
                    "700059",
                    "500001",
                    "683575",
                    "673603",
                    "688521",
                    "476134",
                    "400064",
                    "380002",
                    "679309",
                    "689121",
                    "685605",
                    "500007",
                    "680301",
                    "342001",
                    "673633",
                    "682017",
                    "679561",
                    "94107",
                    "691560",
                    "679335",
                ),
                postType = PostType.GENERIC_POST,
                postId = "PST8E7FBAF7D"
            ),
            OldPostData(
                zipcodes = setOf(
                    "671314",
                    "686668",
                    "460443",
                    "42",
                    "673027",
                    "680311",
                    "691554",
                    "141203",
                    "627005",
                    "691001",
                    "282003",
                    "400091",
                    "676104",
                    "679324",
                    "683514",
                    "500092",
                    "95014",
                    "682019",
                    "670358",
                    "500034",
                    "686001",
                    "670591",
                    "452002",
                    "679586",
                    "690502",
                    "691505",
                    "465441",
                    "673017",
                    "682507",
                    "680006",
                    "676517",
                    "691015",
                    "382350",
                    "680682",
                    "682030",
                    "380001",
                    "ZZZZZZ",
                    "670104",
                    "360510",
                    "500025",
                    "680001",
                    "673018",
                    "670003",
                    "520010",
                    "680302",
                    "390007",
                    "682002",
                    "400014",
                    "680513",
                    "641011",
                    "520004",
                    "691008",
                    "680020",
                    "160055",
                    "690107",
                    "676101",
                    "400080",
                    "110001",
                    "676505",
                    "600001",
                    "560001",
                    "685501",
                    "362001",
                    "671316",
                    "505002",
                    "679321",
                    "51502",
                    "682036",
                    "673121",
                    "110007",
                    "682506",
                    "400070",
                    "560030",
                    "411005",
                    "360311",
                    "679313",
                    "401402",
                    "421102",
                    "679337",
                    "679105",
                    "140307",
                    "695526",
                    "682022",
                    "331024",
                    "676123",
                    "689648",
                    "686020",
                    "575013",
                    "400037",
                    "201307",
                    "600028",
                    "560034",
                    "60000-000",
                    "178957",
                    "673004",
                    "680007",
                    "131403",
                    "500044",
                    "688011",
                    "686503",
                    "682301",
                    "400063",
                    "695502",
                    "679513",
                    "20153",
                    "503145",
                    "560002",
                    "695020",
                    "394375",
                    "670631",
                    "691511",
                    "689581",
                    "605007",
                    "686103",
                    "600009",
                    "682304",
                    "676503",
                    "695584",
                    "680308",
                    "370201",
                    "682024",
                    "695011",
                    "500075",
                    "335523",
                    "570031",
                    "560093",
                    "382042",
                    "679329",
                    "360405",
                    "691019",
                    "800020",
                    "400007",
                    "700012",
                    "302001",
                    "695141",
                    "27",
                    "302021",
                    "466001",
                    "24012",
                    "688535",
                    "689602",
                    "110054",
                    "695311",
                    "680511",
                    "676302",
                    "673631",
                    "673014",
                    "560075",
                    "678001",
                    "679122",
                    "700059",
                    "500001",
                    "683575",
                    "673603",
                    "688521",
                    "476134",
                    "400064",
                    "380002",
                    "689121",
                    "685605",
                    "500007",
                    "680301",
                    "342001",
                    "673633",
                    "682017",
                    "679561",
                    "691560",
                    "679335",
                ),
                postType = PostType.GENERIC_POST,
                postId = "PST34F59C2C9"
            ),
            OldPostData(
                zipcodes = setOf(
                    "562125",
                    "560001",
                    "560100",
                    "560030",
                    "560037",
                    "560034",
                    "570026",
                    "560002",
                    "560105",
                    "560029",
                    "570031",
                    "560093",
                    "636006",
                    "560075",
                ),
                postType = PostType.GENERIC_POST,
                postId = "PST0850B0E07"
            ),
            OldPostData(
                zipcodes = setOf(
                    "562125",
                    "641012",
                    "560026",
                    "571311",
                    "570009",
                    "560102",
                    "635751",
                    "560068",
                    "621703",
                    "638009",
                    "560001",
                    "560100",
                    "560053",
                    "638301",
                    "626103",
                    "625009",
                    "560030",
                    "635001",
                    "560037",
                    "560082",
                    "636001",
                    "560034",
                    "570001",
                    "636010",
                    "573211",
                    "570026",
                    "560002",
                    "560105",
                    "636003",
                    "625016",
                    "560064",
                    "611003",
                    "508208",
                    "560041",
                    "560029",
                    "570031",
                    "609702",
                    "560093",
                    "636006",
                    "560067",
                    "563131",
                    "620012",
                    "637002",
                    "517128",
                    "570016",
                    "560075",
                    "620020",
                    "560070",
                    "637001",
                    "560072",
                ),
                postType = PostType.GENERIC_POST,
                postId = "PST8AFA3A0F4"
            ),
            OldPostData(
                zipcodes = setOf(
                    "600041",
                    "600042",
                    "605602",
                    "600013",
                    "600031",
                    "ZZZZZZ",
                    "600092",
                    "600004",
                    "600001",
                    "600003",
                    "600028",
                    "600053",
                    "602108",
                    "600009",
                    "600012",
                    "600099",
                    "600002",
                    "600081",
                ),
                postType = PostType.GENERIC_POST,
                postId = "PST59c00c5d-7322-4b64-86fa-98220179057f"
            ),
        )

        oldPostDataList.map {
            postProvider.deleteOlderPosts(
                it.zipcodes,
                it.postType,
                it.postId
            )
        }


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
