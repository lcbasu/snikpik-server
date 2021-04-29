package com.dukaankhata.server.entities

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class UniqueId(
    @Id
    @Column(unique = true)
    var id: String = ""
)
