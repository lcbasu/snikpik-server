package com.server.ud.dao.es.post

import com.server.ud.entities.es.post.ESPostAutoSuggest
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository

@Repository
interface ESPostAutoSuggestRepository : ElasticsearchRepository<ESPostAutoSuggest?, String?> {
}
