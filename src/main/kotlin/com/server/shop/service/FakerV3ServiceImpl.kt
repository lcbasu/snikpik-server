package com.server.shop.service

import com.server.shop.provider.FakerV3Provider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FakerV3ServiceImpl : FakerV3Service() {

    @Autowired
    private lateinit var fakerV3ProductV3: FakerV3Provider

    override fun generateFakeShopData(): Any {
        return fakerV3ProductV3.generateFakeShopData()
    }

    override fun doSomething(): Any {
        return fakerV3ProductV3.doSomething()
    }
}
