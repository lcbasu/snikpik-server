package com.server.shop.provider

import com.server.shop.dao.ProductTaggedProductsRepository
import com.server.shop.dto.PostTaggedProductsRequest
import com.server.shop.entities.PostTaggedProduct
import com.server.shop.entities.PostTaggedProductKey
import com.server.shop.pagination.SQLSlice
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class PostTaggedProductsProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var postTaggedProductsRepository: ProductTaggedProductsRepository

    @Autowired
    private lateinit var productV3Provider: ProductV3Provider

    fun saveTaggedProduct(postId: String, productIds: Set<String>): List<PostTaggedProduct> {
        if (productIds.isEmpty()) {
            logger.info("No product Ids to save for post $postId")
            return emptyList()
        }
        val products = productIds.mapNotNull { productV3Provider.getProduct(it) }
        val postTaggedProducts = mutableListOf<PostTaggedProduct>()
        products.map {
            val key = PostTaggedProductKey()
            key.productId = it.id
            key.postId = postId

            val postTaggedProduct = PostTaggedProduct()
            postTaggedProduct.id = key
            postTaggedProduct.product = it
            postTaggedProduct.postIdOpen = postId
            postTaggedProducts.add(postTaggedProduct)
        }
        return if (postTaggedProducts.isNotEmpty()) {
            postTaggedProductsRepository.saveAll(postTaggedProducts)
        } else {
            logger.error("Error occured. No products to save for post $postId and productIds $productIds")
            emptyList()
        }
    }

    fun getPostTaggedProducts(request: PostTaggedProductsRequest): SQLSlice<PostTaggedProduct> {
        val pageable = PageRequest.of(request.page, request.limit)
        return SQLSlice(postTaggedProductsRepository.findAllByPostIdOpen(request.postId, pageable))
    }
}
