package com.dukaankhata.server.service

import com.dukaankhata.server.dto.SaveHolidayRequest
import com.dukaankhata.server.dto.SavedHolidayResponse

abstract class HolidayService {
    abstract fun saveHoliday(saveHolidayRequest: SaveHolidayRequest): SavedHolidayResponse?
}
