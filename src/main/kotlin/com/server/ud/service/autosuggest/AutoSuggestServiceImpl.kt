package com.server.ud.service.autosuggest

import com.server.ud.model.AutoSuggestResult
import com.server.ud.provider.autosuggest.AutoSuggestProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AutoSuggestServiceImpl : AutoSuggestService() {

    @Autowired
    private lateinit var autoSuggestProvider: AutoSuggestProvider
    override fun getPostAutoSuggestion(typesText: String): AutoSuggestResult {
        return autoSuggestProvider.getPostAutoSuggest(typesText)
    }
}
