package com.server.ud.dao.post

import com.server.ud.entities.post.BookmarkedPostsByUser
import com.server.ud.entities.post.BookmarkedPostsByUserTracker
import com.server.ud.enums.PostType
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface BookmarkedPostsByUserRepository : CassandraRepository<BookmarkedPostsByUser?, String?> {
    fun findAllByUserId(userId: String, pageable: Pageable): Slice<BookmarkedPostsByUser>

//    @AllowFiltering
    fun deleteAllByUserIdAndPostTypeAndPostId(userId: String, postType: PostType, postId: String)

//    fun findAllByUserIdAndPostTypeAndPostId(userId: String, postType: PostType, postId: String): List<BookmarkedPostsByUser>
//
////    @Query("SELECT * FROM bookmarked_posts_by_user where post_id = ?0 allow filtering")
////    fun findAllByPostId_V2(postId: String): List<BookmarkedPostsByUser>
}

@Repository
interface BookmarkedPostsByUserTrackerRepository : CassandraRepository<BookmarkedPostsByUserTracker?, String?> {
    fun findAllByPostId(postId: String, pageable: Pageable): Slice<BookmarkedPostsByUserTracker>
}
