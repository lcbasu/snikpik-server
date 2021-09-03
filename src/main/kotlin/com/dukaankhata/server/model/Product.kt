import com.dukaankhata.server.enums.VariantInfoType
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

data class VariantInfo(
    var type: VariantInfoType, // Like 'Red' in case of color and 'Medium' in case of size
    var title: String?, // Like 'Red' in case of color and 'Medium' in case of size
    var code: String?, // Like '#ffffff' in case of color or 'M' in case of size
    var image: String? // Image to represent color or size
)

data class VariantInfos(
    val infos: List<VariantInfo>
)

fun VariantInfos.convertToString(): String {
    this.apply {
        return jacksonObjectMapper().writeValueAsString(this)
    }
}
