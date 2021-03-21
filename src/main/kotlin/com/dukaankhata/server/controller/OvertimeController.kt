package com.dukaankhata.server.controller

import com.dukaankhata.server.dto.SaveOvertimeRequest
import com.dukaankhata.server.dto.SavedOvertimeResponse
import com.dukaankhata.server.service.OvertimeService
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
