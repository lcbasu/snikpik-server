package com.server.dk.entities

import com.server.dk.enums.DeliveryType
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Embeddable
class ExtraChargeDeliveryKey: Serializable {
    @Column(name = "company_id")
    var companyId: String = ""

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_type")
    var deliveryType: DeliveryType = DeliveryType.FREE
}
