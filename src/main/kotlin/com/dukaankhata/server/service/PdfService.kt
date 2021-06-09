package com.dukaankhata.server.service

import java.io.File

abstract class PdfService {
    abstract fun generateSamplePdf(): File
    abstract fun generatePdfForData(templateName: String, variableName: String, dataForVariableName: Any): File
}
