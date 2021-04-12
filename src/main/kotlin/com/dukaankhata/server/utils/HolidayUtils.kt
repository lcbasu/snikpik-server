package com.dukaankhata.server.utils

import com.dukaankhata.server.dao.HolidayRepository
import com.dukaankhata.server.entities.*
import com.dukaankhata.server.enums.HolidayType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class HolidayUtils {

    @Autowired
    private lateinit var holidayRepository: HolidayRepository

    @Autowired
    private lateinit var employeeUtils: EmployeeUtils

    fun getHolidayKey(companyId: Long, employeeId: Long, forDate: String): HolidayKey {
        val key = HolidayKey()
        key.companyId = companyId
        key.employeeId = employeeId
        key.forDate = forDate
        return key
    }

    fun getHoliday(company: Company, employee: Employee, forDate: String): Holiday? =
        try {
            val key = getHolidayKey(companyId = company.id, employeeId = employee.id, forDate = forDate)
            holidayRepository.findById(key).get()
        } catch (e: Exception) {
            null
        }

    fun saveOrUpdateHoliday(addedBy: User, company: Company, employee: Employee, forDate: String, holidayType: HolidayType) : Holiday? {
        val key = getHolidayKey(companyId = company.id, employeeId = employee.id, forDate = forDate)
        val holidayOptional = holidayRepository.findById(key)
        val savedHoliday = if (holidayOptional.isPresent) {
            val holiday = holidayOptional.get()
            holiday.addedBy = addedBy
            holiday.holidayType = holidayType
            holiday.lastModifiedAt = DateUtils.dateTimeNow()
            holidayRepository.save(holiday)
        } else {
            val holiday = Holiday()
            holiday.company = company
            holiday.employee = employee
            holiday.id = key
            holiday.holidayType = holidayType
            holiday.addedBy = addedBy
            holidayRepository.save(holiday)
        }

        // Because for today, we will always cover that in the job that will run tomorrow
        if (DateUtils.parseStandardDate(forDate).toLocalDate().atStartOfDay().isBefore(DateUtils.dateTimeNow().toLocalDate().atStartOfDay())) {
            employeeUtils.updateSalary(employee, forDate)
        }

        return savedHoliday
    }

    fun getHolidayForDate(company: Company, forDate: String): List<Holiday> =
        try {
            holidayRepository.getAllHolidaysForDate(company.id, forDate)
        } catch (e: Exception) {
            emptyList()
        }

    fun getHolidayForEmployee(employee: Employee, startTime: LocalDateTime, endTime: LocalDateTime): List<Holiday> =
        try {
            holidayRepository.getHolidayForEmployee(employee.id, startTime, endTime)
        } catch (e: Exception) {
            emptyList()
        }

    fun getHolidayForEmployee(employee: Employee, datesList: List<String>): List<Holiday> =
        try {
            holidayRepository.getHolidayForEmployee(employee.id, datesList)
        } catch (e: Exception) {
            emptyList()
        }

}
