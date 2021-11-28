package com.server.ud.provider.user

import com.server.common.utils.DateUtils
import com.server.ud.dao.user.UsersByProfileCategoryRepository
import com.server.ud.entities.user.UserV2
import com.server.ud.entities.user.UsersByProfileCategory
import com.server.ud.entities.user.getProfiles
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UsersByProfileCategoryProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var usersByProfileCategoryRepository: UsersByProfileCategoryRepository

    fun save(userV2: UserV2): List<UsersByProfileCategory> {
        try {
            val usersByProfiles = userV2.getProfiles().profileTypes.map {
                UsersByProfileCategory(
                    profileCategory = it.category,
                    forDate = DateUtils.getInstantDate(userV2.createdAt),
                    createdAt = userV2.createdAt,
                    userId = userV2.userId,
                )
            }
            logger.info("UsersByProfileCategory saved for userId: ${userV2.userId}")
            return usersByProfileCategoryRepository.saveAll(usersByProfiles)
        } catch (e: Exception) {
            logger.error("Saving UsersByProfileCategory filed for userId: ${userV2.userId}.")
            e.printStackTrace()
            return emptyList()
        }
    }
}
