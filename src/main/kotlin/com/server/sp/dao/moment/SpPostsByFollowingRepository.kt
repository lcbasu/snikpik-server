package com.server.sp.dao.moment

import com.server.sp.entities.moment.SpMomentsByFollowing
import com.server.sp.entities.moment.SpMomentsByFollowingByFollowingTracker
import com.server.sp.entities.moment.SpMomentsByFollowingByMomentTracker
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface SpMomentsByFollowingRepository : CassandraRepository<SpMomentsByFollowing?, String?> {
}

@Repository
interface SpMomentsByFollowingByMomentTrackerRepository : CassandraRepository<SpMomentsByFollowingByMomentTracker?, String?> {
}

@Repository
interface SpMomentsByFollowingByFollowingTrackerRepository : CassandraRepository<SpMomentsByFollowingByFollowingTracker?, String?> {
}
