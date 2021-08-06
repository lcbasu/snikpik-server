package com.dukaankhata.server.controller

import com.dukaankhata.server.dto.*
import com.dukaankhata.server.service.ProductService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("product")
class ProductController {
    @Autowired
    private lateinit var productService: ProductService

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveProduct(@RequestBody saveProductRequest: SaveProductRequest): SavedProductResponse? {
        return productService.saveProduct(saveProductRequest)
    }

    @RequestMapping(value = ["/addProductsToCollection"], method = [RequestMethod.POST])
    fun addProductsToCollection(@RequestBody addProductsToCollectionRequest: AddProductsToCollectionRequest): AddProductsToCollectionResponse? {
        return productService.addProductsToCollection(addProductsToCollectionRequest)
    }

    @RequestMapping(value = ["/getAllProducts/{companyId}"], method = [RequestMethod.GET])
    fun getAllProducts(@PathVariable companyId: String): AllProductsResponse {
        return productService.getAllProducts(companyId)
    }

    @RequestMapping(value = ["/getAllProductUnits"], method = [RequestMethod.GET])
    fun getAllProductUnits(): AllProductUnits {
        return productService.getAllProductUnits()
    }
}
