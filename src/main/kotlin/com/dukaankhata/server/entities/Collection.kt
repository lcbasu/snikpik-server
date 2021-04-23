package com.dukaankhata.server.entities

import com.dukaankhata.server.model.MediaDetails
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
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
