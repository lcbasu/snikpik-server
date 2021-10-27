package com.server.ud.service

import com.server.ud.dto.SaveUserPostRequest
import com.server.ud.dto.SavedUserPostResponse

abstract class UserPostService {
    abstract fun saveUserPost(saveUserPostRequest: SaveUserPostRequest): SavedUserPostResponse?
    abstract fun fakeSaveUserPost(): List<SavedUserPostResponse>?

}
