package com.dukaankhata.server.entities

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class AttendanceByAdminKey: Serializable {
    @Column(name = "company_id")
    var companyId: Long? = null

    @Column(name = "employee_id")
    var employeeId: Long? = null

    @Column(name = "for_date")
    var forDate: String? = null
}
