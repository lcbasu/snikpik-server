package com.dukaankhata.server.utils

import com.dukaankhata.server.dao.ExtraChargeDeliveryRepository
import com.dukaankhata.server.dto.SaveExtraChargeDeliveryRequest
import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.ExtraChargeDelivery
import com.dukaankhata.server.entities.ExtraChargeDeliveryKey
import com.dukaankhata.server.entities.User
import com.dukaankhata.server.enums.DeliveryType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ExtraChargeDeliveryUtils {

    @Autowired
    private lateinit var extraChargeDeliveryRepository: ExtraChargeDeliveryRepository

    fun getExtraChargeDeliveryKey(companyId: String, deliveryType: DeliveryType): ExtraChargeDeliveryKey {
        val key = ExtraChargeDeliveryKey()
        key.companyId = companyId
        key.deliveryType = deliveryType
        return key
    }

    fun getExtraChargeDeliveries(company: Company): List<ExtraChargeDelivery> =
        try {
            extraChargeDeliveryRepository.findAllByCompany(company)
        } catch (e: Exception) {
            emptyList()
        }

    fun getExtraChargeDelivery(company: Company, deliveryType: DeliveryType): ExtraChargeDelivery? =
        try {
            extraChargeDeliveryRepository.findById(getExtraChargeDeliveryKey(company.id, deliveryType)).get()
        } catch (e: Exception) {
            null
        }

    fun saveOrUpdateExtraChargeDelivery(addedBy: User, company: Company, saveExtraChargeDeliveryRequest: SaveExtraChargeDeliveryRequest) : ExtraChargeDelivery {
        val key = getExtraChargeDeliveryKey(company.id, saveExtraChargeDeliveryRequest.deliveryType)
        val extraChargeDeliveryOptional = extraChargeDeliveryRepository.findById(key)
        return if (extraChargeDeliveryOptional.isPresent) {
            val extraChargeDelivery = extraChargeDeliveryOptional.get()
            extraChargeDelivery.addedBy = addedBy
            extraChargeDelivery.deliveryChargePerOrder = saveExtraChargeDeliveryRequest.deliveryChargePerOrderInPaisa
            extraChargeDelivery.deliveryChargeFreeAbove = saveExtraChargeDeliveryRequest.deliveryChargeFreeAboveInPaisa
            extraChargeDelivery.lastModifiedAt = DateUtils.dateTimeNow()
            extraChargeDeliveryRepository.save(extraChargeDelivery)
        } else {
            val extraChargeDelivery = ExtraChargeDelivery()
            extraChargeDelivery.id = key
            extraChargeDelivery.deliveryChargePerOrder = saveExtraChargeDeliveryRequest.deliveryChargePerOrderInPaisa
            extraChargeDelivery.deliveryChargeFreeAbove = saveExtraChargeDeliveryRequest.deliveryChargeFreeAboveInPaisa
            extraChargeDelivery.company = company
            extraChargeDelivery.addedBy = addedBy
            extraChargeDeliveryRepository.save(extraChargeDelivery)
        }
    }

}
