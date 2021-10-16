import com.server.dk.entities.Employee
import com.server.dk.enums.AttendanceType
import java.time.LocalDateTime

data class AttendanceReportForEmployee (
    val employee: Employee,
    val startDate: String,
    val endDate: String,
    val totalDay: Int,
    val presentDays: Int,
    val absentDays: Int,
    val halfDays: Int,
    val paidHolidays: Int,
    val nonPaidHolidays: Int,
    val overtimeMinutes: Int,
    val overtimeAmountInPaisa: Long,
    val lateFineMinutes: Int,
    val lateFineAmountInPaisa: Long,
    val companyWorkingMinutesPerDay: Int,
)

data class AttendancePunchData (
    val attendanceType: AttendanceType,
    val totalMinutes: Int,
    val updatedAt: LocalDateTime
)

data class ReportDuration(
    val startTime: LocalDateTime,
    val endTime: LocalDateTime
)

data class AttendanceInfoData (
    val attendanceType: AttendanceType,
    val displayText: String,
    val dateNumber: Int,
    val dateText: String
)
