package com.dukaankhata.server.entities

import com.dukaankhata.server.enums.PunchType
import com.dukaankhata.server.enums.SelfieType
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Attendance : Auditable() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = -1

    var forDate: String = ""

    var punchAt: LocalDateTime = LocalDateTime.now()

    @Enumerated(EnumType.STRING)
    var punchType: PunchType = PunchType.NONE

    var selfieUrl: String? = null
    @Enumerated(EnumType.STRING)
    var selfieType: SelfieType? = null

    var locationLat: Double? = null
    var locationLong: Double? = null
    var locationName: String? = null

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    var company: Company? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    var employee: Employee? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "punch_by_user_id")
    var punchBy: User? = null;

}
