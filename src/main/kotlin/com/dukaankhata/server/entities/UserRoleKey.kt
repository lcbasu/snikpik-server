package com.dukaankhata.server.entities

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class UserRoleKey: Serializable {
    @Column(name = "user_id")
    var userId: String? = null

    @Column(name = "company_id")
    var companyId: Long? = null

    @Column(name = "role_type")
    var roleType: String? = null
}
