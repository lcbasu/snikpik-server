package com.server.shop.provider

import com.server.shop.dto.NotifyMeForShopCategoryLaunchResponse
import com.server.ud.provider.automation.AutomationProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ShopV3Provider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var automationProvider: AutomationProvider

    @Autowired
    private lateinit var userV3Provider: UserV3Provider

    fun notifyMeForShopCategoryLaunch(): NotifyMeForShopCategoryLaunchResponse? {
        val userV3 = userV3Provider.getUserV3FromLoggedInUser() ?: error("Logged in user not found")
        automationProvider.registerInterestForShopCategoryLaunch(userV3)
        return NotifyMeForShopCategoryLaunchResponse(true)
    }
}
