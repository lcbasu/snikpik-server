package com.server.ud.dao.post

import com.server.common.utils.DateUtils
import com.server.ud.entities.post.PostsByCategory
import com.server.ud.entities.post.PostsByCategoryTracker
import com.server.ud.enums.CategoryV2
import com.server.ud.enums.PostType
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface PostsByCategoryRepository : CassandraRepository<PostsByCategory?, String?> {
//    @AllowFiltering
//    fun findAllByCategoryId(categoryV2: CategoryV2, pageable: Pageable): Slice<PostsByCategory>

    fun findAllByCategoryIdAndPostType(categoryV2: CategoryV2, postType: PostType, pageable: Pageable): Slice<PostsByCategory>
    fun findAllByCategoryIdAndPostTypeAndCreatedAtAndPostId(categoryV2: CategoryV2, postType: PostType, createdAt: Instant, postId: String): List<PostsByCategory>

    fun deleteAllByCategoryIdAndPostTypeAndCreatedAtAndPostId(categoryV2: CategoryV2, postType: PostType, createdAt: Instant, postId: String)


//    @AllowFiltering
//    fun findAllByPostId(postId: String): List<PostsByCategory>
//
//    @AllowFiltering
//    fun deleteAllByPostId(postId: String)


//    @Query("SELECT * FROM posts_by_category where post_id = ?0 allow filtering")
//    fun findAllByPostId_V2(postId: String): List<PostsByCategory>

//    fun findAllByCategoryIdAndPostTypeAndCreatedAtAndPostIdAndUserId(categoryV2: CategoryV2, postType: PostType, createdAt: Instant, postId: String, userId: String): List<PostsByCategory>

//    @Query("SELECT * FROM bookmarked_posts_by_user where post_id = ?0 allow filtering")
//    fun findAllByPostId_V2(postId: String): List<BookmarkedPostsByUser>
}

@Repository
interface PostsByCategoryTrackerRepository : CassandraRepository<PostsByCategoryTracker?, String?> {
    fun findAllByPostId(postId: String, pageable: Pageable): Slice<PostsByCategoryTracker>
}
