package com.server.ud.dao.user_activity

import com.server.ud.entities.user_activity.UserActivity
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface UserActivitiesRepository : CassandraRepository<UserActivity?, String?> {
}
