package com.server.ud.service.category

import com.server.common.dto.AllCategoryV2Response
import com.server.common.dto.toCategoryV2Response
import com.server.ud.enums.CategoryGroupV2
import com.server.ud.enums.CategoryV2
import org.springframework.stereotype.Service

@Service
class CategoryV2ServiceImpl : CategoryV2Service() {

    override fun getAllCategories(categoryGroup: CategoryGroupV2): AllCategoryV2Response {
        val categories = CategoryV2.values().toList().filter {
            it.categoryGroup == categoryGroup
        }
        return AllCategoryV2Response(
            categories = categories.map { it.toCategoryV2Response() }
        )
    }

}
