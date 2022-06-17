package com.server.sp.service.user

import com.server.sp.dto.SavedSpUserResponse
import com.server.sp.entities.user.toSavedSpUserResponse
import com.server.sp.provider.user.SpUserProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SpUserServiceImpl : SpUserService() {

    @Autowired
    private lateinit var spUserProvider: SpUserProvider

    override fun saveLoggedInSpUser(): SavedSpUserResponse? {
        return spUserProvider.saveSpUserWhoJustLoggedIn()?.toSavedSpUserResponse()
    }

}
