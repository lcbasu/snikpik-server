package com.server.common.service

import com.server.common.model.Credentials
import com.server.common.model.UserDetailsFromToken
import javax.servlet.http.HttpServletRequest

open class SecurityService {
    open fun user(): UserDetailsFromToken? = null
    open fun credentials(): Credentials? = null
    open fun isPublic() = false
    open fun getBearerToken(request: HttpServletRequest?): String = ""
}
