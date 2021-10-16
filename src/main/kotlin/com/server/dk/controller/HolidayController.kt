package com.server.dk.controller

import com.server.dk.dto.SaveHolidayRequest
import com.server.dk.dto.SavedHolidayResponse
import com.server.dk.service.HolidayService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("holiday")
class HolidayController {
    @Autowired
    private lateinit var holidayService: HolidayService

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveUser(@RequestBody saveHolidayRequest: SaveHolidayRequest): SavedHolidayResponse? {
        return holidayService.saveHoliday(saveHolidayRequest)
    }
}
