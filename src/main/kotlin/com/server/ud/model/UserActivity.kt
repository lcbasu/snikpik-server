//package com.server.ud.model
//
//import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
//import com.server.ud.enums.PostType
//import com.server.ud.enums.UserActivityType
//import org.codehaus.jackson.annotate.JsonIgnoreProperties
//import org.codehaus.jackson.annotate.JsonSubTypes
//import org.codehaus.jackson.annotate.JsonTypeInfo
//import org.codehaus.jackson.annotate.JsonTypeName
//
//interface BaseUserActivityData {
//    val userActivityType: UserActivityType
//}
//
//@JsonTypeInfo(
//    use = JsonTypeInfo.Id.NAME,
//    include = JsonTypeInfo.As.PROPERTY,
//    property = "type"
//)
//@JsonSubTypes(
//    JsonSubTypes.Type(value = PostLevelUserActivity::class, name = "PostLevelUserActivity"),
//    JsonSubTypes.Type(value = CommentLevelUserActivity::class, name = "CommentLevelUserActivity"),
//    JsonSubTypes.Type(value = ReplyLevelUserActivity::class, name = "ReplyLevelUserActivity"),
//    JsonSubTypes.Type(value = UserLevelUserActivity::class, name = "UserLevelUserActivity"),
//)
//sealed class UserActivityData: BaseUserActivityData
//
//// In this case postUserId = activityByUserId
//@JsonTypeName("PostLevelUserActivity")
//@JsonIgnoreProperties(ignoreUnknown = true)
//data class PostLevelUserActivity(
//    val activityByUserId: String,
//
//    val postId: String,
//    val postType: PostType,
//    val postUserId: String,
//    val mediaDetails: String? = null,
//    val title: String? = null,
//    val description: String? = null,
//    override val userActivityType: UserActivityType,
//): UserActivityData()
//
//@JsonTypeName("CommentLevelUserActivity")
//@JsonIgnoreProperties(ignoreUnknown = true)
//data class CommentLevelUserActivity(
//    val activityByUserId: String,
//
//    val commentId: String,
//    val commentUserId: String,
//    val commentText: String?,
//    val commentMediaDetails: String? = null,
//
//    val postId: String,
//    val postType: PostType,
//    val postUserId: String,
//    val postMediaDetails: String? = null,
//    val postTitle: String? = null,
//    val postDescription: String? = null,
//    override val userActivityType: UserActivityType,
//): UserActivityData()
//
//@JsonTypeName("ReplyLevelUserActivity")
//@JsonIgnoreProperties(ignoreUnknown = true)
//data class ReplyLevelUserActivity(
//    val activityByUserId: String,
//
//    val replyId: String,
//    val replyUserId: String,
//    val replyText: String?,
//    val replyMediaDetails: String? = null,
//
//    val commentId: String,
//    val commentUserId: String,
//    val commentText: String?,
//    val commentMediaDetails: String? = null,
//
//    val postId: String,
//    val postType: PostType,
//    val postUserId: String,
//    val postMediaDetails: String? = null,
//    val postTitle: String? = null,
//    val postDescription: String? = null,
//    override val userActivityType: UserActivityType,
//): UserActivityData()
//
//// User
//@JsonTypeName("UserLevelUserActivity")
//@JsonIgnoreProperties(ignoreUnknown = true)
//data class UserLevelUserActivity(
//    val activityByUserId: String,
//
//    val forUserId: String,
//    override val userActivityType: UserActivityType,
//): UserActivityData()
//
//fun UserActivityData.convertToString(): String {
//    this.apply {
//        return try {
//            jacksonObjectMapper().writeValueAsString(this)
//        } catch (e: Exception) {
//            ""
//        }
//    }
//}
//
//fun parseUserActivityData(userActivityData: String, userActivityType: UserActivityType): UserActivityData =
//    when (userActivityType) {
//        UserActivityType.POST_CREATED,
//        UserActivityType.POST_LIKED,
//        UserActivityType.POST_SAVED,
//        UserActivityType.POST_SHARED,
//        UserActivityType.WALL_CREATED,
//        UserActivityType.WALL_LIKED,
//        UserActivityType.WALL_SAVED,
//        UserActivityType.WALL_SHARED -> jacksonObjectMapper().readValue(
//            userActivityData,
//            PostLevelUserActivity::class.java
//        )
//
//
//        UserActivityType.COMMENTED_ON_POST,
//        UserActivityType.POST_COMMENT_LIKED,
//        UserActivityType.COMMENTED_ON_WALL,
//        UserActivityType.WALL_COMMENT_LIKED -> jacksonObjectMapper().readValue(
//            userActivityData,
//            CommentLevelUserActivity::class.java
//        )
//
//
//        UserActivityType.REPLIED_TO_POST_COMMENT,
//        UserActivityType.POST_COMMENT_REPLY_LIKED,
//        UserActivityType.REPLIED_TO_WALL_COMMENT,
//        UserActivityType.WALL_COMMENT_REPLY_LIKED -> jacksonObjectMapper().readValue(
//            userActivityData,
//            ReplyLevelUserActivity::class.java
//        )
//
//
//        UserActivityType.USER_FOLLOWED,
//        UserActivityType.USER_CLICKED_CONNECT,
//        UserActivityType.USER_PROFILE_SHARED -> jacksonObjectMapper().readValue(
//            userActivityData,
//            UserLevelUserActivity::class.java
//        )
//    }
