import com.server.dk.entities.Employee
import java.time.LocalDateTime

data class EmployeeWorkingDetailsForMonthWithDate(
    val employee: Employee,
    val withDate: LocalDateTime,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val workingDays: Int,
)
