package com.server.shop.controller

import com.server.shop.dto.*
import com.server.shop.service.AddressV3Service
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("shop/address/v3")
class AddressV3Controller {

    @Autowired
    private lateinit var addressV3Service: AddressV3Service

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun save(@RequestBody request: SaveAddressV3Request): SavedAddressV3Response? {
        return addressV3Service.save(request)
    }

    @RequestMapping(value = ["/update"], method = [RequestMethod.POST])
    fun update(@RequestBody request: UpdateAddressV3Request): SavedAddressV3Response? {
        return addressV3Service.update(request)
    }

    @RequestMapping(value = ["/delete"], method = [RequestMethod.POST])
    fun delete(@RequestBody request: DeleteAddressV3Request): SavedAddressV3Response? {
        return addressV3Service.delete(request)
    }

    @RequestMapping(value = ["/get"], method = [RequestMethod.GET])
    fun get(@RequestParam addressId: String): SavedAddressV3Response? {
        return addressV3Service.get(addressId)
    }
}
