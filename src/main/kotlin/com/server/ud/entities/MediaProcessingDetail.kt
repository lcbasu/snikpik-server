package com.server.ud.entities

import com.server.ud.enums.ResourceType
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("media_processing_detail")
data class MediaProcessingDetail (

    @PrimaryKeyColumn(name = "file_unique_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var fileUniqueId: String = "",

    @Column("resource_type")
    var resourceType: ResourceType? = null,

    @Column("resource_id")
    var resourceId: String? = null,

    @Column("input_file_path")
    var inputFilePath: String? = null,

    @Column("input_file_present")
    var inputFilePresent: Boolean? = false,

    @Column("output_file_path")
    var outputFilePath: String? = null,

    @Column("output_file_present")
    var outputFilePresent: Boolean? = false,

    @Column("for_user")
    var forUser: String? = null
)
