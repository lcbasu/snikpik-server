package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.*
import com.dukaankhata.server.service.UserService
import com.dukaankhata.server.service.converter.UserServiceConverter
import com.dukaankhata.server.utils.AddressUtils
import com.dukaankhata.server.utils.AuthUtils
import com.dukaankhata.server.utils.UserRoleUtils
import com.twilio.rest.lookups.v1.PhoneNumber
import io.sentry.Sentry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserServiceImpl : UserService() {

    @Autowired
    private lateinit var authUtils: AuthUtils

    @Autowired
    private lateinit var userRoleUtils: UserRoleUtils

    @Autowired
    private lateinit var addressUtils: AddressUtils

    @Autowired
    private lateinit var userServiceConverter: UserServiceConverter

    // Save data using the auth token credentials
    override fun saveUser(): SavedUserResponse? {
        val firebaseAuthUserPrincipal = authUtils.getFirebaseAuthUser()
        val phoneNumber = firebaseAuthUserPrincipal?.getPhoneNumber() ?: ""
        val uid = firebaseAuthUserPrincipal?.getUid() ?: ""
        // if already saved then return that value or save a new one
        var user = authUtils.getRequestUserEntity() ?: authUtils.createUser(phoneNumber = phoneNumber, fullName = phoneNumber, uid = uid)
        if (uid.isNotBlank() && uid.isNotEmpty() && (user.uid?.isBlank() == true || user.uid?.isEmpty() == true)) {
            user = authUtils.updateUserUid(user.id, uid)
        }
        return user.toSavedUserResponse()
    }

    override fun getUser(): SavedUserResponse? = authUtils.getRequestUserEntity()?.toSavedUserResponse()

    override fun getUserRoles(phoneNumber: String): UserRoleResponse? {
        val user = authUtils.getUserByMobile(phoneNumber);
        val userRoles = user?.let { userRoleUtils.getUserRolesForUser(it) } ?: emptyList()
        return userServiceConverter.getUserRolesResponse(userRoles)
    }

    override fun verifyPhone(phoneNumber: String): VerifyPhoneResponse? {
        try {
            val result = PhoneNumber.fetcher(com.twilio.type.PhoneNumber(phoneNumber)).fetch()
            return VerifyPhoneResponse(
                valid = true,
                countryCode = result.countryCode,
                numberInNationalFormat = result.nationalFormat,
                numberInInterNationalFormat = result.phoneNumber.toString()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Sentry.captureException(e)
        }
        return VerifyPhoneResponse(
            valid = false
        )
    }

    override fun saveAddress(saveUserAddressRequest: SaveUserAddressRequest): SavedUserAddressResponse? {
        val requestContext = authUtils.validateRequest()

        val user = requestContext.user ?: error("User is required")

        val userAddress = addressUtils.saveUserAddress(user, saveUserAddressRequest.name, saveUserAddressRequest.address) ?: error("Error while saving user address")
        val newAddress = userAddress.address ?: error("Address should always be present for userAddress")
        val updatedUser = authUtils.updateUserDefaultAddress(user, newAddress) ?: error("Error while updating default address for user")

        return SavedUserAddressResponse(
            user = updatedUser.toSavedUserResponse(),
            address = newAddress.toSavedAddressResponse()
        )
    }
}
