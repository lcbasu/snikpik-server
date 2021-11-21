package com.server.ud.provider.faker

import com.github.javafaker.Faker
import com.server.common.enums.MediaType
import com.server.common.enums.NotificationTokenProvider
import com.server.common.enums.ProfileType
import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.RandomIdProvider
import com.server.common.utils.DateUtils
import com.server.dk.model.MediaDetailsV2
import com.server.dk.model.SingleMediaDetail
import com.server.dk.model.convertToString
import com.server.dk.model.sampleMedia
import com.server.ud.dto.*
import com.server.ud.entities.bookmark.Bookmark
import com.server.ud.entities.comment.Comment
import com.server.ud.entities.like.Like
import com.server.ud.entities.post.Post
import com.server.ud.entities.reply.Reply
import com.server.ud.entities.social.SocialRelation
import com.server.ud.entities.user.UserV2
import com.server.ud.enums.*
import com.server.ud.model.HashTagData
import com.server.ud.model.HashTagsList
import com.server.ud.provider.bookmark.BookmarkProvider
import com.server.ud.provider.comment.CommentProvider
import com.server.ud.provider.like.LikeProvider
import com.server.ud.provider.post.PostProvider
import com.server.ud.provider.reply.ReplyProvider
import com.server.ud.provider.social.SocialRelationProcessingProvider
import com.server.ud.provider.social.SocialRelationProvider
import com.server.ud.provider.user.UserV2Provider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class FakerProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    private val minUsersToFake = 5
    private val maxUsersToFake = 25
    private val maxPostToFake = 25
    private val minPostToFake = 5

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
        val usersV2 = mutableListOf<UserV2>()
        for (i in 1..usersToCreate) {
            val profiles = ProfileType.values().toList().shuffled().take(Random.nextInt(1, ProfileType.values().size))
            val location = sampleLocationRequests.shuffled().first()
            val id = randomIdProvider.getTimeBasedRandomIdFor(ReadableIdPrefix.FKE)
            val userV2 = userV2Provider.saveUserV2(UserV2 (
                userId = id,
                createdAt = DateUtils.getInstantNow(),
                absoluteMobile = "",
                countryCode = "",
                handle = faker.name().username(),
                dp = MediaDetailsV2(listOf(SingleMediaDetail(
                    mediaUrl = "https://i.pravatar.cc/150?u=${id}",
                    mediaType = MediaType.IMAGE,
                ))).convertToString(),
                uid = id,
                anonymous = false,
                verified = Random.nextInt(1, 100) % 5 == 0,
                profiles = profiles.joinToString(","),
                fullName = faker.name().fullName(),
                notificationToken = null,
                notificationTokenProvider = NotificationTokenProvider.FIREBASE
            ), false) ?: error("Error saving userV2 for userId: ${id}")
            // This save will also take care of creating the job to process location data
            userV2Provider.updateUserV2Location(UpdateUserV2LocationRequest (
                userId = userV2.userId,
                lat = location.lat!!,
                lng = location.lng!!,
                zipcode = location.zipcode!!,
                name = location.name,
                googlePlaceId = location.googlePlaceId,
            ))
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
                    countOfPost = Random.nextInt(minPostToFake+1, maxPostToFake),
                    maxCountOfComments = Random.nextInt(minPostToFake+1, maxPostToFake),
                    maxCountOfReplies = Random.nextInt(minPostToFake+1, maxPostToFake),
                ),
            ))
        }

        return result.filterNotNull()
    }


    fun createFakeData(userId: String, request: FakerRequest): List<Any> {
        if (request.countOfPost > maxPostToFake ||
            request.maxCountOfComments > maxPostToFake ||
            request.maxCountOfReplies > maxPostToFake) {
            error("Max of 25 fake data points in any category is allowed are allowed to be created at one time")
        }

        if (request.countOfPost < minPostToFake ||
            request.maxCountOfComments < minPostToFake ||
            request.maxCountOfReplies < minPostToFake) {
            error("Minimum value required is 1 for all the above fields.")
        }

        val posts = mutableListOf<Post?>()
        val comments = mutableListOf<Comment?>()
        val replies = mutableListOf<Reply?>()
        val likes = mutableListOf<Like?>()
        val bookmarks = mutableListOf<Bookmark?>()

        val faker = Faker()

        for (i in 1..request.countOfPost) {
            val req = SavePostRequest(
                postType = PostType.GENERIC_POST,
                title = faker.book().title(),
                description = faker.book().publisher(),
                tags = HashTagsList(listOf(
                    HashTagData(
                        tagId = "newhouse",
                        displayName = "newhouse",
                    ),
                    HashTagData(
                        tagId = "lakesideview",
                        displayName = "lakesideview",
                    )
                )),
                categories = setOf(CategoryV2.KITCHEN, CategoryV2.EXTERIOR),
                locationRequest = sampleLocationRequests[Random.nextInt(sampleLocationRequests.size)],
                mediaDetails = sampleMedia[Random.nextInt(sampleMedia.size)]
            )
            posts.add(postProvider.save(userId, req))
        }


        posts.filterNotNull().map {
            val randomCount = Random.nextInt(0, request.maxCountOfComments)
            for (i in 1..randomCount) {
                comments.add(commentProvider.save(userId, SaveCommentRequest(
                    postId = it.postId,
                    postType = it.postType,
                    text = faker.lorem().sentence(),
                )))
            }
        }

        comments.filterNotNull().map {
            val randomCount = Random.nextInt(0, request.maxCountOfReplies)
            for (i in 1..randomCount) {
                replies.add(replyProvider.save(userId, SaveCommentReplyRequest(
                    commentId = it.commentId,
                    postId = it.postId,
                    text = faker.lorem().sentence(),
                )))
            }
        }


        posts.filterNotNull().map {
            likes.add(likeProvider.save(userId, SaveLikeRequest(
                resourceType = ResourceType.POST,
                resourceId = it.postId,
                action = LikeUpdateAction.ADD,
            )))
            bookmarks.add(bookmarkProvider.save(userId, SaveBookmarkRequest(
                    resourceType = ResourceType.POST,
                    resourceId = it.postId,
                    action = BookmarkUpdateAction.ADD,
            )))
        }

        comments.filterNotNull().map {
            likes.add(likeProvider.save(userId, SaveLikeRequest(
                resourceType = ResourceType.POST_COMMENT,
                resourceId = it.commentId,
                action = LikeUpdateAction.ADD,
            )))
            bookmarks.add(bookmarkProvider.save(userId, SaveBookmarkRequest(
                resourceType = ResourceType.POST_COMMENT,
                resourceId = it.commentId,
                action = BookmarkUpdateAction.ADD,
            )))
        }

        replies.filterNotNull().map {
            likes.add(likeProvider.save(userId, SaveLikeRequest(
                resourceType = ResourceType.POST_COMMENT_REPLY,
                resourceId = it.replyId,
                action = LikeUpdateAction.ADD,
            )))
            bookmarks.add(bookmarkProvider.save(userId, SaveBookmarkRequest(
                resourceType = ResourceType.POST_COMMENT_REPLY,
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

}
