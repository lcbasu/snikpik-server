package com.server.dk.enums

enum class PaymentType {
    PAYMENT_ONE_TIME_PAID,
    PAYMENT_ONE_TIME_TOOK,
    PAYMENT_ALLOWANCE,
    PAYMENT_BONUS,
    PAYMENT_DEDUCTIONS,
    PAYMENT_LOAN,
    PAYMENT_ATTENDANCE_LATE_FINE,
    PAYMENT_ATTENDANCE_OVERTIME,
    PAYMENT_OPENING_BALANCE_ADVANCE,
    PAYMENT_OPENING_BALANCE_PENDING,
    PAYMENT_SALARY,

    // Used when the salary for that day is already calculated and
    // we update data like Absent, holiday or present for that day.
    // Then we take the last amount paid in salary
    // and reverse that amount and
    // then add the new salary back again.
    PAYMENT_SALARY_REVERSAL,


    NONE,
}
