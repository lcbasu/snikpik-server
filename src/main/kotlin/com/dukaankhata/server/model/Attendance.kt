import com.dukaankhata.server.entities.Employee
import com.dukaankhata.server.enums.AttendanceType
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
