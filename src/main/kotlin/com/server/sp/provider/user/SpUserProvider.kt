package com.server.sp.provider.user

import com.google.firebase.cloud.FirestoreClient
import com.server.common.dto.AWSLambdaAuthResponse
import com.server.common.enums.MediaType
import com.server.common.enums.NotificationTokenProvider
import com.server.common.enums.ProcessingType
import com.server.common.enums.ReadableIdPrefix
import com.server.common.model.MediaDetailsV2
import com.server.common.model.SingleMediaDetail
import com.server.common.model.UserDetailsFromToken
import com.server.common.model.convertToString
import com.server.common.pagination.CassandraPageV2
import com.server.common.provider.CommonProvider
import com.server.common.provider.SecurityProvider
import com.server.common.provider.UserIdByMobileNumberProvider
import com.server.common.provider.automation.AutomationProvider
import com.server.common.utils.CommonUtils
import com.server.common.utils.DateUtils
import com.server.common.utils.PaginationRequestUtil
import com.server.sp.dao.user.SpUserRepository
import com.server.sp.dto.*
import com.server.sp.entities.user.SpUser
import com.server.sp.entities.user.toSavedSpUserResponse
import com.server.sp.entities.user.toSpUserPublicMiniDataResponse
import com.server.sp.provider.job.SpJobProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class SpUserProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var spUserRepository: SpUserRepository

    @Autowired
    private lateinit var spJobProvider: SpJobProvider

    @Autowired
    private lateinit var spUsersByHandleProvider: SpUsersByHandleProvider

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    @Autowired
    private lateinit var userIdByMobileNumberProvider: UserIdByMobileNumberProvider

    @Autowired
    private lateinit var automationProvider: AutomationProvider

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    @Autowired
    private lateinit var commonProvider: CommonProvider

    fun getSpUser(userId: String): SpUser? =
        try {
            val userIdToFind = if (userId.startsWith(ReadableIdPrefix.USR.name)) userId else "${ReadableIdPrefix.USR.name}$userId"
            val users = spUserRepository.findAllByUserId(userIdToFind)
            if (users.size > 1) {
                error("More than one user has same userId: $userIdToFind")
            }
            users.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting User for $userId failed.")
            e.printStackTrace()
            null
        }

    private fun getUserByHandle(handle: String): SpUser? {
        val usersByHandle = spUsersByHandleProvider.getUsersByHandle(handle) ?: return null
        return getSpUser(usersByHandle.userId)
    }

    fun getUserByIdOrHandle(userIdOrHandle: String) : SpUser? {
        return getSpUser(userIdOrHandle) ?: getUserByHandle(userIdOrHandle)
    }

    fun saveSpUser(spUser: SpUser, processingType: ProcessingType = ProcessingType.REFRESH) : SpUser? {
        try {
            val oldUser = getSpUser(spUser.userId)
            val savedUser = spUserRepository.save(spUser)
            logger.info("SpUser saved with userId: ${savedUser.userId}.")
//            if (oldUser == null) {
//                logger.info("User ${userV2.userId} is new.")
//                automationProvider.sendSlackMessageForNewUser(savedUser)
//            }
//            if (processingType == ProcessingType.REFRESH) {
//                udJobProvider.scheduleProcessingForSpUser(savedUser.userId)
//            } else if (processingType == ProcessingType.DELETE_AND_REFRESH) {
//                udJobProvider.scheduleReProcessingForSpUser(savedUser.userId)
//            }
//            saveSpUserToFirestore(savedUser)
//            saveForAuthV2(savedUser)
            processJustAfterSaveSpUser(oldUser, savedUser, processingType)
            return savedUser
        } catch (e: Exception) {
            logger.error("Saving SpUser for ${spUser.userId} failed.")
            e.printStackTrace()
            return null
        }
    }

    private fun processJustAfterSaveSpUser (oldUser: SpUser?, savedUser: SpUser, processingType: ProcessingType = ProcessingType.REFRESH) {
        GlobalScope.launch {
            if (oldUser == null) {
                logger.info("User ${savedUser.userId} is new.")
                automationProvider.sendSlackMessageForNewSpUser(savedUser)
            }
            if (processingType == ProcessingType.REFRESH) {
                spJobProvider.scheduleProcessingForSpUser(savedUser.userId)
            } else if (processingType == ProcessingType.DELETE_AND_REFRESH) {
                spJobProvider.scheduleReProcessingForSpUser(savedUser.userId)
            }
            saveSpUserToFirestore(savedUser)
            saveForAuthV2(savedUser)
        }
    }

    fun updateSpUserHandle(request: UpdateSpUserHandleRequest): SpUser? {
        val firebaseAuthUser = securityProvider.validateRequest()
        val user = getSpUser(firebaseAuthUser.getUserIdToUse()) ?: error("No user found for userId: ${firebaseAuthUser.getUserIdToUse()}")
        return updateSpUserHandle(user, request.newHandle)
    }

    fun updateSpUserHandle(user: SpUser, newHandle: String): SpUser? {
        if (spUsersByHandleProvider.isHandleAvailable(newHandle)) {
            val newUserToBeSaved = user.copy(handle = newHandle)
            val savedUser = saveSpUser(newUserToBeSaved)
            spUsersByHandleProvider.save(savedUser!!)
            return savedUser
        } else {
            error("$newHandle not available for userId: ${user.userId}")
        }
    }

    fun updateNotificationToken(request: UpdateSpNotificationTokenRequest): SpUser? {
        val firebaseAuthUser = securityProvider.validateRequest()
        val user = getSpUser(firebaseAuthUser.getUserIdToUse()) ?: error("No user found for userId: ${firebaseAuthUser.getUserIdToUse()}")
        val newUserToBeSaved = user.copy(notificationToken = request.token, notificationTokenProvider = request.tokenProvider)
        logger.info("Updating notification token for userId: ${user.userId} (${user.absoluteMobile}), new token: ${request.token}")
        return saveSpUser(newUserToBeSaved, ProcessingType.NO_PROCESSING)
    }

    fun updateSpUserDP(request: UpdateSpUserDPRequest): SpUser? {
        val firebaseAuthUser = securityProvider.validateRequest()
        val user = getSpUser(firebaseAuthUser.getUserIdToUse()) ?: error("No user found for userId: ${firebaseAuthUser.getUserIdToUse()}")
        val newUserToBeSaved = user.copy(dp = request.dp.convertToString())
        return saveSpUser(newUserToBeSaved, ProcessingType.NO_PROCESSING)
    }

    fun updateSpUserCoverImage(request: UpdateSpUserCoverImageRequest): SpUser? {
        val firebaseAuthUser = securityProvider.validateRequest()
        val user = getSpUser(firebaseAuthUser.getUserIdToUse()) ?: error("No user found for userId: ${firebaseAuthUser.getUserIdToUse()}")
        val newUserToBeSaved = user.copy(coverImage = request.coverImage.convertToString())
        return saveSpUser(newUserToBeSaved, ProcessingType.NO_PROCESSING)
    }

    fun updateSpUserName(request: UpdateSpUserNameRequest): SpUser? {
        val firebaseAuthUser = securityProvider.validateRequest()
        val user = getSpUser(firebaseAuthUser.getUserIdToUse()) ?: error("No user found for userId: ${firebaseAuthUser.getUserIdToUse()}")
        val newUserToBeSaved = user.copy(fullName = request.newName)
        return saveSpUser(newUserToBeSaved, ProcessingType.NO_PROCESSING)
    }

    fun updateSpUserEmail(request: UpdateSpUserEmailRequest): SpUser? {
        val firebaseAuthUser = securityProvider.validateRequest()
        val user = getSpUser(firebaseAuthUser.getUserIdToUse()) ?: error("No user found for userId: ${firebaseAuthUser.getUserIdToUse()}")
        val newUserToBeSaved = user.copy(email = request.newEmail)
        return saveSpUser(newUserToBeSaved, ProcessingType.NO_PROCESSING)
    }

    fun updateContactVisibility(request: UpdateSpUserContactVisibilityRequest): SpUser? {
        val firebaseAuthUser = securityProvider.validateRequest()
        return updateContactVisibility(firebaseAuthUser.getUserIdToUse(), request.contactVisible)
    }

    fun toggleContactVisibility(): SpUser? {
        val firebaseAuthUser = securityProvider.validateRequest()
        val user = getSpUser(firebaseAuthUser.getUserIdToUse()) ?: error("No user found for userId: ${firebaseAuthUser.getUserIdToUse()}")
        // If user.contactVisible is null then assume it was true
        val currentVisibility = user.contactVisible ?: true
        // Set it to opposite of current value
        return updateContactVisibility(user, currentVisibility.not())
    }

    // Make this private after initial data is filled
    fun updateContactVisibility(userId: String, contactVisible: Boolean): SpUser? {
        val user = getSpUser(userId) ?: error("No user found for userId: $userId")
        return updateContactVisibility(user, contactVisible)
    }

    private fun updateContactVisibility(user: SpUser, contactVisible: Boolean): SpUser? {
        val newUserToBeSaved = user.copy(contactVisible = contactVisible)
        return saveSpUser(newUserToBeSaved, ProcessingType.NO_PROCESSING)
    }

    fun removeSpUserDP(): SpUser? {
        val firebaseAuthUser = securityProvider.validateRequest()
        val user = getSpUser(firebaseAuthUser.getUserIdToUse()) ?: error("No user found for userId: ${firebaseAuthUser.getUserIdToUse()}")
        val newUserToBeSaved = user.copy(dp = null)
        return saveSpUser(newUserToBeSaved, ProcessingType.NO_PROCESSING)
    }

    fun getLoggedInSpUser(): SpUser? {
        val firebaseAuthUser = securityProvider.validateRequest()
        return getSpUser(firebaseAuthUser.getUserIdToUse())
    }

    fun saveSpUserWhoJustLoggedIn(): SpUser? {
        val firebaseAuthUser = securityProvider.validateRequest()
        val existing = getSpUser(firebaseAuthUser.getUserIdToUse())
        if (existing != null) {
            return existing
        }
        return saveSpUser(getSpUserObjectFromFirebaseObject(firebaseAuthUser))
    }

    fun getAWSLambdaAuthDetails(): AWSLambdaAuthResponse? {
        val userDetails = securityProvider.validateRequest()
        userDetails.getUserIdToUse()
        val userV2 = getSpUser(userDetails.getUserIdToUse()) ?: error("No user found with userId: ${userDetails.getUserIdToUse()}")
        return AWSLambdaAuthResponse(
            userId = userV2.userId,
            anonymous = userV2.anonymous
        )
    }

    private fun getSpUserObjectFromFirebaseObject(firebaseAuthUser: UserDetailsFromToken): SpUser {
        var userName = firebaseAuthUser.getName()
        if (userName == null || userName.isNullOrBlank()) {
            userName = "Guest User"
        }
        return SpUser(
            userId = firebaseAuthUser.getUserIdToUse(),
            createdAt = DateUtils.getInstantNow(),
            absoluteMobile = firebaseAuthUser.getAbsoluteMobileNumber(),
            countryCode = "",
            handle = "",
            email = firebaseAuthUser.getEmail(),
            dp = firebaseAuthUser.getPicture()?.let {
                MediaDetailsV2(
                    listOf(
                        SingleMediaDetail(
                            mediaUrl = it,
                            mediaType = MediaType.IMAGE,
                        )
                    )
                ).convertToString()
            },
            uid = firebaseAuthUser.getUid(),
            anonymous = firebaseAuthUser.getIsAnonymous() == true,
            verified = false,
            fullName = userName,
            notificationToken = null,
            notificationTokenProvider = NotificationTokenProvider.FIREBASE,
            atLeastOnceLoggedIn = true,
        )
    }

    fun isUserHandleAvailable(handle: String): Boolean {
        return spUsersByHandleProvider.isHandleAvailable(handle)
    }

    fun updateSpUserDuringSignup(request: UpdateSpUserDuringSignupRequest): SpUser? {
        val firebaseAuthUser = securityProvider.validateRequest()
        val user = getSpUser(firebaseAuthUser.getUserIdToUse()) ?: error("No user found for userId: ${firebaseAuthUser.getUserIdToUse()}")
        // First just update the name and Profile Image
        // because there is no need for uniqueness enforcement for name and dp
        val newUserToBeSaved = user.copy(
            fullName = request.newName,
            dp = request.dp?.convertToString())

        val nameUpdatedUser = saveSpUser(newUserToBeSaved, ProcessingType.NO_PROCESSING) ?: error("Error while updating name and dp for userId: ${user.userId}")

        // Now update the username as it requires uniqueness enforcement
        // and once, updated, do user level processing by job scheduling
        return updateSpUserHandle(nameUpdatedUser, request.newHandle)
    }

    private fun saveForAuthV2 (user: SpUser) {
        GlobalScope.launch {
            if (user.absoluteMobile.isNullOrBlank().not()) {
                userIdByMobileNumberProvider.saveUserIdByMobileNumber(
                    absoluteMobileNumber = user.absoluteMobile!!,
                    userId = user.userId
                )
            } else {
                logger.warn("absoluteMobile is null so not saving for auth V2")
            }
        }
    }

    private fun saveSpUserToFirestore (user: SpUser) {
        GlobalScope.launch {
            FirestoreClient.getFirestore()
                .collection("sp_users")
                .document(user.userId)
                .collection("users")
                .document(user.userId)
                .set(user.toSavedSpUserResponse())

            FirestoreClient.getFirestore()
                .collection("sp_users")
                .document(user.userId)
                .collection("users_public")
                .document(user.userId)
                .set(user.toSpUserPublicMiniDataResponse())
        }
    }

    fun saveAllToFirestore() {
        spUserRepository.findAll().forEach {
            saveSpUserToFirestore(it!!)
        }
    }

    fun saveAllForAuthV2() {
        spUserRepository.findAll().forEach {
            saveForAuthV2(it!!)
        }
    }

    fun removeSpUserHandle(): SpUser? {
        val firebaseAuthUser = securityProvider.validateRequest()
        val loggedInUserId = firebaseAuthUser.getUserIdToUse()
        val user = getSpUser(loggedInUserId) ?: error("No user found for userId: ${firebaseAuthUser.getUserIdToUse()}")
        val isAdmin = CommonUtils.isAdmin(loggedInUserId)
        if (isAdmin.not()) {
            error("User $loggedInUserId is not authorized to remove username. Only admins can remove the username.")
        }
        val newUserToBeSaved = user.copy(handle = null, fullName = null)
        return saveSpUser(newUserToBeSaved, ProcessingType.NO_PROCESSING)
    }

    fun getUsers(request: GetAllSpUsersRequest): AllSpUsersResponse {
        commonProvider.hardCheckForAdmin()
        val result = getAllSpUserForRequest(request)
        return AllSpUsersResponse(
            users = result.content?.filterNotNull()?.map { it.toSavedSpUserResponse() } ?: emptyList(),
            count = result.count,
            hasNext = result.hasNext,
            pagingState = result.pagingState
        )
    }

    fun getTotalPlatformUsers(): List<SpUser> {
        val limit = 10
        var pagingState = CommonUtils.DEFAULT_PAGING_STATE_VALUE
        val resultUsers = mutableListOf<SpUser>()
        val slicedResult = getAllSpUserForRequest(
            GetAllSpUsersRequest(
            limit = limit,
            pagingState = pagingState,
        ))
        resultUsers.addAll((slicedResult.content?.filterNotNull() ?: emptyList()))
        var hasNext = slicedResult.hasNext == true
        pagingState = slicedResult.pagingState ?: CommonUtils.DEFAULT_PAGING_STATE_VALUE
        while (hasNext) {
            val nextSlicedResult = getAllSpUserForRequest(
                GetAllSpUsersRequest(
                    limit = limit,
                    pagingState = pagingState,
                ))

            hasNext = nextSlicedResult.hasNext == true
            pagingState = nextSlicedResult.pagingState ?: CommonUtils.DEFAULT_PAGING_STATE_VALUE
            resultUsers.addAll((nextSlicedResult.content?.filterNotNull() ?: emptyList()))
        }
        return resultUsers
    }

    private fun getAllSpUserForRequest(request: GetAllSpUsersRequest): CassandraPageV2<SpUser> {
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(request.limit, request.pagingState)
        val users = spUserRepository.findAllBy(pageRequest as Pageable)
        return CassandraPageV2(users)
    }

}
