package com.server.ud.enums

enum class ResourceType {
    POST,
    POST_COMMENT,
    POST_COMMENT_REPLY,
    WALL,
    WALL_COMMENT,
    WALL_COMMENT_REPLY,
}

fun getResourcePostType(postType: PostType): ResourceType {
    return when (postType) {
        PostType.GENERIC_POST -> ResourceType.POST
        PostType.COMMUNITY_WALL_POST -> ResourceType.WALL
    }
}

fun getResourceCommentType(postType: PostType): ResourceType {
    return when (postType) {
        PostType.GENERIC_POST -> ResourceType.POST_COMMENT
        PostType.COMMUNITY_WALL_POST -> ResourceType.WALL_COMMENT
    }
}

fun getResourceReplyType(postType: PostType): ResourceType {
    return when (postType) {
        PostType.GENERIC_POST -> ResourceType.POST_COMMENT_REPLY
        PostType.COMMUNITY_WALL_POST -> ResourceType.WALL_COMMENT_REPLY
    }
}
