package com.dukaankhata.server.entities

import com.dukaankhata.server.enums.HolidayType
import javax.persistence.*

@Entity
class Holiday : Auditable() {

    @EmbeddedId
    var id: HolidayKey? = null

    @Enumerated(EnumType.STRING)
    var holidayType: HolidayType = HolidayType.NONE

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
