package com.server.dk.controller

import com.server.dk.dto.SaveLateFineRequest
import com.server.dk.dto.SavedLateFineResponse
import com.server.dk.service.LateFineService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("lateFine")
class LateFineController {
    @Autowired
    private lateinit var lateFineService: LateFineService

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveLateFine(@RequestBody saveLateFineRequest: SaveLateFineRequest): SavedLateFineResponse? {
        return lateFineService.saveLateFine(saveLateFineRequest)
    }
}
