package com.dukaankhata.server.model

data class SalarySlipForHTML(
    val employeeName: String,
    val paymentDetails: List<SalarySlipPaymentDetailForHTML>,
    val totalPaymentAmount: String,
    val totalDeductionsAmount: String,
    val employerName: String,
    val employerContact: String
)

data class SalarySlipPaymentDetailForHTML(
    val dateTime: String,
    val type: String,
    val description: String,
    val amount: String
)
