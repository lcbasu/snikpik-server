package com.server.ud.dao.user

import com.server.common.enums.ProfileCategory
import com.server.common.enums.ProfileType
import com.server.ud.entities.user.PointerForUsersInMarketplace
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PointerForUsersInMarketplaceRepository : CassandraRepository<PointerForUsersInMarketplace?, String?> {

    @Query("select * from pointer_for_users_in_marketplace where profile_category = ?0 and profile_type = ?1 and zipcode = ?2")
    fun findAllForGivenData(profileCategory: ProfileCategory, profileType: ProfileType, zipcode: String): List<PointerForUsersInMarketplace>

}
