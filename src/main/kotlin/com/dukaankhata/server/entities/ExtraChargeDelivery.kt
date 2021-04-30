package com.dukaankhata.server.entities

import javax.persistence.*

@Entity
class ExtraChargeDelivery : Auditable() {

    @EmbeddedId
    var id: ExtraChargeDeliveryKey? = null

    /**
     * In Paisa
     * */
    var deliveryChargePerOrder: Long = 0

    /**
     * In Paisa
     * */
    var deliveryChargeFreeAbove: Long = 0

    // Keeping Company reference in all the models
    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @MapsId("company_id")
    @JoinColumn(name = "company_id")
    var company: Company? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    var addedBy: User? = null;
}
