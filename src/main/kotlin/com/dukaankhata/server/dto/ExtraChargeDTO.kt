package com.dukaankhata.server.dto

import com.dukaankhata.server.entities.ExtraChargeDelivery
import com.dukaankhata.server.entities.ExtraChargeTax
import com.dukaankhata.server.enums.DeliveryType
import com.dukaankhata.server.enums.ExtraChargeType
import com.dukaankhata.server.enums.TaxType
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
open class ExtraChargeResponse(
    val type: ExtraChargeType
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveExtraChargeDeliveryRequest(
    val companyId: String,
    val deliveryType: DeliveryType,
    val deliveryChargePerOrderInPaisa: Long,
    val deliveryChargeFreeAboveInPaisa: Long,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedExtraChargeDeliveryResponse(
    val company: SavedCompanyResponse,
    val deliveryType: DeliveryType,
    val deliveryChargePerOrderInPaisa: Long,
    val deliveryChargeFreeAboveInPais: Long,
) : ExtraChargeResponse(ExtraChargeType.DELIVERY)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveExtraChargeTaxRequest(
    val companyId: String,
    val taxType: TaxType,
    val taxPercentage: Double,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedExtraChargeTaxResponse(
    val company: SavedCompanyResponse,
    val taxType: TaxType,
    val taxPercentage: Double,
) : ExtraChargeResponse(ExtraChargeType.TAX)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedExtraChargesResponse(
    val company: SavedCompanyResponse,
    val charges: List<ExtraChargeResponse>
)

fun ExtraChargeDelivery.toSavedExtraChargeDeliveryResponse(): SavedExtraChargeDeliveryResponse {
    this.apply {
        return SavedExtraChargeDeliveryResponse(
            company = company!!.toSavedCompanyResponse(),
            deliveryType = id!!.deliveryType,
            deliveryChargePerOrderInPaisa = deliveryChargePerOrder,
            deliveryChargeFreeAboveInPais = deliveryChargeFreeAbove
        )
    }
}

fun ExtraChargeTax.toSavedExtraChargeTaxResponse(): SavedExtraChargeTaxResponse {
    this.apply {
        return SavedExtraChargeTaxResponse(
            company = company!!.toSavedCompanyResponse(),
            taxType = id!!.taxType,
            taxPercentage = taxPercentage,
        )
    }
}

