package com.server.ud.model

import com.server.ud.enums.ResourceType

data class MediaInputDetail(
    var fileUniqueId: String,
    var forUser: String,

    var inputFilePath: String,

    var resourceType: ResourceType,
    var resourceId: String,
)

data class MediaOutputDetail(
    var fileUniqueId: String,
    var forUser: String,

    var outputFilePath: String
)

data class FileInfo (val userId: String, val fileUniqueId: String)
data class BucketAndKey (val bucket: String, val key: String)
