import com.server.dk.entities.CartItem
import com.server.dk.entities.ProductOrder

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
