package com.server.ud.service

import com.server.common.provider.AuthProvider
import com.server.common.provider.MediaHandlerProvider
import com.server.ud.service.MediaHandlerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MediaHandlerServiceImpl : MediaHandlerService() {

    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var mediaHandlerProvider: MediaHandlerProvider

    override fun startProcessingAfterVideoProcessing(processedVideoUrls : Set<String>) {
        mediaHandlerProvider.startProcessingAfterVideoProcessing(processedVideoUrls)
    }

}
