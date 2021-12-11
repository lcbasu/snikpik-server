package com.server.ud.provider.es

import com.server.ud.model.AutoSuggestEntry
import com.server.ud.model.AutoSuggestResult
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.script.ScriptType
import org.elasticsearch.script.mustache.SearchTemplateRequest
import org.elasticsearch.script.mustache.SearchTemplateResponse
import org.elasticsearch.search.aggregations.bucket.filter.ParsedFilter
import org.elasticsearch.search.aggregations.bucket.terms.Terms
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.elasticsearch.search.suggest.SuggestBuilder
import org.elasticsearch.search.suggest.SuggestBuilders
import org.elasticsearch.search.suggest.completion.CompletionSuggestion
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class ESProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var restHighLevelClient: RestHighLevelClient

    fun getNearbyZipcodes(lat: Double?, lng: Double?): Set<String> {
        return try {
            val request = SearchTemplateRequest(SearchRequest("locations"))
            request.scriptType = ScriptType.INLINE
            request.script =
                "{\"aggs\":{\"locations_filter\":{\"filter\":{\"geo_distance\":{\"distance\":\"{{distance_in_km}}\",\"geoPoint\":{\"lat\":{{latitude}},\"lon\":{{longitude}}}}},\"aggs\":{\"zipcodes\":{\"terms\":{\"field\":\"zipcode\"}}}}},\"size\":0}"
            val scriptParams: MutableMap<String, Any> = HashMap()

            // TODO: Make this dynamic by taking this input from user
            scriptParams["distance_in_km"] = "300km"
            scriptParams["latitude"] = lat.toString()
            scriptParams["longitude"] = lng.toString()
            request.scriptParams = scriptParams
            val response: SearchTemplateResponse = restHighLevelClient.searchTemplate(request, RequestOptions.DEFAULT)
            ((response.response.aggregations.asList()[0] as ParsedFilter).aggregations.asList()[0] as Terms).buckets.map {
                it.keyAsString
            }.toSet()
        } catch (e: Exception) {
            emptySet()
        }
    }

    fun getPostAutoSuggest(typedText: String): AutoSuggestResult {
        return try {

            val searchRequest = SearchRequest("posts_auto_suggest")

            val searchSourceBuilder = SearchSourceBuilder()
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());

            val completionSuggestionBuilder = SuggestBuilders
                .completionSuggestion("suggestionText")
                .prefix(typedText)
            val suggestBuilder = SuggestBuilder()
            suggestBuilder.addSuggestion("post-autocomplete-suggest", completionSuggestionBuilder)
            searchSourceBuilder.suggest(suggestBuilder)

            searchRequest.source(searchSourceBuilder)
            val searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT)
            val suggest = searchResponse.suggest
            val entries: CompletionSuggestion = suggest.getSuggestion("post-autocomplete-suggest")

            val suggestions = mutableMapOf<String, Set<String>>()
            for (entry in entries) {
                for (option in entry.options) {
                    val suggestText = option.text.string()
                    val id = option.hit.id
                    suggestions[suggestText] = suggestions.getOrDefault(suggestText, emptySet()) + setOf(id)
                    logger.info("suggestText: $suggestText")
                }
            }
            AutoSuggestResult(
                suggestions = suggestions.map {
                    AutoSuggestEntry(
                        text = it.key,
                        ids = it.value
                    )
                }
            )
        } catch (e: Exception) {
            logger.error("Failed to get auto-suggest for typedText: $typedText")
            e.printStackTrace()
            AutoSuggestResult(emptyList())
        }
    }
}
