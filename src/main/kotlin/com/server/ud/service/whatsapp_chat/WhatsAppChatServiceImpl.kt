package com.server.ud.service.whatsapp_chat

import com.server.common.dto.AllProfileTypeResponse
import com.server.common.dto.UpdateUserV2PreferredCategoriesRequest
import com.server.common.enums.ProfileCategory
import com.server.common.provider.SecurityProvider
import com.server.common.dto.*
import com.server.dk.dto.AllUserReportResponse
import com.server.dk.dto.UserReportRequest
import com.server.dk.dto.UserReportResponse
import com.server.ud.dto.*
import com.server.ud.entities.post.toLikedPostsByUserPostDetail
import com.server.ud.entities.social.FollowersCountByUser
import com.server.ud.entities.social.FollowingsCountByUser
import com.server.ud.entities.user.toProfilePageUserDetailsResponse
import com.server.ud.entities.user.toSavedUserV2Response
import com.server.ud.entities.user.toUserV2PublicMiniDataResponse
import com.server.ud.provider.post.BookmarkedPostsByUserProvider
import com.server.ud.provider.post.LikedPostsByUserProvider
import com.server.ud.provider.post.PostsByUserProvider
import com.server.ud.provider.social.FollowersCountByUserProvider
import com.server.ud.provider.social.FollowingsCountByUserProvider
import com.server.ud.provider.user.UserV2Provider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class WhatsAppChatServiceImpl : WhatsAppChatService() {

    @Autowired
    private lateinit var userV2Provider: UserV2Provider

    @Autowired
    private lateinit var postsByUserProvider: PostsByUserProvider

    @Autowired
    private lateinit var likedPostsByUserProvider: LikedPostsByUserProvider

    @Autowired
    private lateinit var bookmarkedPostsByUserProvider: BookmarkedPostsByUserProvider

    @Autowired
    private lateinit var followersCountByUserProvider: FollowersCountByUserProvider

    @Autowired
    private lateinit var followingsCountByUserProvider: FollowingsCountByUserProvider

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    override fun saveWhatsAppNumberDetails(request: SaveWhatsAppNumberDetailRequest?): SavedWhatsAppNumberDetailResponse? {
        TODO("Not yet implemented")
    }

    override fun getAllWhatsAppNumbersDetails(): AllWhatsAppNumbersDetailsResponse? {
        TODO("Not yet implemented")
    }

    override fun saveWhatsAppChatTracker(request: SaveWhatsAppChatTrackerRequest?): SavedWhatsAppChatTrackerResponse? {
        TODO("Not yet implemented")
    }

}
