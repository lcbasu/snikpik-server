package com.server.sp.service.moment

import com.server.sp.dto.DeleteMomentRequest
import com.server.sp.dto.SaveSpMomentRequest
import com.server.sp.dto.SavedSpMomentResponse
import com.server.sp.dto.UpdateMomentRequest
import com.server.sp.entities.user.SpMomentsCountByUser
import com.server.sp.provider.moment.SpMomentProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SpMomentServiceImpl : SpMomentService() {

    @Autowired
    private lateinit var spMomentProvider: SpMomentProvider

    override fun saveMoment(request: SaveSpMomentRequest): SavedSpMomentResponse? {
        return spMomentProvider.saveMoment(request)
    }

    override fun deleteMoment(request: DeleteMomentRequest): Boolean {
        return spMomentProvider.deleteMoment(request)
    }

    override fun updateMoment(request: UpdateMomentRequest): SavedSpMomentResponse? {
        return spMomentProvider.updateMoment(request)
    }

    override fun getMoment(momentId: String): SavedSpMomentResponse? {
        return spMomentProvider.getMoment(momentId)
    }

    override fun getMomentsCountByUser(userId: String): SpMomentsCountByUser? {
        return spMomentProvider.getMomentsCountByUser(userId)
    }

}
