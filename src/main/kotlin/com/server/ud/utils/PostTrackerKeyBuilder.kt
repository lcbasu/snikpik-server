package com.server.ud.utils

import com.server.common.utils.CommonUtils
import com.server.ud.entities.reply.Reply
import com.server.ud.enums.PostType

object PostTrackerKeyBuilder {
    fun getPostTrackerKeyForReply(reply: Reply): String =
        "${reply.replyId}${CommonUtils.STRING_SEPARATOR}${reply.userId}${CommonUtils.STRING_SEPARATOR}${reply.commentId}${CommonUtils.STRING_SEPARATOR}${reply.postId}${CommonUtils.STRING_SEPARATOR}${reply.postType}"

    fun parsePostTrackerKeyForReply(key: String): PostReplyTrackerKey {
        val values = key.split(CommonUtils.STRING_SEPARATOR)
        return PostReplyTrackerKey(
            replyId = values[0],
            userId = values[1],
            commentId = values[2],
            postId = values[3],
            postType = PostType.valueOf(values[4])
        )
    }
}

data class PostReplyTrackerKey(
    val replyId: String,
    val userId: String,
    val commentId: String,
    val postId: String,
    val postType: PostType
)
