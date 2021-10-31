package com.server.ud.dao.es.article

import com.server.ud.entities.es.article.Article
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.annotations.Query

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface ESArticleRepository : ElasticsearchRepository<Article?, String?> {
    fun findByAuthorsName(name: String?, pageable: Pageable?): Page<Article?>?

    @Query("{\"bool\": {\"must\": [{\"match\": {\"authors.name\": \"?0\"}}]}}")
    fun findByAuthorsNameUsingCustomQuery(name: String?, pageable: Pageable?): Page<Article?>?
}
