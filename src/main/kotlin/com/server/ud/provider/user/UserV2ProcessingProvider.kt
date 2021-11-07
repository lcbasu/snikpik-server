package com.server.ud.provider.user

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
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
    private lateinit var usersByZipcodeAndProfileProvider: UsersByZipcodeAndProfileProvider

    @Autowired
    private lateinit var usersByZipcodeProvider: UsersByZipcodeProvider

    fun processUserV2(userId: String) {
        runBlocking {
            logger.info("Do UserV2 processing for userId: $userId")

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
                usersByZipcodeAndProfileProvider.save(user)
            }

            val usersByZipcodeFuture = async {
                usersByZipcodeProvider.save(user)
            }

            handlesByUserFuture.await()
            usersByProfileCategoryFuture.await()
            usersByProfileTypeFuture.await()
            usersByZipcodeAndProfileFuture.await()
            usersByZipcodeFuture.await()
            logger.info("Completed")
        }
    }

}
