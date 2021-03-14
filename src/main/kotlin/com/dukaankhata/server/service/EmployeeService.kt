package com.dukaankhata.server.service

import com.dukaankhata.server.dto.SaveEmployeeRequest
import com.dukaankhata.server.dto.SavedEmployeeResponse

open class EmployeeService {
    open fun saveEmployee(saveEmployeeRequest: SaveEmployeeRequest): SavedEmployeeResponse? = null
    open fun getEmployee(): SavedEmployeeResponse? = null
}
