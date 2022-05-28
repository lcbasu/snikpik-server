package com.server.common.service

import com.server.common.dto.*
import com.server.common.entities.toSavedUserResponse
import com.server.common.provider.AuthProvider
import com.server.common.provider.SecurityProvider
import com.server.common.provider.UserRoleProvider
import com.server.dk.dto.SaveUserAddressRequest
import com.server.dk.dto.SavedUserAddressResponse
import com.server.dk.dto.UserAddressesResponse
import com.server.dk.dto.toSavedAddressResponse
import com.server.dk.provider.AddressProvider
import com.server.ud.provider.user.UserV2Provider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserServiceImpl : UserService() {

    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var userRoleProvider: UserRoleProvider

    @Autowired
    private lateinit var addressProvider: AddressProvider

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    @Autowired
    private lateinit var userV2Provider: UserV2Provider

    // Save data using the auth token credentials
    override fun saveUser(): SavedUserResponse? {
        val firebaseAuthUserPrincipal = securityProvider.getFirebaseAuthUser()
        val absoluteMobile = firebaseAuthUserPrincipal?.getAbsoluteMobileNumber() ?: ""
        val uid = firebaseAuthUserPrincipal?.getUid() ?: ""
        // if already saved then return that value or save a new one
        var user = authProvider.getRequestUserEntity() ?: authProvider.createUser(absoluteMobile = absoluteMobile, fullName = null, uid = uid)
//        if (uid.isNotBlank() && uid.isNotEmpty() && (user.uid?.isBlank() == true || user.uid?.isEmpty() == true)) {
//            user = authProvider.updateUserUid(user.id, uid)
//        }
        return user.toSavedUserResponse()
    }

    override fun getUser(): SavedUserResponse? = authProvider.getRequestUserEntity()?.toSavedUserResponse()

    override fun getUserRoles(absoluteMobile: String): UserRoleResponse? {
        val user = authProvider.getUserByMobile(absoluteMobile);
        val userRoles = user?.let { userRoleProvider.getUserRolesForUser(it) } ?: emptyList()
        val roles = userRoles.filter { it.id != null && it.id?.roleType != null }.map { it.id?.roleType }
        return UserRoleResponse(roles = roles.filterNotNull().toSet())
    }

    override fun verifyPhone(absoluteMobile: String): PhoneVerificationResponse? {
        return authProvider.getVerifiedPhoneResponse(absoluteMobile)
    }

    override fun saveAddress(saveUserAddressRequest: SaveUserAddressRequest): SavedUserAddressResponse? {
        val requestContext = authProvider.validateRequest()

        val user = requestContext.user ?: error("User is required")

        val userAddress = addressProvider.saveUserAddress(user, saveUserAddressRequest.address) ?: error("Error while saving user address")
        val newAddress = userAddress.address ?: error("Address should always be present for userAddress")
        val updatedUser = authProvider.updateUserDefaultAddress(user, newAddress) ?: error("Error while updating default address for user")

        return SavedUserAddressResponse(
            user = updatedUser.toSavedUserResponse(),
            address = newAddress.toSavedAddressResponse()
        )
    }

    override fun getAddresses(): UserAddressesResponse {
        val requestContext = authProvider.validateRequest()
        val user = requestContext.user
        val userAddresses = addressProvider.getUserAddresses(user)
        return UserAddressesResponse(
            user = user.toSavedUserResponse(),
            addresses = userAddresses.mapNotNull {
                it.address?.toSavedAddressResponse()
            }
        )
    }

    override fun registerNotificationSettings(notificationSettingsRequest: RegisterUserNotificationSettingsRequest): SavedUserResponse? {
        val requestContext = authProvider.validateRequest()
        val user = requestContext.user
        val updatedUser = authProvider.registerNotificationSettings(
            user = user,
            token = notificationSettingsRequest.token,
            tokenProvider = notificationSettingsRequest.tokenProvider
        )
        return updatedUser.toSavedUserResponse()
    }

    override fun updateDefaultAddress(request: UpdateDefaultAddressRequest): UserAddressesResponse? {
        val requestContext = authProvider.validateRequest()
        val userAddress = addressProvider.getAddress(request.defaultAddressId) ?: error("Error while saving user address")
        val updatedUser = authProvider.updateUserDefaultAddress(requestContext.user, userAddress) ?: error("Error while updating default address for user")
        val userAddresses = addressProvider.getUserAddresses(updatedUser)
        return UserAddressesResponse(
            user = updatedUser.toSavedUserResponse(),
            addresses = userAddresses.mapNotNull {
                it.address?.toSavedAddressResponse()
            }
        )
    }

}
