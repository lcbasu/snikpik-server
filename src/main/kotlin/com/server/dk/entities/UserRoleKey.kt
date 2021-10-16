package com.server.dk.entities

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class UserRoleKey: Serializable {
    @Column(name = "user_id")
    var userId: String = ""

    @Column(name = "company_id")
    var companyId: String = ""

    @Column(name = "role_type")
    var roleType: String = ""
}
