package com.dukaankhata.server.controller

import com.dukaankhata.server.dto.SaveAddressRequest
import com.dukaankhata.server.dto.SavedAddressResponse
import com.dukaankhata.server.service.AddressService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("address")
class AddressController {

    @Autowired
    private lateinit var addressService: AddressService

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveAttendance(@RequestBody saveAddressRequest: SaveAddressRequest): SavedAddressResponse {
        return addressService.save(saveAddressRequest)
    }
}
