package com.dukaankhata.server.enums

enum class RoleType {
    NOT_DEFINED,
    EMPLOYER,
    EMPLOYEE_ADMIN, // This is defined when an employer makes an employee the Admin for the company
    EMPLOYEE_NON_ADMIN,
}
