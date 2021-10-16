package com.server.dk.service

import com.server.dk.dto.SaveOvertimeRequest
import com.server.dk.dto.SavedOvertimeResponse

abstract class OvertimeService {
    abstract fun saveOvertime(saveOvertimeRequest: SaveOvertimeRequest): SavedOvertimeResponse?
}
