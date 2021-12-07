package com.server.ud.service.faker

import com.server.common.provider.SecurityProvider
import com.server.ud.dto.FakerRequest
import com.server.ud.dto.FakerResponse
import com.server.ud.provider.deferred.DeferredProcessingProvider
import com.server.ud.provider.faker.FakerProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FakerServiceImpl : FakerService() {

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    @Autowired
    private lateinit var fakerProvider: FakerProvider

    @Autowired
    private lateinit var deferredProcessingProvider: DeferredProcessingProvider

    override fun createFakeData(request: FakerRequest): FakerResponse {
        val userDetailsFromToken = securityProvider.validateRequest()
//        val user = requestContext.userV2

//        if (user.absoluteMobile != "+919742097429") error("Only Admin is allowed to do this.")

        return FakerResponse(result = fakerProvider.createFakeData(userDetailsFromToken.getUserIdToUse(), request))
    }

    override fun createFakeDataRandomly(): String {
        val userDetailsFromToken = securityProvider.validateRequest()
//        val user = requestContext.userV2

//        if (user.absoluteMobile != "+919742097429") error("Only Admin is allowed to do this.")
        deferredProcessingProvider.deferFakeDataGeneration()
        return "Job scheduled to generate fake data"
    }

    override fun doSomething(): Any {
        return fakerProvider.doSomething()
    }
}
