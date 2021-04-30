package com.dukaankhata.server.utils

import com.dukaankhata.server.dao.DiscountRepository
import com.dukaankhata.server.dto.SaveDiscountRequest
import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.Discount
import com.dukaankhata.server.entities.User
import com.dukaankhata.server.enums.ReadableIdPrefix
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class DiscountUtils {

    @Autowired
    private lateinit var discountRepository: DiscountRepository

    @Autowired
    private lateinit var uniqueIdGeneratorUtils: UniqueIdGeneratorUtils

    fun getDiscount(discountId: String): Discount? =
        try {
            discountRepository.findById(discountId).get()
        } catch (e: Exception) {
            null
        }

    fun saveDiscount(addedBy: User, company: Company, saveDiscountRequest: SaveDiscountRequest): Discount {
        val newDiscount = Discount()
        newDiscount.id = uniqueIdGeneratorUtils.getUniqueId(ReadableIdPrefix.DCT.name)
        newDiscount.promoCode = saveDiscountRequest.promoCode
        newDiscount.discountType = saveDiscountRequest.discountType
        newDiscount.discountAmount = saveDiscountRequest.discountAmount
        newDiscount.minOrderValueInPaisa = saveDiscountRequest.minOrderValueInPaisa ?: 0
        newDiscount.maxDiscountAmountInPaisa = saveDiscountRequest.maxDiscountAmountInPaisa ?: 0
        newDiscount.sameCustomerCount = saveDiscountRequest.sameCustomerCount
        newDiscount.visibleToCustomer = saveDiscountRequest.visibleToCustomer
        newDiscount.startAt = DateUtils.parseEpochInMilliseconds(saveDiscountRequest.startAt)
        newDiscount.endAt = DateUtils.parseEpochInMilliseconds(saveDiscountRequest.endAt)
        newDiscount.company = company
        newDiscount.addedBy = addedBy
        return discountRepository.save(newDiscount)
    }

    /**
     *
     * We can only disable discounts. We can NEVER delete a discount.
     * A someone might have already made use of it
     *
     * */
    fun disableDiscount(discountId: String): Discount? {
        val discount = getDiscount(discountId) ?: error("Discount not found for id: $discountId")
        discount.endAt = DateUtils.dateTimeNow()
        return discountRepository.save(discount)

    }

    fun getActiveDiscounts(company: Company): List<Discount> {
        return discountRepository.getActiveDiscounts(companyId = company.id)
    }
}
