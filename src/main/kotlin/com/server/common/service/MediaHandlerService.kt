package com.server.common.service

import com.server.common.enums.MediaType
import com.server.dk.model.MediaDetailsV2
import org.springframework.web.multipart.MultipartFile

abstract class MediaHandlerService {
    abstract fun uploadFile(file: MultipartFile, mediaType: MediaType): MediaDetailsV2
}
