package com.dukaankhata.server.controller

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {
    @RequestMapping("/")
    fun index(): String? {
        return "Greetings from Spring Boot!"
    }

    @RequestMapping("/public/{id}")
    fun public(@PathVariable id: String): String? {
        return "Greetings from Public endpoint! $id"
    }

}
