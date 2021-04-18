package com.dukaankhata.server.entities

import javax.persistence.*

@Entity
class Collection : Auditable() {
    @Id
    var id: String = ""
    var title: String = ""
    var subTitle: String = ""
    var mediaDetails: String = "" // MediaDetails object -> Multiple Images or videos

    // Keeping Company reference in all the models
    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    var company: Company? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    var addedBy: User? = null;
}
