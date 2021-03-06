package com.server.dk.dao

import com.server.dk.entities.ProductCollection
import com.server.dk.entities.ProductCollectionKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository


@Repository
interface ProductCollectionRepository : JpaRepository<ProductCollection?, ProductCollectionKey?> {
    @Query(value ="SELECT * FROM product_collection WHERE collection_id IN :collectionIds or product_id IN :productIds", nativeQuery = true)
    fun getProductCollections(
        @Param("collectionIds") collectionIds: Set<String>,
        @Param("productIds") productIds: Set<String>
    ): List<ProductCollection>
}
