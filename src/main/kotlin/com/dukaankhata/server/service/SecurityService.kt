package com.dukaankhata.server.service

import com.dukaankhata.server.model.Credentials
import com.dukaankhata.server.model.FirebaseAuthUser
import javax.servlet.http.HttpServletRequest

open class SecurityService {
    open fun user(): FirebaseAuthUser? = null
    open fun credentials(): Credentials? = null
    open fun isPublic() = false
    open fun getBearerToken(request: HttpServletRequest?): String = ""
}
