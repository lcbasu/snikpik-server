package com.server.common.entities

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class UniqueId(
    @Id
    @Column(unique = true)
    var id: String = "",

    var prefix: String? = null
) : Auditable()
