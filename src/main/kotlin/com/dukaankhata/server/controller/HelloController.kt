package com.dukaankhata.server.controller

import com.dukaankhata.server.utils.UniqueIdGeneratorUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {

    @Autowired
    private lateinit var uniqueIdGeneratorUtils: UniqueIdGeneratorUtils

    @RequestMapping("/")
    fun index(): String? {
        return "Greetings from DukaanKhata!"
    }

    @RequestMapping("/public/{id}")
    fun public(@PathVariable id: String): String? {
        return "Greetings from DukaanKhata! $id"
    }

    @RequestMapping(value = ["/getUniqueId/{prefix}"], method = [RequestMethod.GET])
    fun getUniqueId(@PathVariable prefix: String?): String? {
        return uniqueIdGeneratorUtils.getUniqueId(prefix);
    }

}
