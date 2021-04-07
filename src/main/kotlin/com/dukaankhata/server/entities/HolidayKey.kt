package com.dukaankhata.server.entities

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class HolidayKey: Serializable {
    @Column(name = "company_id")
    var companyId: Long = -1

    @Column(name = "employee_id")
    var employeeId: Long = -1

    @Column(name = "for_date")
    var forDate: String = ""
}
