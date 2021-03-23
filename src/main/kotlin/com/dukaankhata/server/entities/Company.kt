package com.dukaankhata.server.entities

import com.dukaankhata.server.enums.SalaryPaymentSchedule
import javax.persistence.*

@Entity
class Company : Auditable() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
    var name: String = ""
    var location: String = ""
    var workingMinutes: Int = 0
    var totalDueAmountInPaisa: Long = 0
//    var userId: String = ""
    @Enumerated(EnumType.STRING)
    var salaryPaymentSchedule: SalaryPaymentSchedule = SalaryPaymentSchedule.LAST_DAY_OF_MONTH

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User? = null;
}
