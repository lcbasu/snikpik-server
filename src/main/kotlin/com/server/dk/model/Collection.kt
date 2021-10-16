import com.server.dk.entities.Collection
import com.server.dk.entities.Product

data class AllCollectionsWithProductsRaw(
    val collectionsWithProducts: List<CollectionWithProductsRaw>
)

data class CollectionWithProductsRaw(
    val collection: Collection,
    val products: List<Product>
)
