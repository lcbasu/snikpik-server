package com.server.ud.service.autosuggest

import com.server.ud.model.AutoSuggestResult

abstract class AutoSuggestService {
    abstract fun getPostAutoSuggestion(typedText: String): AutoSuggestResult

}
