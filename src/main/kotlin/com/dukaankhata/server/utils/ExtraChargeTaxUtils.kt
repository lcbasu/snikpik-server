package com.dukaankhata.server.utils

import com.dukaankhata.server.dao.ExtraChargeTaxRepository
import com.dukaankhata.server.dto.SaveExtraChargeTaxRequest
import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.ExtraChargeTax
import com.dukaankhata.server.entities.ExtraChargeTaxKey
import com.dukaankhata.server.entities.User
import com.dukaankhata.server.enums.TaxType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ExtraChargeTaxUtils {

    val maxTaxPercentage = 50

    @Autowired
    private lateinit var extraChargeTaxRepository: ExtraChargeTaxRepository

    fun getExtraChargeTaxKey(companyId: String, taxType: TaxType): ExtraChargeTaxKey {
        val key = ExtraChargeTaxKey()
        key.companyId = companyId
        key.taxType = taxType
        return key
    }

    fun getExtraChargeTaxes(company: Company): List<ExtraChargeTax> =
        try {
            extraChargeTaxRepository.findAllByCompany(company)
        } catch (e: Exception) {
            emptyList()
        }

    fun getExtraChargeTax(company: Company, taxType: TaxType): ExtraChargeTax? =
        try {
            extraChargeTaxRepository.findById(getExtraChargeTaxKey(company.id, taxType)).get()
        } catch (e: Exception) {
            null
        }

    fun saveOrUpdateExtraChargeTax(addedBy: User, company: Company, saveExtraChargeTaxRequest: SaveExtraChargeTaxRequest) : ExtraChargeTax {
        val key = getExtraChargeTaxKey(company.id, saveExtraChargeTaxRequest.taxType)
        val extraChargeTaxOptional = extraChargeTaxRepository.findById(key)
        if (saveExtraChargeTaxRequest.taxPercentage > maxTaxPercentage) {
            error("You can not apply more than $maxTaxPercentage% tax")
        }
        return if (extraChargeTaxOptional.isPresent) {
            val extraChargeTax = extraChargeTaxOptional.get()
            extraChargeTax.addedBy = addedBy
            extraChargeTax.taxPercentage = saveExtraChargeTaxRequest.taxPercentage
            extraChargeTax.lastModifiedAt = DateUtils.dateTimeNow()
            extraChargeTaxRepository.save(extraChargeTax)
        } else {
            val extraChargeTax = ExtraChargeTax()
            extraChargeTax.id = key
            extraChargeTax.taxPercentage = saveExtraChargeTaxRequest.taxPercentage
            extraChargeTax.company = company
            extraChargeTax.addedBy = addedBy
            extraChargeTaxRepository.save(extraChargeTax)
        }
    }

}
