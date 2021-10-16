package com.server.dk.provider

import com.server.dk.dao.DiscountRepository
import com.server.dk.dto.SaveDiscountRequest
import com.server.dk.entities.Company
import com.server.dk.entities.Discount
import com.server.dk.entities.User
import com.server.dk.enums.ReadableIdPrefix
import com.server.dk.utils.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class DiscountProvider {

    @Autowired
    private lateinit var discountRepository: DiscountRepository

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    fun getDiscount(discountId: String): Discount? =
        try {
            discountRepository.findById(discountId).get()
        } catch (e: Exception) {
            null
        }

    fun saveDiscount(addedBy: User, company: Company, saveDiscountRequest: SaveDiscountRequest): Discount {
        val newDiscount = Discount()
        newDiscount.id = uniqueIdProvider.getUniqueId(ReadableIdPrefix.DCT.name)
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
