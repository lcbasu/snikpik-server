package com.dukaankhata.server.entities

import com.dukaankhata.server.utils.DateUtils
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class LateFine : Auditable() {

    @Id
    @Column(unique = true)
    var id: String = ""

    var forDate: String = ""

    var hourlyLateFineWageInPaisa: Long = 0
    var totalLateFineAmountInPaisa: Long = 0
    var totalLateFineMinutes: Int = 0

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
