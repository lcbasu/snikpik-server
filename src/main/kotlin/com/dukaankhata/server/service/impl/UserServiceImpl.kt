package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.*
import com.dukaankhata.server.service.UserService
import com.dukaankhata.server.provider.AddressProvider
import com.dukaankhata.server.provider.AuthProvider
import com.dukaankhata.server.provider.UserRoleProvider
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

    // Save data using the auth token credentials
    override fun saveUser(): SavedUserResponse? {
        val firebaseAuthUserPrincipal = authProvider.getFirebaseAuthUser()
        val phoneNumber = firebaseAuthUserPrincipal?.getPhoneNumber() ?: ""
        val uid = firebaseAuthUserPrincipal?.getUid() ?: ""
        // if already saved then return that value or save a new one
        var user = authProvider.getRequestUserEntity() ?: authProvider.createUser(phoneNumber = phoneNumber, fullName = phoneNumber, uid = uid)
//        if (uid.isNotBlank() && uid.isNotEmpty() && (user.uid?.isBlank() == true || user.uid?.isEmpty() == true)) {
//            user = authProvider.updateUserUid(user.id, uid)
//        }
        return user.toSavedUserResponse()
    }

    override fun getUser(): SavedUserResponse? = authProvider.getRequestUserEntity()?.toSavedUserResponse()

    override fun getUserRoles(phoneNumber: String): UserRoleResponse? {
        val user = authProvider.getUserByMobile(phoneNumber);
        val userRoles = user?.let { userRoleProvider.getUserRolesForUser(it) } ?: emptyList()
        val roles = userRoles.filter { it.id != null && it.id?.roleType != null }.map { it.id?.roleType }
        return UserRoleResponse(roles = roles.filterNotNull().toSet())
    }

    override fun verifyPhone(phoneNumber: String): VerifyPhoneResponse? {
        return authProvider.getVerifiedPhoneResponse(phoneNumber)
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
}
