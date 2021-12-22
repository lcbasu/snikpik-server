package com.server.ud.enums

enum class UserActivityType {

    // Post
    // Generic
    POST_CREATED,
    POST_LIKED,
    POST_SAVED,
    POST_SHARED,
    // Wall
    WALL_CREATED,
    WALL_LIKED,
    WALL_SAVED,
    WALL_SHARED,



    // Comment
    // Post Comment
    COMMENTED_ON_POST,
    POST_COMMENT_LIKED,
    // Wall Comment
    COMMENTED_ON_WALL,
    WALL_COMMENT_LIKED,



    // Replies
    // Post Comment Replies
    REPLIED_TO_POST_COMMENT,
    POST_COMMENT_REPLY_LIKED,
    // Wall Comment Replies
    REPLIED_TO_WALL_COMMENT,
    WALL_COMMENT_REPLY_LIKED,



    // User
    USER_FOLLOWED,
    USER_CLICKED_CONNECT, // Tried to connect with the user by clicking on connect button
    USER_PROFILE_SHARED,

}

fun UserActivityType.toUserAggregateActivityType(): UserAggregateActivityType {
    this.apply {
        return when (this) {
            UserActivityType.POST_CREATED,
            UserActivityType.WALL_CREATED -> UserAggregateActivityType.NEW_POST_CREATED

            UserActivityType.POST_LIKED,
            UserActivityType.WALL_LIKED,
            UserActivityType.POST_COMMENT_LIKED,
            UserActivityType.WALL_COMMENT_LIKED,
            UserActivityType.POST_COMMENT_REPLY_LIKED,
            UserActivityType.WALL_COMMENT_REPLY_LIKED -> UserAggregateActivityType.LIKED


            UserActivityType.POST_SAVED,
            UserActivityType.WALL_SAVED -> UserAggregateActivityType.SAVED


            UserActivityType.POST_SHARED,
            UserActivityType.WALL_SHARED,
            UserActivityType.USER_PROFILE_SHARED -> UserAggregateActivityType.SHARED

            UserActivityType.COMMENTED_ON_POST,
            UserActivityType.COMMENTED_ON_WALL -> UserAggregateActivityType.COMMENTED


            UserActivityType.REPLIED_TO_POST_COMMENT,
            UserActivityType.REPLIED_TO_WALL_COMMENT -> UserAggregateActivityType.REPLIED


            UserActivityType.USER_FOLLOWED -> UserAggregateActivityType.FOLLOWED


            UserActivityType.USER_CLICKED_CONNECT -> UserAggregateActivityType.CLICKED_CONNECT
        }
    }
}

enum class UserAggregateActivityType {
    NEW_POST_CREATED,
    LIKED,
    SAVED,
    SHARED,
    COMMENTED,
    REPLIED,
    FOLLOWED,
    CLICKED_CONNECT,
}
