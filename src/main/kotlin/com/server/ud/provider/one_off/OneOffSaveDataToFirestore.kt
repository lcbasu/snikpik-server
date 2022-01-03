package com.server.ud.provider.one_off

import com.server.ud.provider.bookmark.BookmarkForResourceByUserProvider
import com.server.ud.provider.bookmark.BookmarksCountByResourceProvider
import com.server.ud.provider.bookmark.BookmarksCountByUserProvider
import com.server.ud.provider.comment.CommentForPostByUserProvider
import com.server.ud.provider.comment.CommentsCountByPostProvider
import com.server.ud.provider.like.LikeForResourceByUserProvider
import com.server.ud.provider.like.LikesCountByResourceProvider
import com.server.ud.provider.like.LikesCountByUserProvider
import com.server.ud.provider.post.PostsCountByUserProvider
import com.server.ud.provider.reply.RepliesCountByCommentProvider
import com.server.ud.provider.social.FollowersCountByUserProvider
import com.server.ud.provider.social.FollowingsCountByUserProvider
import com.server.ud.provider.social.SocialRelationProvider
import com.server.ud.provider.user.UserV2Provider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class OneOffSaveDataToFirestore {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var bookmarkForResourceByUserProvider: BookmarkForResourceByUserProvider

    @Autowired
    private lateinit var bookmarksCountByResourceProvider: BookmarksCountByResourceProvider

    @Autowired
    private lateinit var bookmarksCountByUserProvider: BookmarksCountByUserProvider

    @Autowired
    private lateinit var commentForPostByUserProvider: CommentForPostByUserProvider

    @Autowired
    private lateinit var commentsCountByPostProvider: CommentsCountByPostProvider

    @Autowired
    private lateinit var likeForResourceByUserProvider: LikeForResourceByUserProvider

    @Autowired
    private lateinit var likesCountByResourceProvider: LikesCountByResourceProvider

    @Autowired
    private lateinit var likesCountByUserProvider: LikesCountByUserProvider

    @Autowired
    private lateinit var postsCountByUserProvider: PostsCountByUserProvider

    @Autowired
    private lateinit var repliesCountByCommentProvider: RepliesCountByCommentProvider

    @Autowired
    private lateinit var followersCountByUserProvider: FollowersCountByUserProvider

    @Autowired
    private lateinit var followingsCountByUserProvider: FollowingsCountByUserProvider

    @Autowired
    private lateinit var socialRelationProvider: SocialRelationProvider

    @Autowired
    private lateinit var userV2Provider: UserV2Provider

//    @Autowired
//    private lateinit var resourceViewsCountByResourceProvider: ResourceViewsCountByResourceProvider

    fun saveMetadataToFirestore() {
        logger.info("Start save data to firestore")

//        bookmarkForResourceByUserProvider.saveAllToFirestore()
//        bookmarksCountByResourceProvider.saveAllToFirestore()
//        bookmarksCountByUserProvider.saveAllToFirestore()
//        commentForPostByUserProvider.saveAllToFirestore()
//        commentsCountByPostProvider.saveAllToFirestore()
//        likeForResourceByUserProvider.saveAllToFirestore()
//        likesCountByResourceProvider.saveAllToFirestore()
//        likesCountByUserProvider.saveAllToFirestore()
//        postsCountByUserProvider.saveAllToFirestore()
//        repliesCountByCommentProvider.saveAllToFirestore()
//        followersCountByUserProvider.saveAllToFirestore()
//        followingsCountByUserProvider.saveAllToFirestore()
//        socialRelationProvider.saveAllToFirestore()
        userV2Provider.saveAllToFirestore()
//        resourceViewsCountByResourceProvider.saveAllToFirestore()

        logger.info("Finish save data to firestore")
    }

}
