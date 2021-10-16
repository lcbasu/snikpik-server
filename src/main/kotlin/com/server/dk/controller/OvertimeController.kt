package com.server.dk.controller

import com.server.dk.dto.SaveOvertimeRequest
import com.server.dk.dto.SavedOvertimeResponse
import com.server.dk.service.OvertimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("overtime")
class OvertimeController {
    @Autowired
    private lateinit var overtimeService: OvertimeService

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveUser(@RequestBody saveOvertimeRequest: SaveOvertimeRequest): SavedOvertimeResponse? {
        return overtimeService.saveOvertime(saveOvertimeRequest)
    }
}
