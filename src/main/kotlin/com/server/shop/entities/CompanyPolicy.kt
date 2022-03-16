package com.server.shop.entities

import com.server.common.entities.Auditable
import javax.persistence.*

@Entity
class CompanyPolicy : Auditable() {
    @EmbeddedId
    var id: CompanyPolicyKey? = null

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @MapsId("policy_id")
    @JoinColumn(name = "policy_id")
    var policy: Policy? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @MapsId("company_id")
    @JoinColumn(name = "company_id")
    var company: CompanyV3? = null;

}
