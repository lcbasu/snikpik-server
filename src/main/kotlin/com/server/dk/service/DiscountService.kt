package com.server.dk.service

import com.server.dk.dto.SaveDiscountRequest
import com.server.dk.dto.SavedDiscountResponse

abstract class DiscountService {
    abstract fun saveDiscount(saveDiscountRequest: SaveDiscountRequest): SavedDiscountResponse
}
