package com.server.ud.service.location

import com.server.ud.entities.location.Location

abstract class ProcessLocationSchedulerService {
    abstract fun createLocationProcessingJob(location: Location): Location
}
