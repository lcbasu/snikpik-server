package com.server.sp.service.moment

import com.server.sp.dto.DeleteMomentRequest
import com.server.sp.dto.SaveSpMomentRequest
import com.server.sp.dto.SavedSpMomentResponse
import com.server.sp.dto.UpdateMomentRequest
import com.server.sp.entities.user.SpMomentsCountByUser

abstract class SpMomentService {
    abstract fun saveMoment(request: SaveSpMomentRequest): SavedSpMomentResponse?
    abstract fun deleteMoment(request: DeleteMomentRequest): Boolean
    abstract fun updateMoment(request: UpdateMomentRequest): SavedSpMomentResponse?
    abstract fun getMoment(momentId: String): SavedSpMomentResponse?
    abstract fun getMomentsCountByUser(userId: String): SpMomentsCountByUser?
}
