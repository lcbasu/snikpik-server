package com.server.ud.controller

import com.server.ud.dto.FakePostRequest
import com.server.ud.dto.PaginatedRequest
import com.server.ud.dto.SavePostRequest
import com.server.ud.dto.SavedPostResponse
import com.server.ud.entities.post.Post
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.service.marketplace.MarketplaceService
import com.server.ud.service.post.PostService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("ud/post")
class MarketplaceController {

    @Autowired
    private lateinit var marketplaceService: MarketplaceService

//    @RequestMapping(value = ["/getProfessionals"], method = [RequestMethod.GET])
//    fun getProfessionals(@RequestParam limit: Int, @RequestParam pagingState: String?): CassandraPageV2<Post?>? {
//        return marketplaceService.getProfessionals(
//            PaginatedRequest(
//                limit,
//                pagingState
//            )
//        )
//    }
//
//    @RequestMapping(value = ["/getPost"], method = [RequestMethod.GET])
//    fun getPost(@RequestParam postId: String): Post? {
//        return postService.getPost(postId)
//    }
}
