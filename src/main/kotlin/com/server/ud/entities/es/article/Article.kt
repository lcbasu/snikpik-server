package com.server.ud.entities.es.article

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.InnerField
import org.springframework.data.elasticsearch.annotations.MultiField
import org.springframework.data.elasticsearch.annotations.FieldType.Keyword
import org.springframework.data.elasticsearch.annotations.FieldType.Nested
import org.springframework.data.elasticsearch.annotations.FieldType.Text


@Document(indexName = "blog")
class Article (
    @Id
    private val id: String? = null,

    @MultiField(
        mainField = Field(type = Text, fielddata = true),
        otherFields = [InnerField(suffix = "verbatim", type = Keyword)]
    )
    private val title: String? = null,

    @Field(type = Nested, includeInParent = true)
    private val authors: List<Author>? = null,

    @Field(type = Keyword)
    private val tags: List<String> = emptyList()

)
