package com.server.ud.entities.es.article

import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.FieldType.Text
import org.springframework.data.elasticsearch.annotations.Field

@Document(indexName = "author")
class Author (
    @Field(type = Text)
    private val name: String? = null
)
