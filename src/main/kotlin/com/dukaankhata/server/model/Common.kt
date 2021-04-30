import com.dukaankhata.server.entities.CartItem
import com.dukaankhata.server.entities.ProductOrder

data class UpdatedCartData (
    val updatedCartItem: CartItem,
    val updatedProductOrder: ProductOrder,
    val productOrderCartItems: List<CartItem>
)

data class MigratedCartData (
    val fromProductOrder: ProductOrder,
    val toProductOrder: ProductOrder,
    val migratedCartItems: List<CartItem>
)
