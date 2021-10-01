package com.dukaankhata.server.entities

import com.dukaankhata.server.enums.OpeningBalanceType
import com.dukaankhata.server.enums.SalaryType
import com.dukaankhata.server.utils.DateUtils
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Employee : Auditable() {
    @Id
    @Column(unique = true)
    var id: String = ""
    var name: String = ""
    var absoluteMobile: String = ""
    var countryCode: String = ""

    @Enumerated(EnumType.STRING)
    var salaryType: SalaryType = SalaryType.MONTHLY
    var salaryCycle: String = ""
    var salaryAmountInPaisa: Long = 0

//    @Enumerated(EnumType.STRING)
//    var openingBalanceType: OpeningBalanceType = OpeningBalanceType.ADVANCE
//    var openingBalanceInPaisa: Long = 0

    var balanceInPaisaTillNow: Long = 0

    var joinedAt: LocalDateTime = DateUtils.dateTimeNow()
    var leftAt: LocalDateTime? = null

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    var company: Company? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "created_for_user_id")
    var createdForUser: User? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    var createdByUser: User? = null;
}
