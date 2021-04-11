import com.dukaankhata.server.entities.Payment

data class SalaryReversal (
    val revertedSalaryPayment: Payment,
    val newSalaryPayment: Payment,
)
