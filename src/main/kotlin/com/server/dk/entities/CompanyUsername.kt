package com.server.dk.entities

import com.server.common.entities.Auditable
import com.server.common.entities.User
import javax.persistence.*

@Entity
class CompanyUsername : Auditable() {
    @Id
    @Column(unique = true)
    var id: String = ""

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    var company: Company? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    var addedBy: User? = null;
}

