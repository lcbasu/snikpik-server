package com.server.ud.service

import com.server.ud.dto.SavePostRequest
import com.server.ud.dto.SavedPostResponse

abstract class PostService {
    abstract fun savePost(savePostRequest: SavePostRequest): SavedPostResponse?
    abstract fun fakeSavePosts(): List<SavedPostResponse>?
}
