import com.dukaankhata.server.entities.CartItem
import com.dukaankhata.server.entities.ProductOrder

data class UpdatedCartData (
    val updatedCartItem: CartItem,
    val updatedProductOrder: ProductOrder,
    val productOrderCartItems: List<CartItem>
)
