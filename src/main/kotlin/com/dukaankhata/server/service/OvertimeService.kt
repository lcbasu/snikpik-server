package com.dukaankhata.server.service

import com.dukaankhata.server.dto.SaveOvertimeRequest
import com.dukaankhata.server.dto.SavedOvertimeResponse

abstract class OvertimeService {
    abstract fun saveOvertime(saveOvertimeRequest: SaveOvertimeRequest): SavedOvertimeResponse?
}
