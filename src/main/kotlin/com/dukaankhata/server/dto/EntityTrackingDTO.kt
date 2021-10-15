import com.dukaankhata.server.dto.*
import com.dukaankhata.server.entities.Collection
import com.dukaankhata.server.entities.EntityTracking
import com.dukaankhata.server.entities.getTrackingData
import com.dukaankhata.server.enums.EntityType
import com.dukaankhata.server.enums.TrackingType

data class EntityInteractionRequest(
    var entityType: EntityType,
    val trackingType: TrackingType,
    val companyServerIdOrUsername: String? = null,
    val productId: String? = null,
    val collectionId: String? = null,
    val productVariantId: String? = null,
    val trackingData: TrackingData? = null,
)

data class SavedEntityTrackingResponse(
    var serverId: String,
    var entityType: EntityType,
    var trackingType: TrackingType,
    var trackingData: TrackingData?,
    var product: SavedProductResponse?,
    var productVariant: SavedProductVariantResponse?,
    var collection: SavedCollectionResponse?,
    var company: SavedCompanyResponse?,
    var addedBy: SavedUserResponse?,
)

fun EntityTracking.toSavedEntityTrackingResponse(): SavedEntityTrackingResponse {
    this.apply {
        return SavedEntityTrackingResponse(
            serverId = id,
            entityType = entityType,
            trackingType = trackingType,
            trackingData = getTrackingData(),
            company = company!!.toSavedCompanyResponse(),
            addedBy = addedBy!!.toSavedUserResponse(),
            product = product?.toSavedProductResponse(),
            productVariant = productVariant?.toSavedProductVariant(),
            collection = collection?.toSavedCollectionResponse()
        )
    }
}
