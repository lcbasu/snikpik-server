package com.server.ud.provider.marketplace

import com.server.common.dto.ProfileTypeWithUsersResponse
import com.server.common.dto.toProfileTypeResponse
import com.server.ud.dto.*
import com.server.ud.provider.user.ProfileTypesByNearbyZipcodeProvider
import com.server.ud.provider.user.UserV2Provider
import com.server.ud.provider.user.UsersByNearbyZipcodeAndProfileTypeProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MarketplaceProvider {

    private val usersPerProfileTypes = 2

    @Autowired
    private lateinit var usersByNearbyZipcodeAndProfileTypeProvider: UsersByNearbyZipcodeAndProfileTypeProvider

    @Autowired
    private lateinit var profileTypesByNearbyZipcodeProvider: ProfileTypesByNearbyZipcodeProvider

    @Autowired
    private lateinit var userV2Provider: UserV2Provider

    fun getFeedForMarketplaceUsersV2(request: MarketplaceUsersFeedRequestV2): MarketplaceUsersFeedResponseV2 {
        val resultForProfileTypes = profileTypesByNearbyZipcodeProvider.getFeedForMarketplaceProfileTypes(
            MarketplaceProfileTypesFeedRequest(
                request.zipcode,
                request.profileCategory,
                request.limit,
                request.pagingState
            )
        )
        val result = resultForProfileTypes.content?.filterNotNull()?.map {
            val users = usersByNearbyZipcodeAndProfileTypeProvider.getFeedForMarketplaceUsers(
                MarketplaceUserFeedRequest(
                    request.zipcode,
                    it.profileType,
                    usersPerProfileTypes,
                    null
                )
            )
            ProfileTypeWithUsersResponse(
                profileTypeToShow = it.profileType.toProfileTypeResponse(),
                users = (users.content?.filterNotNull()?.mapNotNull { it.toMarketplaceUserDetail(userV2Provider) } ?: emptyList())
            )
        } ?: emptyList()
        return MarketplaceUsersFeedResponseV2(
            users = result,
            count = resultForProfileTypes.count,
            hasNext = resultForProfileTypes.hasNext,
            pagingState = resultForProfileTypes.pagingState
        )
    }
}
