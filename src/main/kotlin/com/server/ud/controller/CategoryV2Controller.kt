package com.server.ud.controller

import com.server.ud.dto.AllCategoryV2Response
import com.server.ud.enums.CategoryGroupV2
import com.server.ud.service.category.CategoryV2Service
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("ud/category")
class CategoryV2Controller {

    @Autowired
    private lateinit var categoryV2Service: CategoryV2Service

    @RequestMapping(value = ["/getAllCategories"], method = [RequestMethod.GET])
    fun getAllCategories(@RequestParam categoryGroup: CategoryGroupV2): AllCategoryV2Response {
        return categoryV2Service.getAllCategories(categoryGroup)
    }
}
