package com.server.ud.service.faker

import com.server.common.provider.AuthProvider
import com.server.ud.dto.FakerRequest
import com.server.ud.dto.FakerResponse
import com.server.ud.provider.faker.FakerProvider
import com.server.ud.provider.job.JobProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FakerServiceImpl : FakerService() {

    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var fakerProvider: FakerProvider

    @Autowired
    private lateinit var jobProvider: JobProvider

    override fun createFakeData(request: FakerRequest): FakerResponse {
        val requestContext = authProvider.validateRequest()
        val user = requestContext.userV2

        if (user.absoluteMobile != "+919742097429") error("Only Admin is allowed to do this.")

        return FakerResponse(result = fakerProvider.createFakeData(user, request))
    }

    override fun createFakeDataRandomly(): String {
        val requestContext = authProvider.validateRequest()
        val user = requestContext.userV2

        if (user.absoluteMobile != "+919742097429") error("Only Admin is allowed to do this.")
        jobProvider.scheduleFakeDataGeneration()
        return "Job scheduled to generate fake data"
    }
}
