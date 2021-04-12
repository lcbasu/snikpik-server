package com.dukaankhata.server.model

import java.time.LocalDate

data class Student(
    var id: Int,
    val name: String,
    val lastName: String,
    val birthday: LocalDate,
    val nationality: String,
    val university: String,
    val active: Boolean?,
)
