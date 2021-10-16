package com.server.dk.utils

import com.server.dk.entities.ProductVariant

object ProductUtils {
    fun isVariantSame(variant1: ProductVariant, variant2: ProductVariant): Boolean {
        return variant1.product?.id == variant1.product?.id && CommonUtils.getStringWithOnlyCharOrDigit(variant1.title) == CommonUtils.getStringWithOnlyCharOrDigit(variant2.title)
    }
}
