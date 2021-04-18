package com.dukaankhata.server.entities

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class UniqueId(
    @Id
    var id: String = ""
)
