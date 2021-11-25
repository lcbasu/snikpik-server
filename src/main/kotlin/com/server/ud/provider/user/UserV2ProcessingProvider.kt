package com.server.ud.provider.user

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UserV2ProcessingProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var userV2Provider: UserV2Provider

    @Autowired
    private lateinit var handlesByUserProvider: HandlesByUserProvider

    @Autowired
    private lateinit var usersByProfileCategoryProvider: UsersByProfileCategoryProvider

    @Autowired
    private lateinit var usersByProfileTypeProvider: UsersByProfileTypeProvider

    @Autowired
    private lateinit var usersByZipcodeAndProfileTypeProvider: UsersByZipcodeAndProfileTypeProvider

    @Autowired
    private lateinit var profileTypesByZipcodeAndProfileCategoryProvider: ProfileTypesByZipcodeAndProfileCategoryProvider

    @Autowired
    private lateinit var usersByZipcodeProvider: UsersByZipcodeProvider

    fun processUserV2(userId: String) {
        GlobalScope.launch {
            logger.info("Start: UserV2 processing for userId: $userId")

            val user = userV2Provider.getUser(userId) ?: error("No user found for $userId while doing user processing.")

            val handlesByUserFuture = async {
                handlesByUserProvider.save(user)
            }

            val usersByProfileCategoryFuture = async {
                usersByProfileCategoryProvider.save(user)
            }

            val usersByProfileTypeFuture = async {
                usersByProfileTypeProvider.save(user)
            }

            val usersByZipcodeAndProfileFuture = async {
                usersByZipcodeAndProfileTypeProvider.save(user)
            }

            val profileTypesByZipcodeAndProfileCategoryProviderFuture = async {
                profileTypesByZipcodeAndProfileCategoryProvider.save(user)
            }

            val usersByZipcodeFuture = async {
                usersByZipcodeProvider.save(user)
            }

            handlesByUserFuture.await()
            usersByProfileCategoryFuture.await()
            usersByProfileTypeFuture.await()
            usersByZipcodeAndProfileFuture.await()
            profileTypesByZipcodeAndProfileCategoryProviderFuture.await()
            usersByZipcodeFuture.await()

            logger.info("Done: UserV2 processing for userId: $userId")
        }
    }

}
