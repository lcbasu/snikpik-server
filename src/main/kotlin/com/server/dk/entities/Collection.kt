package com.server.dk.entities

import com.server.dk.model.MediaDetails
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.entities.Auditable
import com.server.common.entities.User
import javax.persistence.*

@Entity
class Collection : Auditable() {
    @Id
    var id: String = ""
    var title: String = ""
    var subTitle: String? = ""
    var mediaDetails: String = "" // MediaDetails object -> Multiple Images or videos

    var totalOrderAmountInPaisa: Long? = 0
    var totalViewsCount: Long? = 0
    var totalClicksCount: Long? = 0
    var totalOrdersCount: Long? = 0
    var totalProductsViewCount: Long? = 0
    var totalProductsClickCount: Long? = 0
    var totalUnitsOrdersCount: Long? = 0

    // Keeping Company reference in all the models
    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    var company: Company? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    var addedBy: User? = null;
}

fun Collection.getMediaDetails(): MediaDetails {
    this.apply {
        return jacksonObjectMapper().readValue(mediaDetails, MediaDetails::class.java)
    }
}
