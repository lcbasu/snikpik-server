package com.server.ud.dao.es.location

import com.server.ud.entities.es.location.ESLocation
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository

@Repository
interface ESLocationRepository : ElasticsearchRepository<ESLocation?, String?> {
}
