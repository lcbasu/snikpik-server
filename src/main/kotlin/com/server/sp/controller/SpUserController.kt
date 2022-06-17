package com.server.sp.controller

import com.server.common.provider.SecurityProvider
import com.server.sp.dto.SavedSpUserResponse
import com.server.sp.service.user.SpUserService
import io.micrometer.core.annotation.Timed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@Timed
@RequestMapping("sp/user")
class SpUserController {

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    @Autowired
    private lateinit var spUserService: SpUserService

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveLoggedInSpUser(): SavedSpUserResponse? {
        securityProvider.validateRequest()
        return spUserService.saveLoggedInSpUser()
    }
}
