package com.server.dk.entities

import com.server.common.entities.Auditable
import com.server.common.entities.User
import javax.persistence.*

@Entity
class ExtraChargeTax : Auditable() {

    @EmbeddedId
    var id: ExtraChargeTaxKey? = null

    var taxPercentage: Double = 0.0

    // Keeping Company reference in all the models
    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @MapsId("company_id")
    @JoinColumn(name = "company_id")
    var company: Company? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    var addedBy: User? = null;
}
