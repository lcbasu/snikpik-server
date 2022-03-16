package com.server.shop.provider

import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.UniqueIdProvider
import com.server.shop.dao.SaveForLaterRepository
import com.server.shop.entities.CartItemV3
import com.server.shop.entities.SaveForLater
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SaveForLaterProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var saveForLaterRepository: SaveForLaterRepository

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    fun saveSaveForLater(cartItem: CartItemV3): SaveForLater? {
        return try {
            val newProduct = SaveForLater()
            newProduct.id = uniqueIdProvider.getUniqueIdAfterSaving(ReadableIdPrefix.SFL.name)
            newProduct.addedBy = cartItem.addedBy
            newProduct.cartItem = cartItem
            saveForLaterRepository.save(newProduct)
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("saveSaveForLater error", e)
            null
        }
    }

}
