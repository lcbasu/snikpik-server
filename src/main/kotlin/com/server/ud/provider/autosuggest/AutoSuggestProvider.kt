package com.server.ud.provider.autosuggest

import com.server.ud.provider.es.ESProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class AutoSuggestProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var esProvider: ESProvider

    fun getPostAutoSuggest(typedText: String) = esProvider.getPostAutoSuggest(typedText)
}
