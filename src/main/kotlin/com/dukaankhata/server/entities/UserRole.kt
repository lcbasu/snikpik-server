package com.dukaankhata.server.entities

import javax.persistence.*

@Entity
class UserRole : Auditable() {

    @EmbeddedId
    var id: UserRoleKey? = null

    @ManyToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @MapsId("user_id")
    @JoinColumn(name = "user_id")
    var user: User? = null

    @ManyToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @MapsId("company_id")
    @JoinColumn(name = "company_id")
    var company: Company? = null
}
