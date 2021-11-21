package com.server.ud.service

abstract class MediaHandlerService {
    abstract fun startProcessingAfterVideoProcessing(processedVideoUrls : Set<String>)
}
