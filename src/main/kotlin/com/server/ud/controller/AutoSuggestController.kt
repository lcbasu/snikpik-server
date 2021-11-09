package com.server.ud.controller

import com.server.ud.model.AutoSuggestResult
import com.server.ud.service.autosuggest.AutoSuggestService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("ud/post")
class AutoSuggestController {

    @Autowired
    private lateinit var autoSuggestService: AutoSuggestService

    @RequestMapping(value = ["/getPostAutoSuggestion"], method = [RequestMethod.GET])
    fun getPostAutoSuggestion(@RequestParam typesText: String): AutoSuggestResult {
        return autoSuggestService.getPostAutoSuggestion(typesText)
    }
}
