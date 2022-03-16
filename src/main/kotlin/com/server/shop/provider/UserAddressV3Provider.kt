package com.server.shop.provider

import com.server.shop.dao.UserAddressV3Repository
import com.server.shop.entities.AddressV3
import com.server.shop.entities.UserAddressKeyV3
import com.server.shop.entities.UserAddressV3
import com.server.shop.entities.UserV3
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UserAddressV3Provider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)


    @Autowired
    private lateinit var userAddressV3Repository: UserAddressV3Repository

    fun save(user: UserV3, address: AddressV3): UserAddressV3 {
        val key = UserAddressKeyV3()
        key.userId = user.id
        key.addressId = address.id
        val userAddressV3 = UserAddressV3()
        userAddressV3.id = key
        userAddressV3.user = user
        userAddressV3.address = address
        userAddressV3.deleted = address.deleted
        return userAddressV3Repository.save(userAddressV3)
    }

    fun getUserAddresses(userId: String): List<UserAddressV3> {
        return userAddressV3Repository.findAllByUserId(userId).filter { !it.deleted }
    }


}
