package com.server.dk.controller

import com.server.dk.dto.*
import com.server.dk.service.ProductService
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

    @RequestMapping(value = ["/update"], method = [RequestMethod.POST])
    fun updateProduct(@RequestBody request: UpdateProductRequest): SavedProductResponse? {
        return productService.updateProduct(request)
    }

    @RequestMapping(value = ["/addProductsToCollection"], method = [RequestMethod.POST])
    fun addProductsToCollection(@RequestBody addProductsToCollectionRequest: AddProductsToCollectionRequest): AddProductsToCollectionResponse? {
        return productService.addProductsToCollection(addProductsToCollectionRequest)
    }

    @RequestMapping(value = ["/updateStatus"], method = [RequestMethod.POST])
    fun updateStatus(@RequestBody updateProductStatusRequest: UpdateProductStatusRequest): SavedProductResponse? {
        return productService.updateStatus(updateProductStatusRequest)
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
