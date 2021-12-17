package com.server.dk.jobs

import com.server.dk.entities.Company
import com.server.dk.provider.CompanyProvider
import org.quartz.JobDataMap
import org.quartz.JobExecutionContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.quartz.QuartzJobBean

class TakeShopOnlineJob: QuartzJobBean() {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var companyProvider: CompanyProvider

    override fun executeInternal(context: JobExecutionContext) {
        takeShopOnline(context.mergedJobDataMap)
    }

    private fun takeShopOnline(jobDataMap: JobDataMap) {
        val companyId = jobDataMap.getString("id")
        val company: Company = companyProvider.getCompany(companyId) ?: error("Could not find company for companyId: $companyId")
        companyProvider.takeShopOnline(company)
        logger.info("Shop taken online for companyId: ${company.id}")
    }
}
