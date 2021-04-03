package com.dukaankhata.server.service

import com.dukaankhata.server.dto.SaveLateFineRequest
import com.dukaankhata.server.dto.SavedLateFineResponse

abstract class LateFineService {
    abstract fun saveLateFine(saveLateFineRequest: SaveLateFineRequest): SavedLateFineResponse?
}
