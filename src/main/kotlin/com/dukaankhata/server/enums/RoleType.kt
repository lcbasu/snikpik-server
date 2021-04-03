package com.dukaankhata.server.enums

enum class RoleType {
    NOT_DEFINED, // -1
    EMPLOYER, // All Access
    EMPLOYEE_ADMIN, // All Access. This is defined when an employer makes an employee the Admin for the company
    EMPLOYEE_NON_ADMIN,
}
