package com.server.dk.provider

import com.server.dk.dao.ExtraChargeDeliveryRepository
import com.server.dk.dto.SaveExtraChargeDeliveryRequest
import com.server.dk.entities.Company
import com.server.dk.entities.ExtraChargeDelivery
import com.server.dk.entities.ExtraChargeDeliveryKey
import com.server.dk.entities.User
import com.server.dk.enums.DeliveryType
import com.server.dk.utils.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ExtraChargeDeliveryProvider {

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
