package com.server.shop.entities

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
open class FirstMapped {
    var firstVal: String? = null
}

@MappedSuperclass
open class SecondMapped : FirstMapped() {
    var secondVal: String? = null
}

@Entity
class ExtendingEntity : SecondMapped() {
    @Id
    var id = 0
}

