package com.server.common.service

import com.server.common.controller.ProcessedVideoMessage
import com.server.common.enums.MediaType
import com.server.common.provider.AuthProvider
import com.server.common.provider.MediaHandlerProvider
import com.server.dk.model.MediaDetailsV2
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class MediaHandlerServiceImpl : MediaHandlerService() {

    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var mediaHandlerProvider: MediaHandlerProvider

    override fun uploadFile(file: MultipartFile, mediaType: MediaType): MediaDetailsV2 {
        val requestContext = authProvider.validateRequest()
        if (requestContext.user.anonymous) error("Only logged in users can upload media files")
        return mediaHandlerProvider.uploadFile(file, mediaType)
    }

    override fun startProcessingAfterVideoProcessing(message: ProcessedVideoMessage?) {
        mediaHandlerProvider.startProcessingAfterVideoProcessing(message)
    }

}
