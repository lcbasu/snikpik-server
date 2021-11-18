package com.server.ud.service.faker

import com.server.ud.dto.FakerRequest
import com.server.ud.dto.FakerResponse

abstract class FakerService {
    abstract fun createFakeData(request: FakerRequest): FakerResponse
    abstract fun createFakeDataRandomly(): FakerResponse
}
