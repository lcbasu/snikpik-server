package com.server.ud.service.category

import com.server.common.dto.AllCategoryV2Response
import com.server.ud.enums.CategoryGroupV2

abstract class CategoryV2Service {
    abstract fun getAllCategories(categoryGroup: CategoryGroupV2): AllCategoryV2Response
}
