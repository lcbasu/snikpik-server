import com.dukaankhata.server.entities.Employee
import com.dukaankhata.server.entities.Payment

data class SalaryReversal (
    val revertedSalaryPayment: Payment,
    val newSalaryPayment: Payment,
)

data class MonthPayment(
    val employee: Employee,
    val monthNumber: Int,
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
