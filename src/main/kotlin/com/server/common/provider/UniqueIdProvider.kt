package com.server.common.provider

import com.server.common.dao.UniqueIdRepository
import com.server.common.entities.UniqueId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class UniqueIdProvider {

    val maximumTryout = 10
    val uuidMinLength = 10
    val uuidMaxLength = 13 // 3(For prefix - Fixed) + 10 (Random)

    @Autowired
    private lateinit var uniqueIdRepository: UniqueIdRepository

    @Autowired
    private lateinit var randomIdProvider: RandomIdProvider

    @Transactional
    fun getUniqueIdAfterSaving(prefix: String? = null, onlyNumbers: Boolean? = false, minLength: Int? = uuidMinLength, maxLength: Int? = uuidMaxLength): String {
        return saveId(getUniqueIdWithoutSaving(prefix, onlyNumbers, minLength, maxLength), prefix).id
    }

    @Transactional
    fun getUniqueIdWithoutSaving(prefix: String? = null, onlyNumbers: Boolean? = false, minLength: Int? = uuidMinLength, maxLength: Int? = uuidMaxLength): String {

        // Create a new UUID
        var currentId = randomIdProvider.getRandomId(prefix, onlyNumbers, minLength, maxLength)

        // Check in DB and regenerate if required
        try {
            var existingResult = uniqueIdRepository.findById(currentId)
            var currentTryoutCount = 1
            while (existingResult.isPresent && currentTryoutCount < maximumTryout) {
                currentId = randomIdProvider.getRandomId(prefix, onlyNumbers, minLength, maxLength)
                existingResult = uniqueIdRepository.findById(currentId)
                currentTryoutCount += 1
            }
            // Save the unique ID if not already present
            if (existingResult.isPresent.not()) {
                // Return the unique ID
                return currentId
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        error("Failed to generate Unique ID")
    }

    fun isIdAvailable(id: String): Boolean {
        return uniqueIdRepository.findById(id).isPresent.not()
    }

    fun saveId(id: String, prefix: String?): UniqueId {
        return uniqueIdRepository.saveAndFlush(UniqueId(id = id, prefix = prefix))
    }

}
