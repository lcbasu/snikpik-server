package com.server.ud.entities

//import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
//import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
//import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import org.springframework.data.annotation.Id

//@DynamoDBTable(tableName = "UserPost")
class UserPost(

    @Id
    var id: String? = null,

//    @DynamoDBHashKey
    var userId: String? = null,

//    @DynamoDBRangeKey
//    @DynamoDBAttribute
    var postedAt: Long? = null,

//    @DynamoDBAttribute
    var title: String? = null,

//    @DynamoDBAttribute
    var description: String? = null

//    @DynamoDBRangeKey
//    @DynamoDBTypeConverted(converter = MomentConverter::class)
//    @DynamoDBAttribute
//    var moment: Moment? = null,
)

