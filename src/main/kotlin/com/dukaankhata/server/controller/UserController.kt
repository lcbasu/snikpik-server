package com.dukaankhata.server.controller

import com.dukaankhata.server.dto.*
import com.dukaankhata.server.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("user")
class UserController {
    @Autowired
    private lateinit var userService: UserService

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveUser(): SavedUserResponse? {
        return userService.saveUser()
    }

    @RequestMapping(value = ["/getUserRoles/{phoneNumber}"], method = [RequestMethod.GET])
    fun getUserRoles(@PathVariable phoneNumber: String): UserRoleResponse? {
        return userService.getUserRoles(phoneNumber)
    }

    @RequestMapping(value = ["/verifyPhone/{phoneNumber}"], method = [RequestMethod.GET])
    fun verifyPhone(@PathVariable phoneNumber: String): VerifyPhoneResponse? {
        return userService.verifyPhone(phoneNumber)
    }

    @RequestMapping(value = ["/saveAddress"], method = [RequestMethod.POST])
    fun saveAddress(@RequestBody saveUserAddressRequest: SaveUserAddressRequest): SavedUserAddressResponse? {
        return userService.saveAddress(saveUserAddressRequest)
    }
}
