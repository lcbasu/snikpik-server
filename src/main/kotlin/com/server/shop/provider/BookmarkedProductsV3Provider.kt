package com.server.shop.provider

import com.server.common.enums.ReadableIdPrefix
import com.server.common.utils.CommonUtils
import com.server.shop.dao.BookmarkedProductsV3Repository
import com.server.shop.dto.AllBookmarkedProductVariantsRequest
import com.server.shop.dto.AllBookmarkedProductVariantsResponse
import com.server.shop.dto.toAllBookmarkedProductVariantsResponse
import com.server.shop.entities.BookmarkedProductsV3
import com.server.shop.entities.ProductVariantV3
import com.server.shop.entities.UserV3
import com.server.shop.pagination.SQLSlice
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class BookmarkedProductsV3Provider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var bookmarkedProductsV3Repository: BookmarkedProductsV3Repository

    @Autowired
    private lateinit var userV3Provider: UserV3Provider

    fun saveBookmark(bookmarked: Boolean, user: UserV3, productVariantV3: ProductVariantV3): BookmarkedProductsV3? {
        return try {
            val newBookmark = BookmarkedProductsV3()
            newBookmark.id = getId(user, productVariantV3)
            newBookmark.bookmarked = bookmarked
            newBookmark.productVariant = productVariantV3
            newBookmark.product = productVariantV3.product
            newBookmark.addedBy = user
            bookmarkedProductsV3Repository.save(newBookmark)
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("saveBookmark error", e)
            null
        }
    }

    fun getBookmark(user: UserV3, productVariantV3: ProductVariantV3): BookmarkedProductsV3? {
        return try {
            bookmarkedProductsV3Repository.findById(getId(user, productVariantV3)).orElse(null)
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("getBookmark error", e)
            null
        }
    }

    fun updateBookmark(user: UserV3, productVariantV3: ProductVariantV3): BookmarkedProductsV3? {
        return try {
            val existing = getBookmark(user, productVariantV3)
            if (existing == null) {
                saveBookmark(true, user, productVariantV3)
            } else {
                existing.bookmarked = !existing.bookmarked
                bookmarkedProductsV3Repository.save(existing)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("saveBookmark error", e)
            null
        }
    }

    fun getId(user: UserV3, productVariantV3: ProductVariantV3) =
        "${ReadableIdPrefix.PBK.name}${user.id}${CommonUtils.STRING_SEPARATOR}${productVariantV3.id}"

//    fun getAllBookmarks(user: UserV3): List<BookmarkedProductsV3> {
//        return try {
//            bookmarkedProductsV3Repository.findAllByAddedBy(user)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            logger.error("getBookmarks error", e)
//            emptyList()
//        }
//    }

//    fun getValidBookmarks(user: UserV3): List<BookmarkedProductsV3> {
//        return getAllBookmarks(user).filter { it.bookmarked }
//    }

    fun getAllBookmarkedProductVariants(request: AllBookmarkedProductVariantsRequest): AllBookmarkedProductVariantsResponse {
        val userV3 = userV3Provider.getUserV3(request.userId) ?: error("Logged in user not found")
        val pageable = PageRequest.of(request.page, request.limit)
        return SQLSlice(
            bookmarkedProductsV3Repository.findAllByAddedByAndBookmarked(
                userV3,
                true,
                pageable
            )
        ).toAllBookmarkedProductVariantsResponse()
    }


}
