package com.server.ud.model

data class AutoSuggestEntry (
    val text: String,
    val ids: Set<String>
)

data class AutoSuggestResult (
    val suggestions: List<AutoSuggestEntry>
)

