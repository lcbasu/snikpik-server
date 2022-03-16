package com.server.shop.dto

import com.server.common.dto.UserV2PublicMiniDataResponse

data class AllCreatorsResponse (
    val creators: List<UserV2PublicMiniDataResponse>
)

data class TaggedProductCommissionsResponse (
    val commissionPercentageOnProduct: Double,
    val maxCommissionInPaisaOnProduct: Long,

    val unboxMarginInPercentage: Double,
)
