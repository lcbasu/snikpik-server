package com.dukaankhata.server.entities

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class CompanyCustomerKey: Serializable {
    @Column(name = "company_id")
    var companyId: Long = -1

    @Column(name = "user_id")
    var userId: String = ""
}
