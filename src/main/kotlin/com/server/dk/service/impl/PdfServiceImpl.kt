package com.server.dk.service.impl

import com.server.dk.model.Student
import com.server.dk.service.PdfService
import com.server.dk.utils.DateUtils
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring5.SpringTemplateEngine
import org.xhtmlrenderer.pdf.ITextRenderer
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

@Service
class PdfServiceImpl: PdfService() {

    @Autowired
    private lateinit var templateEngine: SpringTemplateEngine

    override fun generateSamplePdf(): File =
        runBlocking {
            val context = Context()
            context.setVariable("students", getStudents())
            val html = async { renderHTMLUsingTemplate("pdf_students", context) }
            val htmlAssetsPath = "/pdf-resources/"
            renderPdf(html, htmlAssetsPath)
        }

    override fun generatePdfForData(templateName: String, variableName: String, dataForVariableName: Any): File =
        runBlocking {
            val context = Context()
            context.setVariable(variableName, dataForVariableName)
            val html = async { renderHTMLUsingTemplate(templateName, context) }
            val htmlAssetsPath = "/pdf-resources/"
            renderPdf(html, htmlAssetsPath)
        }

    private fun renderHTMLUsingTemplate(templateName: String, contextWithVariablesValues: Context): String {
        return templateEngine.process(templateName, contextWithVariablesValues)
    }

    private suspend fun renderPdf(HTML: Deferred<String>, htmlAssetsPath: String, prefix: String? = null): File {
        val fileNamePrefix = (prefix ?: "dk").plus("_").plus(DateUtils.dateTimeNow().toString())
        val file = File.createTempFile(fileNamePrefix, ".pdf")
        val outputStream: OutputStream = FileOutputStream(file)
        val renderer = ITextRenderer(20f * 4f / 3f, 20)
        renderer.setDocumentFromString(HTML.await(), ClassPathResource(htmlAssetsPath).url.toExternalForm())
        renderer.layout()
        renderer.createPDF(outputStream)
        outputStream.close()
        file.deleteOnExit()
        return file
    }

    private fun getStudents(): List<Student> {
        return listOf(
            Student(
                id = 1,
                name = "ABC",
                lastName = "XYZ",
                active = true,
                birthday = DateUtils.dateTimeNow().toLocalDate(),
                nationality = "IN",
                university = "IIT-R"
            )
        )
    }
}
