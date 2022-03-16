package com.server.shop.entities

import com.server.shop.enums.PolicyType
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Embeddable
class BrandPolicyKey: Serializable {
    // Only one value of policy per brand and policy type
    @Column(name = "brand_id")
    var brandId: String = ""

    // Only one value of policy per brand and policy type
    // Hence using policy_type instead of policy_id
    @Enumerated(EnumType.STRING)
    @Column(name = "policy_type")
    var policyType: PolicyType = PolicyType.REFUND
}
