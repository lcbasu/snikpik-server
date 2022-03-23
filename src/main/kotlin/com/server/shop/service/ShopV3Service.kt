package com.server.shop.service

import com.server.shop.dto.NotifyMeForShopCategoryLaunchResponse

abstract class ShopV3Service {
    abstract fun notifyMeForShopCategoryLaunch(): NotifyMeForShopCategoryLaunchResponse?
}
