package com.server.dk.service

import com.server.dk.dto.SaveHolidayRequest
import com.server.dk.dto.SavedHolidayResponse

abstract class HolidayService {
    abstract fun saveHoliday(saveHolidayRequest: SaveHolidayRequest): SavedHolidayResponse?
}
