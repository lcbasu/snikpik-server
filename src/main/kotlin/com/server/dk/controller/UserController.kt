package com.server.dk.controller

import com.server.dk.dto.*
import com.server.dk.service.UserService
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

    @RequestMapping(value = ["/getUserRoles/{absoluteMobile}"], method = [RequestMethod.GET])
    fun getUserRoles(@PathVariable absoluteMobile: String): UserRoleResponse? {
        return userService.getUserRoles(absoluteMobile)
    }

    @RequestMapping(value = ["/verifyPhone/{absoluteMobile}"], method = [RequestMethod.GET])
    fun verifyPhone(@PathVariable absoluteMobile: String): PhoneVerificationResponse? {
        return userService.verifyPhone(absoluteMobile)
    }

    @RequestMapping(value = ["/saveAddress"], method = [RequestMethod.POST])
    fun saveAddress(@RequestBody saveUserAddressRequest: SaveUserAddressRequest): SavedUserAddressResponse? {
        return userService.saveAddress(saveUserAddressRequest)
    }

    @RequestMapping(value = ["/registerNotificationSettings"], method = [RequestMethod.POST])
    fun registerNotificationSettings(@RequestBody notificationSettingsRequest: RegisterUserNotificationSettingsRequest): SavedUserResponse? {
        return userService.registerNotificationSettings(notificationSettingsRequest)
    }

    @RequestMapping(value = ["/getAddresses"], method = [RequestMethod.GET])
    fun getAddresses(): UserAddressesResponse {
        return userService.getAddresses()
    }


    @RequestMapping(value = ["/updateDefaultAddress"], method = [RequestMethod.POST])
    fun updateDefaultAddress(@RequestBody request: UpdateDefaultAddressRequest): UserAddressesResponse? {
        return userService.updateDefaultAddress(request)
    }
}
