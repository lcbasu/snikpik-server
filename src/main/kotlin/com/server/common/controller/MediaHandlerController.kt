package com.server.common.controller

import com.server.common.enums.MediaType
import com.server.common.service.MediaHandlerService
import com.server.dk.model.MediaDetailsV2
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile


@RestController
@RequestMapping("media")
class MediaHandlerController {

    @Autowired
    private lateinit var mediaHandlerService: MediaHandlerService

    @PostMapping("/upload")
    fun uploadFile(@RequestParam("file") file: MultipartFile, @RequestParam("mediaType") mediaType: MediaType): MediaDetailsV2 {
        return mediaHandlerService.uploadFile(file, mediaType)
    }
}
