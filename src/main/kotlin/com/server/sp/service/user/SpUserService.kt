package com.server.sp.service.user

import com.server.sp.dto.SavedSpUserResponse

abstract class SpUserService {
    abstract fun saveLoggedInSpUser(): SavedSpUserResponse?
}
