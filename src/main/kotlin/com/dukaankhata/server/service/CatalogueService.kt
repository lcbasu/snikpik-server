package com.dukaankhata.server.service

import com.dukaankhata.server.dto.CategoryGroupsResponse

abstract class CatalogueService {
    abstract fun getCategoryGroups(): CategoryGroupsResponse
}
