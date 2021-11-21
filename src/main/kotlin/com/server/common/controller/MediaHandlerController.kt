package com.server.common.controller

import com.server.ud.service.MediaHandlerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("media")
class MediaHandlerController {

    @Autowired
    private lateinit var mediaHandlerService: MediaHandlerService

    // DO NOT ALLOW UPLOADS directly though server. Use AWS Lambda
//    @PostMapping("/upload")
//    fun uploadFile(@RequestParam("file") file: MultipartFile, @RequestParam("mediaType") mediaType: MediaType): MediaDetailsV2 {
//        return mediaHandlerService.uploadFile(file, mediaType)
//    }
}
