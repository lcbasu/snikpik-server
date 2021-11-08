package com.server.ud.entities.es.post

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document

@Document(indexName = "posts_auto_suggest")
class ESPostAutoSuggest (

    @Id
    var postId: String,

    var suggestionText: Set<String>,
)


