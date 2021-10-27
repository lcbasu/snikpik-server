package com.server.ud.controller

import com.server.ud.dto.SaveUserPostRequest
import com.server.ud.dto.SavedUserPostResponse
import com.server.ud.service.UserPostService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("userPost")
class UserPostController {
    @Autowired
    private lateinit var userPostService: UserPostService

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveUserPost(@RequestBody saveUserPostRequest: SaveUserPostRequest): SavedUserPostResponse? {
        return userPostService.saveUserPost(saveUserPostRequest)
    }

    @RequestMapping(value = ["/fakeSave"], method = [RequestMethod.POST])
    fun fakeSaveUserPost(): List<SavedUserPostResponse>? {
        return userPostService.fakeSaveUserPost()
    }
}
