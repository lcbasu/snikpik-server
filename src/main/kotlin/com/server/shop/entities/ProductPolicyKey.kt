package com.server.shop.entities

import com.server.shop.enums.PolicyType
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Embeddable
class ProductPolicyKey: Serializable {

    @Column(name = "product_id")
    var productId: String = ""

    @Enumerated(EnumType.STRING)
    @Column(name = "policy_type")
    var policyType: PolicyType = PolicyType.REFUND
}
