package com.server.sp.controller

import com.server.sp.dto.DeleteMomentRequest
import com.server.sp.dto.SaveSpMomentRequest
import com.server.sp.dto.SavedSpMomentResponse
import com.server.sp.dto.UpdateMomentRequest
import com.server.sp.entities.user.SpMomentsCountByUser
import com.server.sp.service.moment.SpMomentService
import io.micrometer.core.annotation.Timed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@Timed
@RequestMapping("sp/moment")
class SpMomentController {

    @Autowired
    private lateinit var spMomentService: SpMomentService

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveMoment(@RequestBody request: SaveSpMomentRequest): SavedSpMomentResponse? {
        return spMomentService.saveMoment(request)
    }

    @RequestMapping(value = ["/delete"], method = [RequestMethod.POST])
    fun deleteMoment(@RequestBody request: DeleteMomentRequest): Boolean {
        return spMomentService.deleteMoment(request)
    }


    @RequestMapping(value = ["/update"], method = [RequestMethod.POST])
    fun updateMoment(@RequestBody request: UpdateMomentRequest): SavedSpMomentResponse? {
        return spMomentService.updateMoment(request)
    }

    @RequestMapping(value = ["/getMoment"], method = [RequestMethod.GET])
    fun getMoment(@RequestParam momentId: String): SavedSpMomentResponse? {
        return spMomentService.getMoment(momentId)
    }

    @RequestMapping(value = ["/getMomentsCountByUser"], method = [RequestMethod.GET])
    fun getMomentsCountByUser(@RequestParam userId: String): SpMomentsCountByUser? {
        return spMomentService.getMomentsCountByUser(userId)
    }
}
