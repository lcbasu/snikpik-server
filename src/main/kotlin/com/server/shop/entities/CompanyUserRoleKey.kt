package com.server.shop.entities

import com.server.shop.enums.CompanyUserRoleType
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Embeddable
class CompanyUserRoleKey: Serializable {

    @Column(name = "user_id")
    var userId: String = ""

    @Column(name = "company_id")
    var companyId: String = ""

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type")
    var roleType: CompanyUserRoleType = CompanyUserRoleType.OWNER
}
