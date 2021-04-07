package com.dukaankhata.server.entities

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class UserRoleKey: Serializable {
    @Column(name = "user_id")
    var userId: String = ""

    @Column(name = "company_id")
    var companyId: Long = -1

    @Column(name = "role_type")
    var roleType: String = ""
}
