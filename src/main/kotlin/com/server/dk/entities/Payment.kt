package com.server.dk.entities

import com.server.dk.enums.PaymentType
import com.server.dk.utils.DateUtils
import java.time.LocalDateTime
import java.time.ZoneId
import javax.persistence.*

@Entity
class Payment : Auditable() {

    @Id
    @Column(unique = true)
    var id: String = ""

    var forDate: String = ""

    @Enumerated(EnumType.STRING)
    var paymentType: PaymentType = PaymentType.NONE

    var description: String? = null

    var amountInPaisa: Long = 0

    var multiplierUsed: Int = 0

    var companyOldAmountInPaisa: Long = 0
    var companyNewAmountInPaisa: Long = 0
    var employeeOldAmountInPaisa: Long = 0
    var employeeNewAmountInPaisa: Long = 0

    var addedAt: LocalDateTime = DateUtils.dateTimeNow()

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
//    @MapsId("company_id")
    @JoinColumn(name = "company_id")
    var company: Company? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
//    @MapsId("employee_id")
    @JoinColumn(name = "employee_id")
    var employee: Employee? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    var addedBy: User? = null;

}
