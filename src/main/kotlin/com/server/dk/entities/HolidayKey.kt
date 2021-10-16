package com.server.dk.entities

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class HolidayKey: Serializable {
    @Column(name = "company_id")
    var companyId: String = ""

    @Column(name = "employee_id")
    var employeeId: String = ""

    @Column(name = "for_date")
    var forDate: String = ""
}
