package com.server.ud.service.like

import com.server.ud.dto.CitiesLocationResponse

abstract class LocationService {
    abstract fun getCitiesLocationData(): CitiesLocationResponse
}
