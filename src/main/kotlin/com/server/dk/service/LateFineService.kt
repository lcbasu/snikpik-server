package com.server.dk.service

import com.server.dk.dto.SaveLateFineRequest
import com.server.dk.dto.SavedLateFineResponse

abstract class LateFineService {
    abstract fun saveLateFine(saveLateFineRequest: SaveLateFineRequest): SavedLateFineResponse?
}
