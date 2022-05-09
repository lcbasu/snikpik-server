package com.server.ud.entities.experts

import com.server.common.utils.DateUtils
import com.server.ud.enums.ExpertBookingState
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant
import javax.persistence.EnumType
import javax.persistence.Enumerated

// Uncomment when actively working on this.

//@Table("unbox_experts")
//data class UnboxExpert (
//
//    @PrimaryKeyColumn(name = "expert_user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
//    val expertUserId: String,
//
//    @Column("created_at")
//    @CassandraType(type = CassandraType.Name.TIMESTAMP)
//    var createdAt: Instant = DateUtils.getInstantNow(),
//
//    @Column("hourly_fee_in_paisa")
//    val hourlyFeeInPaisa: Long = 0,
//
//)
//
//@Table("unbox_experts_bookings")
//data class UnboxExpertBooking (
//
//    @PrimaryKeyColumn(name = "booking_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
//    val bookingId: String,
//
//    @Column("expert_user_id")
//    val expertUserId: String,
//
//    @Column("customer_user_id")
//    val customerUserId: String,
//
//    @Column("created_at")
//    @CassandraType(type = CassandraType.Name.TIMESTAMP)
//    var createdAt: Instant = DateUtils.getInstantNow(),
//
//    @Column("hourly_fee_in_paisa_booked_for")
//    val hourlyFeeInPaisaBookedFor: Long = 0,
//
//    @Enumerated(EnumType.STRING)
//    val state: ExpertBookingState = ExpertBookingState.BOOKED,
//
//    )
//
//
//@Table("unbox_experts_bookings_by_expert")
//data class UnboxExpertBookingByExpert (
//
//    @PrimaryKeyColumn(name = "expert_user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
//    val expertUserId: String,
//
//    @PrimaryKeyColumn(name = "booking_id", ordinal = 0, type = PrimaryKeyType.CLUSTERED)
//    val bookingId: String,
//
//    @Column("customer_user_id")
//    val customerUserId: String,
//
//    @Column("created_at")
//    @CassandraType(type = CassandraType.Name.TIMESTAMP)
//    var createdAt: Instant,
//
//    @Column("hourly_fee_in_paisa_booked_for")
//    val hourlyFeeInPaisaBookedFor: Long,
//
//    @Enumerated(EnumType.STRING)
//    val state: ExpertBookingState,
//
//)
//
//
//@Table("unbox_experts_bookings_by_customer")
//data class UnboxExpertBookingByCustomer (
//
//    @PrimaryKeyColumn(name = "customer_user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
//    val customerUserId: String,
//
//    @PrimaryKeyColumn(name = "booking_id", ordinal = 0, type = PrimaryKeyType.CLUSTERED)
//    val bookingId: String,
//
//    @Column("expert_user_id")
//    val expertUserId: String,
//
//    @Column("created_at")
//    @CassandraType(type = CassandraType.Name.TIMESTAMP)
//    var createdAt: Instant,
//
//    @Column("hourly_fee_in_paisa_booked_for")
//    val hourlyFeeInPaisaBookedFor: Long,
//
//    @Enumerated(EnumType.STRING)
//    val state: ExpertBookingState,
//
//)
