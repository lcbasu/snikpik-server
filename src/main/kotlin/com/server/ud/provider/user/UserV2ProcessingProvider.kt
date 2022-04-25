package com.server.ud.provider.user

import com.server.common.enums.ProfileCategory
import com.server.common.enums.ProfileType
import com.server.ud.dto.MarketplaceProfileTypesFeedRequest
import com.server.ud.dto.MarketplaceUserFeedRequest
import com.server.ud.entities.location.Location
import com.server.ud.entities.user.ProfileTypesByNearbyZipcode
import com.server.ud.entities.user.UsersByNearbyZipcodeAndProfileType
import com.server.ud.provider.location.LocationProvider
import com.server.ud.provider.search.SearchProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UserV2ProcessingProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var userV2Provider: UserV2Provider

    @Autowired
    private lateinit var handlesByUserProvider: HandlesByUserProvider

    @Autowired
    private lateinit var usersByProfileCategoryProvider: UsersByProfileCategoryProvider

    @Autowired
    private lateinit var usersByProfileTypeProvider: UsersByProfileTypeProvider

    @Autowired
    private lateinit var usersByZipcodeAndProfileTypeProvider: UsersByZipcodeAndProfileTypeProvider

    @Autowired
    private lateinit var usersByNearbyZipcodeAndProfileTypeProvider: UsersByNearbyZipcodeAndProfileTypeProvider

    @Autowired
    private lateinit var profileTypesByNearbyZipcodeProvider: ProfileTypesByNearbyZipcodeProvider

    @Autowired
    private lateinit var profileTypesByZipcodeAndProfileCategoryProvider: ProfileTypesByZipcodeAndProfileCategoryProvider

    @Autowired
    private lateinit var usersByZipcodeProvider: UsersByZipcodeProvider

    @Autowired
    private lateinit var locationProvider: LocationProvider

    @Autowired
    private lateinit var searchProvider: SearchProvider

    fun processUserV2(userId: String) {
        GlobalScope.launch {
            logger.info("Start: UserV2 processing for userId: $userId")

            val user = userV2Provider.getUser(userId) ?: error("No user found for $userId while doing user processing.")

            val handlesByUserFuture = async {
                handlesByUserProvider.save(user)
            }

            val usersByProfileCategoryFuture = async {
                usersByProfileCategoryProvider.save(user)
            }

            val usersByProfileTypeFuture = async {
                usersByProfileTypeProvider.save(user)
            }

            val usersByZipcodeAndProfileFuture = async {
                usersByZipcodeAndProfileTypeProvider.save(user)
            }

            val usersNearbyTasksFuture = async {
                if (user.permanentLocationLat != null &&
                    user.permanentLocationLng != null &&
                    user.permanentLocationZipcode != null) {
                    val nearbyZipcodes = locationProvider.getNearbyZipcodes(
                        lat = user.permanentLocationLat,
                        lng = user.permanentLocationLng,
                        originalZipcode = user.permanentLocationZipcode
                    )
                    profileTypesByNearbyZipcodeProvider.save(user, nearbyZipcodes)
                    usersByNearbyZipcodeAndProfileTypeProvider.save(user, nearbyZipcodes)
                }
            }

            val profileTypesByZipcodeAndProfileCategoryProviderFuture = async {
                profileTypesByZipcodeAndProfileCategoryProvider.save(user)
            }

            val usersByZipcodeFuture = async {
                usersByZipcodeProvider.save(user)
            }

            val algoliaIndexingFuture = async {
                searchProvider.doSearchProcessingForUser(user)
            }

            handlesByUserFuture.await()
            usersByProfileCategoryFuture.await()
            usersByProfileTypeFuture.await()
            usersByZipcodeAndProfileFuture.await()
            profileTypesByZipcodeAndProfileCategoryProviderFuture.await()
            usersByZipcodeFuture.await()
            usersNearbyTasksFuture.await()
            algoliaIndexingFuture.await()

            logger.info("Done: UserV2 processing for userId: $userId")
        }
    }

    fun reProcessUserV2(userId: String) {
        GlobalScope.launch {
            logger.info("Start: Delete user data for dependent information for userId: $userId")

            // Delete the older data
            usersByNearbyZipcodeAndProfileTypeProvider.delete(userId)
            usersByProfileCategoryProvider.delete(userId)
            usersByProfileTypeProvider.delete(userId)
            usersByZipcodeAndProfileTypeProvider.delete(userId)
            usersByZipcodeProvider.delete(userId)

            // Now Re-Process the user
            processUserV2(userId)

            logger.info("End: Delete user data for dependent information for userId: $userId")
        }
    }

    fun processUserDataForNewNearbyLocation(originalLocation: Location, nearbyZipcodes: Set<String>) {
        GlobalScope.launch {
            processUserForNearbyLocation(originalLocation, nearbyZipcodes)
            processProfileTypesForNearbyLocation(originalLocation, nearbyZipcodes)
        }
    }

    private fun processUserForNearbyLocation(originalLocation: Location, nearbyZipcodes: Set<String>) {
        GlobalScope.launch {
            logger.info("Start: processUserForNearbyLocation for locationId: ${originalLocation.locationId}")
            if (originalLocation.zipcode == null) {
                logger.error("Location ${originalLocation.name} does not have zipcode. Hence skipping processUserForNearbyLocation")
                return@launch
            }

            val usersPerProfileTypeToUse = 50
            val maxSaveListSize = 10

            val nearbyUsers = mutableListOf<UsersByNearbyZipcodeAndProfileType>()
            nearbyZipcodes.map { zipcode ->
                ProfileType.values().map { profileType ->
                    // 50 Users from each profile from each zipcode
                    nearbyUsers.addAll(
                        usersByNearbyZipcodeAndProfileTypeProvider.getFeedForMarketplaceUsers(
                            MarketplaceUserFeedRequest(
                                zipcode = zipcode,
                                profileType = profileType,
                                limit = usersPerProfileTypeToUse,
                            )
                        ).content?.filterNotNull() ?: emptyList()
                    )
                }
            }
            logger.info("Total ${nearbyUsers.size} nearby users found for the current location ${originalLocation.name}. Save in batches of $maxSaveListSize")
            nearbyUsers.chunked(maxSaveListSize).map {
                usersByNearbyZipcodeAndProfileTypeProvider.save(it, originalLocation.zipcode)
            }
            logger.info("Done: processUserForNearbyLocation for locationId: ${originalLocation.locationId}")
        }
    }

    private fun processProfileTypesForNearbyLocation(originalLocation: Location, nearbyZipcodes: Set<String>) {
        GlobalScope.launch {
            logger.info("Start: processProfileTypesForNearbyLocation for locationId: ${originalLocation.locationId}")
            if (originalLocation.zipcode == null) {
                logger.error("Location ${originalLocation.name} does not have zipcode. Hence skipping processProfileTypesForNearbyLocation")
                return@launch
            }

            val profileTypePerCategoryToUse = 100
            val maxSaveListSize = 10

            val nearbyProfileTypes = mutableListOf<ProfileTypesByNearbyZipcode>()
            nearbyZipcodes.map { zipcode ->
                ProfileCategory.values().map { profileCategory ->
                    nearbyProfileTypes.addAll(
                        profileTypesByNearbyZipcodeProvider.getFeedForMarketplaceProfileTypes(
                            MarketplaceProfileTypesFeedRequest(
                                zipcode = zipcode,
                                profileCategory = profileCategory,
                                limit = profileTypePerCategoryToUse,
                            )
                        ).content?.filterNotNull() ?: emptyList()
                    )
                }
            }
            logger.info("Total ${nearbyProfileTypes.size} nearby profile types found for the current location ${originalLocation.name}. Save in batches of $maxSaveListSize")
            nearbyProfileTypes.chunked(maxSaveListSize).map {
                profileTypesByNearbyZipcodeProvider.save(it, originalLocation.zipcode)
            }
            logger.info("Done: processProfileTypesForNearbyLocation for locationId: ${originalLocation.locationId}")
        }
    }

}
