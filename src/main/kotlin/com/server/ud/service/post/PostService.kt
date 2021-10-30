package com.server.ud.service.post

import com.server.ud.dto.FakePostRequest
import com.server.ud.dto.SavePostRequest
import com.server.ud.dto.SavedPostResponse

abstract class PostService {
    abstract fun savePost(savePostRequest: SavePostRequest): SavedPostResponse?
    abstract fun fakeSavePosts(request: FakePostRequest): List<SavedPostResponse>?
}
