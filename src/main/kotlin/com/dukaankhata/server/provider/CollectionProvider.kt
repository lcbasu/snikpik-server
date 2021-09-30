package com.dukaankhata.server.provider

import AllCollectionsWithProductsRaw
import CollectionWithProductsRaw
import com.dukaankhata.server.dao.CollectionRepository
import com.dukaankhata.server.dto.*
import com.dukaankhata.server.entities.Collection
import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.User
import com.dukaankhata.server.enums.ReadableIdPrefix
import com.dukaankhata.server.model.convertToString
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CollectionProvider {

    @Autowired
    private lateinit var collectionRepository: CollectionRepository

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Autowired
    private lateinit var productCollectionProvider: ProductCollectionProvider

    @Autowired
    private lateinit var productVariantProvider: ProductVariantProvider

    fun getCollection(collectionId: String): Collection? =
        try {
            collectionRepository.findById(collectionId).get()
        } catch (e: Exception) {
            null
        }

    fun getCollections(collectionIds: Set<String>): List<Collection?> =
        try {
            collectionRepository.findAllById(collectionIds)
        } catch (e: Exception) {
            emptyList()
        }

    fun saveCollection(company: Company, user: User, saveCollectionRequest: SaveCollectionRequest) : Collection? {
        try {
            val newCollection = Collection()
            newCollection.id = uniqueIdProvider.getUniqueId(ReadableIdPrefix.CLC.name)
            newCollection.addedBy = user
            newCollection.company = company
            newCollection.title = saveCollectionRequest.title
            newCollection.subTitle = saveCollectionRequest.subTitle ?: ""
            newCollection.mediaDetails = saveCollectionRequest.mediaDetails.convertToString()
            return collectionRepository.save(newCollection)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getCollections(company: Company): List<Collection> =
        try {
            collectionRepository.findAllByCompany(company)
        } catch (e: Exception) {
            emptyList()
        }

    fun getBestSellerCollections(collections: List<Collection>, takeMaxSize: Int = 10): List<Collection> {
        return collections
            .sortedBy { it.totalOrderAmountInPaisa }
            .sortedBy { it.totalViewsCount }
            .take(takeMaxSize)
    }

    fun getAllCollection(company: Company) =
        runBlocking {
            AllCollectionsResponse(
                collections = getCollections(company).map {
                    async { it.toSavedCollectionResponse() }
                }.map {
                    it.await()
                }
            )
        }

    fun getAllCollectionWithProductsRaw(company: Company) =
        runBlocking {
            val collections = getCollections(company)
            val allProductCollections = productCollectionProvider.getProductCollections(collectionIds = collections.map { it.id }.toSet())
                .filter { it.collection != null && it.product != null }
                .groupBy { it.collection?.id }
            AllCollectionsWithProductsRaw(
                collections.map {
                    async {
                        val collection = it
                        val products = allProductCollections.getOrDefault(it.id, emptyList()).map { it.product }
                        collection to products
                    }
                }.map {
                    val result = it.await()
                    CollectionWithProductsRaw(
                        collection = result.first,
                        products = result.second.filterNotNull()
                    )
                })
        }

    fun getAllCollectionWithProducts(company: Company) =
        getAllCollectionWithProductsRaw(company).toAllCollectionsWithProductsResponse()

    fun getCollectionWithProductsRaw(collectionId: String) =
        runBlocking {
            val collection = getCollection(collectionId) ?: error("No collection found with id: $collectionId")
            val products = productCollectionProvider
                .getProductCollections(collectionIds = setOf(collection.id))
                .mapNotNull { it.product }
            CollectionWithProductsRaw(
                collection = collection,
                products = products
            )
        }
}
