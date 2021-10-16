package com.server.dk.service

import com.server.dk.dto.CategoryGroupsResponse

abstract class CatalogueService {
    abstract fun getCategoryGroups(): CategoryGroupsResponse
}
