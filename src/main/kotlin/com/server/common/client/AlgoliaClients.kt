package com.server.common.client

import com.algolia.search.DefaultSearchClient
import com.algolia.search.SearchClient
import com.server.common.properties.AlgoliaProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AlgoliaClients {

    @Autowired
    private lateinit var algoliaProperties: AlgoliaProperties

    @Bean
    fun getAlgoliaSearchClient(): SearchClient? {
        return DefaultSearchClient.create(algoliaProperties.applicationId, algoliaProperties.apiKey)
    }

}
