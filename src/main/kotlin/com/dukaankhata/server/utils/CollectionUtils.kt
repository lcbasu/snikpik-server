package com.dukaankhata.server.utils

import com.dukaankhata.server.dao.CollectionRepository
import com.dukaankhata.server.dto.SaveCollectionRequest
import com.dukaankhata.server.entities.Collection
import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.User
import com.dukaankhata.server.enums.ReadableIdPrefix
import com.dukaankhata.server.model.convertToString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CollectionUtils {

    @Autowired
    private lateinit var collectionRepository: CollectionRepository

    @Autowired
    private lateinit var uniqueIdGeneratorUtils: UniqueIdGeneratorUtils

    fun getCollection(collectionId: String): Collection? =
        try {
            collectionRepository.findById(collectionId).get()
        } catch (e: Exception) {
            null
        }

    fun saveCollection(company: Company, user: User, saveCollectionRequest: SaveCollectionRequest) : Collection? {
        try {
            val newCollection = Collection()
            newCollection.id = uniqueIdGeneratorUtils.getUniqueId(ReadableIdPrefix.CLC.getPrefix())
            newCollection.addedBy = user
            newCollection.company = company
            newCollection.title = saveCollectionRequest.title
            newCollection.subTitle = saveCollectionRequest.subTitle
            newCollection.mediaDetails = saveCollectionRequest.mediaDetails.convertToString()
            return collectionRepository.save(newCollection)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

}
