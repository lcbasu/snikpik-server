package com.server.common.service

import com.server.common.dto.ApplicableExperimentsResponse

abstract class ExperimentService {
    abstract fun getApplicableExperiments(): ApplicableExperimentsResponse?
}
