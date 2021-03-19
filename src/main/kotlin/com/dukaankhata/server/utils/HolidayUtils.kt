package com.dukaankhata.server.utils

import com.dukaankhata.server.dao.HolidayRepository
import com.dukaankhata.server.entities.*
import com.dukaankhata.server.enums.HolidayType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class HolidayUtils {

    @Autowired
    private lateinit var holidayRepository: HolidayRepository

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
        if (holidayOptional.isPresent) {
            val holiday = holidayOptional.get()
            holiday.addedBy = addedBy
            holiday.holidayType = holidayType
            return holidayRepository.save(holiday)
        }

        // Save new one if old one does not exist
        return holidayRepository.let {
            val holiday = Holiday()
            holiday.company = company
            holiday.employee = employee
            holiday.id = key
            holiday.holidayType = holidayType
            holiday.addedBy = addedBy
            it.save(holiday)
        }
    }

}
