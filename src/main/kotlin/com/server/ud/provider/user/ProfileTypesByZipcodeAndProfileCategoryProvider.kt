package com.server.ud.provider.user

import com.server.ud.dao.user.ProfileTypesByZipcodeAndProfileCategoryRepository
import com.server.ud.entities.user.ProfileTypesByZipcodeAndProfileCategory
import com.server.ud.entities.user.UserV2
import com.server.ud.entities.user.getProfiles
import com.server.ud.utils.pagination.PaginationRequestUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ProfileTypesByZipcodeAndProfileCategoryProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var repository: ProfileTypesByZipcodeAndProfileCategoryRepository

    fun save(userV2: UserV2): List<ProfileTypesByZipcodeAndProfileCategory> {
        try {
            if (userV2.permanentLocationZipcode == null) {
                logger.error("zipcode is required to save ProfileTypesByZipcodeAndProfileCategory for userId: ${userV2.userId}.")
                return emptyList()
            }
            val profileTypes = userV2.getProfiles().profileTypes
                .map {
                    ProfileTypesByZipcodeAndProfileCategory(
                        zipcode = userV2.permanentLocationZipcode!!,
                        profileCategory = it.category,
                        profileType = it.id,
                    )
            }
            return repository.saveAll(profileTypes)
        } catch (e: Exception) {
            logger.error("Saving ProfileTypesByZipcodeAndProfileCategory filed for userId: ${userV2.userId}.")
            e.printStackTrace()
            return emptyList()
        }
    }
}
