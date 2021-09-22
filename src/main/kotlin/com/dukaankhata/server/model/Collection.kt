import com.dukaankhata.server.entities.Collection
import com.dukaankhata.server.entities.Product

data class AllCollectionsWithProductsRaw(
    val collectionsWithProducts: List<CollectionWithProductsRaw>
)

data class CollectionWithProductsRaw(
    val collection: Collection,
    val products: List<Product>
)
