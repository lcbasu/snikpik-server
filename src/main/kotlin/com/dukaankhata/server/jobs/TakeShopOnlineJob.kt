package com.dukaankhata.server.jobs

import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.utils.CompanyUtils
import io.sentry.Sentry
import org.quartz.JobDataMap
import org.quartz.JobExecutionContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.quartz.QuartzJobBean

class TakeShopOnlineJob: QuartzJobBean() {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var companyUtils: CompanyUtils

    override fun executeInternal(context: JobExecutionContext) {
        takeShopOnline(context.mergedJobDataMap)
    }

    private fun takeShopOnline(jobDataMap: JobDataMap) {
        val companyId = jobDataMap.getString("id")
        val company: Company = companyUtils.getCompany(companyId) ?: error("Could not find company for companyId: $companyId")
        companyUtils.takeShopOnline(company)
        logger.info("Shop taken online for companyId: ${company.id}")
        Sentry.captureMessage("Shop taken online for companyId: ${company.id}")
    }
}
