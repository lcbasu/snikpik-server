package com.server.dk.entities

import com.server.common.utils.DateUtils
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Overtime : Auditable() {

    @Id
    @Column(unique = true)
    var id: String = ""

    var forDate: String = ""

    var hourlyOvertimeWageInPaisa: Long = 0
    var totalOvertimeAmountInPaisa: Long = 0
    var totalOvertimeMinutes: Int = 0

    var addedAt: LocalDateTime = DateUtils.dateTimeNow()

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    var company: Company? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    var employee: Employee? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    var addedBy: User? = null;

}
