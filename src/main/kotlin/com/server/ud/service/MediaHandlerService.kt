package com.server.ud.service

import com.server.common.enums.MediaType
import com.server.common.model.MediaDetailsV2
import org.springframework.web.multipart.MultipartFile

abstract class MediaHandlerService {
    abstract fun startProcessingAfterVideoProcessing(processedVideoUrls : Set<String>)
    abstract fun uploadFile(file: MultipartFile, mediaType: MediaType): MediaDetailsV2
}
