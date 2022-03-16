package com.server.shop.service

import com.server.shop.provider.ShopV3Provider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ShopV3ServiceImpl : ShopV3Service() {

    @Autowired
    private lateinit var shopV3Provider: ShopV3Provider
}
