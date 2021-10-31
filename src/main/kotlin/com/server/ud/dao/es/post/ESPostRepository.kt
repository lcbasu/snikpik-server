package com.server.ud.dao.es.post

import com.server.ud.entities.es.post.ESPost
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository

@Repository
interface ESPostRepository : ElasticsearchRepository<ESPost?, String?> {
}
