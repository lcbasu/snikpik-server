package com.server.ud.controller

import com.server.common.provider.SecurityProvider
import io.ably.lib.rest.AblyRest
import io.ably.lib.rest.Auth
import io.ably.lib.types.AblyException
import io.ably.lib.types.Capability
import io.micrometer.core.annotation.Timed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@Timed
@RequestMapping("external/ably")
class AblyController {

    @Autowired
    private lateinit var ablyRest: AblyRest

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    @Throws(AblyException::class)
    open fun setAblyRest(apiKey: String) {
        ablyRest = AblyRest(apiKey)
    }

    /* Issue token requests to clients sending a request to the /auth endpoint */
    @RequestMapping("/auth")
    @Throws(AblyException::class)
    fun auth(request: HttpServletRequest, response: HttpServletResponse): String? {
        val username: String = securityProvider.validateRequest().getUserIdToUse()
        val tokenParams: Auth.TokenParams = getTokenParams(username)
        return createTokenRequest(tokenParams, response)
    }

    @Throws(AblyException::class)
    fun getTokenParams(username: String?): Auth.TokenParams {
        val tokenParams = Auth.TokenParams()
        tokenParams.capability = Capability.c14n("{ '*': ['subscribe'] }")
        if (username != null) {
            tokenParams.clientId = username
        }
        return tokenParams
    }

    fun createTokenRequest(tokenParams: Auth.TokenParams?, response: HttpServletResponse): String? {
        val tokenRequest: Auth.TokenRequest
        return try {
            tokenRequest = ablyRest.auth.createTokenRequest(tokenParams, null)
            response.setHeader("Content-Type", "application/json")
            tokenRequest.asJson()
        } catch (e: AblyException) {
            response.status = 500
            "Error requesting token: " + e.message
        }
    }
}
