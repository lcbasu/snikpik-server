package com.server.common.service

import com.server.common.dto.ApplicableExperimentsResponse
import com.server.common.provider.AuthProvider
import com.server.common.provider.ExperimentProvider
import com.server.common.provider.SecurityProvider
import com.server.common.provider.UserRoleProvider
import com.server.dk.provider.AddressProvider
import com.server.ud.provider.user.UserV2Provider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ExperimentServiceImpl : ExperimentService() {

    @Autowired
    private lateinit var experimentProvider: ExperimentProvider

    override fun getApplicableExperiments(): ApplicableExperimentsResponse? {
        return experimentProvider.getApplicableExperiments()
    }

}
