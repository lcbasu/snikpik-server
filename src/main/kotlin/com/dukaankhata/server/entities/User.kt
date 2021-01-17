package com.dukaankhata.server.entities

import com.dukaankhata.server.enums.Gender
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id

@Entity
class User : Auditable() {
    // Phone Number with country code
    @Id
    var id: String = ""
    var uid: String = ""
    var fullName: String = ""
    @Enumerated(EnumType.STRING)
    var gender: Gender = Gender.DO_NOT_SAY
}
