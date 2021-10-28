package com.server.common.service

import com.server.common.model.Credentials
import com.server.common.model.FirebaseAuthUser
import javax.servlet.http.HttpServletRequest

open class SecurityService {
    open fun user(): FirebaseAuthUser? = null
    open fun credentials(): Credentials? = null
    open fun isPublic() = false
    open fun getBearerToken(request: HttpServletRequest?): String = ""
}
