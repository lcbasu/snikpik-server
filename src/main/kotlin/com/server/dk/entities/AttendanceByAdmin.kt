package com.server.dk.entities

import com.server.common.entities.Auditable
import com.server.common.entities.User
import com.server.dk.enums.AttendanceType
import javax.persistence.*

@Entity
class AttendanceByAdmin : Auditable() {

    @EmbeddedId
    var id: AttendanceByAdminKey? = null

    @Enumerated(EnumType.STRING)
    var attendanceType: AttendanceType = AttendanceType.NONE

    var workingMinutes: Int = 0

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @MapsId("company_id")
    @JoinColumn(name = "company_id")
    var company: Company? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @MapsId("employee_id")
    @JoinColumn(name = "employee_id")
    var employee: Employee? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    var addedBy: User? = null;

}
