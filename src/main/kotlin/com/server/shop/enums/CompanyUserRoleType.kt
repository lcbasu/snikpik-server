package com.server.shop.enums

enum class CompanyUserRoleType {
    OWNER, // All Access
    EMPLOYEE_ADMIN, // All Access. This is defined when an employer makes an employee the Admin for the company
    EMPLOYEE_NON_ADMIN,
}
