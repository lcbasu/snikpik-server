package com.dukaankhata.server.service

import com.dukaankhata.server.dto.SaveDiscountRequest
import com.dukaankhata.server.dto.SavedDiscountResponse

abstract class DiscountService {
    abstract fun saveDiscount(saveDiscountRequest: SaveDiscountRequest): SavedDiscountResponse
}
