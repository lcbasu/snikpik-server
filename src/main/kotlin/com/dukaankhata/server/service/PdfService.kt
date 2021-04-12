package com.dukaankhata.server.service

import com.dukaankhata.server.entities.Employee
import java.io.File

abstract class PdfService {
    abstract fun generateSamplePdf(): File
    abstract fun generatePdfForSalarySlip(employee: Employee, startDate: String, endDate: String): File
}
