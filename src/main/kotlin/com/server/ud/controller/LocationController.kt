package com.server.ud.controller

import com.server.ud.dto.CitiesLocationResponse
import com.server.ud.service.like.LocationService
import io.micrometer.core.annotation.Timed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@Timed
@RequestMapping("ud/location")
class LocationController {

    @Autowired
    private lateinit var locationService: LocationService

    @RequestMapping(value = ["/getCitiesLocationData"], method = [RequestMethod.POST])
    fun getCitiesLocationData(): CitiesLocationResponse {
        return locationService.getCitiesLocationData()
    }
}
