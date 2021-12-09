package com.server.ud.service.like

import com.server.ud.dto.CitiesLocationResponse
import com.server.ud.provider.location.LocationProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LocationServiceImpl : LocationService() {

    @Autowired
    private lateinit var locationProvider: LocationProvider

    override fun getCitiesLocationData(): CitiesLocationResponse {
        return locationProvider.getCitiesLocationData()
    }
}
