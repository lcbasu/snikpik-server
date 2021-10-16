package com.dukaankhata.server.entities

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable

@DynamoDBTable(tableName = "posts")
class Post(

    var id: String? = null,

    @DynamoDBHashKey
    var userId: String? = null,

    @DynamoDBRangeKey
    @DynamoDBAttribute
    var postedAt: Long? = null,

    @DynamoDBAttribute
    var title: String? = null,

    @DynamoDBAttribute
    var description: String? = null

//    @DynamoDBRangeKey
//    @DynamoDBTypeConverted(converter = MomentConverter::class)
//    @DynamoDBAttribute
//    var moment: Moment? = null,
)

