package com.server.ud.enums

enum class InstagramPostProcessingState {
    WANT_TO_INGEST,
    DOES_NOT_WANT_TO_INGEST,
    BLOCKED_FOR_INGESTION,
    PROCESSING,
    FAILED,
    FAILED_RETRY,
    SUCCESS,
    NOT_SUPPORTED, // -> For carousel that has one or more than one video.
}
