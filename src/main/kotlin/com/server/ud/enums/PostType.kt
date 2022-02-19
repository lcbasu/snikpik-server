package com.server.ud.enums

import com.server.common.enums.MediaType

enum class PostType {
    GENERIC_POST,
    COMMUNITY_WALL_POST,
}

enum class InstagramToUnboxPostType {
    CAROUSEL_ALBUM_IMAGE,
    CAROUSEL_ALBUM_VIDEO,
    CAROUSEL_ALBUM_IMAGE_AND_VIDEO,
    VIDEO,
    IMAGE,
}

enum class InstagramMediaType {
    CAROUSEL_ALBUM,
    VIDEO,
    IMAGE,
}

fun InstagramMediaType.getMediaType(): MediaType {
    this.apply {
        return when (this) {
            InstagramMediaType.CAROUSEL_ALBUM -> MediaType.IMAGE
            InstagramMediaType.VIDEO -> MediaType.VIDEO
            InstagramMediaType.IMAGE -> MediaType.IMAGE
        }
    }
}
