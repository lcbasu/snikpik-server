package com.server.ud.controller

import com.server.ud.model.AutoSuggestResult
import com.server.ud.service.autosuggest.AutoSuggestService
import io.micrometer.core.annotation.Timed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@Timed
@RequestMapping("ud/autoSuggest")
class AutoSuggestController {

    @Autowired
    private lateinit var autoSuggestService: AutoSuggestService

    @RequestMapping(value = ["/getPostAutoSuggestion"], method = [RequestMethod.GET])
    fun getPostAutoSuggestion(@RequestParam typedText: String): AutoSuggestResult {
        return autoSuggestService.getPostAutoSuggestion(typedText)
    }
}
