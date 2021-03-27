package com.dukaankhata.server.entities

import com.dukaankhata.server.utils.DateUtils
import java.time.LocalDateTime
import java.time.ZoneId
import javax.persistence.*

@Entity
class Overtime : Auditable() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = -1

    var forDate: String? = null

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