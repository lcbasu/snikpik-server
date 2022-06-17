package com.server.common.provider

import com.server.common.dto.ApplicableExperimentsResponse
import com.server.common.utils.ExperimentManager
import com.server.common.utils.CommonUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ExperimentProvider {

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    fun getApplicableExperiments(): ApplicableExperimentsResponse? {
        val loggedInUserId = securityProvider.validateRequest().getUserIdToUse()
        val isAdmin = CommonUtils.isAdmin(loggedInUserId)
        val experiments = ExperimentManager.allApplicableExperimentsMap(loggedInUserId, isAdmin)
        return ApplicableExperimentsResponse(
            userId = loggedInUserId,
            applicableExperiments = experiments.filter { it.value }.map { it.key }.toSet(),
        )
    }

}
