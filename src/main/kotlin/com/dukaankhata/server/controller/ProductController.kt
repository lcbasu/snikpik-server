package com.dukaankhata.server.controller

import com.dukaankhata.server.dto.AddProductsToCollectionRequest
import com.dukaankhata.server.dto.AddProductsToCollectionResponse
import com.dukaankhata.server.dto.SaveProductRequest
import com.dukaankhata.server.dto.SavedProductResponse
import com.dukaankhata.server.service.ProductService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

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
}
