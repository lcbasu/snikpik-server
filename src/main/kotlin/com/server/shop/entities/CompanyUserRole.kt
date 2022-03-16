package com.server.shop.entities

import com.server.common.entities.Auditable
import javax.persistence.*

@Entity
class CompanyUserRole : Auditable() {

    @EmbeddedId
    var id: CompanyUserRoleKey? = null

    @ManyToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @MapsId("user_id")
    @JoinColumn(name = "user_id")
    var user: UserV3? = null

    @ManyToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @MapsId("company_id")
    @JoinColumn(name = "company_id")
    var company: CompanyV3? = null
}
