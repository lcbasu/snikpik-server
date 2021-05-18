import com.dukaankhata.server.entities.Employee
import com.dukaankhata.server.entities.Payment

data class SalaryReversal (
    val revertedSalaryPayment: Payment,
    val newSalaryPayment: Payment,
)

data class MonthPayment(
    val employee: Employee,
    val monthNumber: Int,
    // This might change from month to month ro date to date
    // TODO: So add a table to keep track of all salary updates and
    // only one will be the active salary for that employee
    val actualSalary: Long,
    val salaryAmount: Long,
    val paymentsAmount: Long,
    val closingBalance: Long,
)

data class DailyPayment(
    val employee: Employee,
    val forDate: String,
    val salaryAmount: Long,
    val paymentsAmount: Long,
)
