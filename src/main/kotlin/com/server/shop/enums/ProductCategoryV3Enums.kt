package com.server.shop.enums

import com.server.common.enums.MediaQualityType
import com.server.common.enums.MediaType
import com.server.common.model.MediaDetailsV2
import com.server.common.model.SingleMediaDetail

enum class ProductCategoryV3Group(
    val displayName: String,
    val mediaDetails: MediaDetailsV2
) {
    HOME(
        "Home",
        MediaDetailsV2(emptyList())
    ),
    CARS(
        "Cars",
        MediaDetailsV2(emptyList())
    ),
    MOBILES(
        "Mobiles",
        MediaDetailsV2(emptyList())
    ),
}

enum class ProductCategoryV3(
    val categoryGroup: ProductCategoryV3Group,
    val placementOrder: Int,
    val displayName: String,
    val subTitle: String,
    val mediaDetails: MediaDetailsV2
) {
    APPLIANCES(
        ProductCategoryV3Group.HOME,
        4,
        "Appliances",
        "Appliances & electronics",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/ProductCategoryV3/Images/APPLIANCES_0.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 64,
                height = 56,
                mediaQualityType = MediaQualityType.HIGH,
            ),
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/ProductCategoryV3/Images/APPLIANCES_1.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 64,
                height = 56,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    BUILDING(
        ProductCategoryV3Group.HOME,
        4,
        "Building",
        "Building material",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/ProductCategoryV3/Images/BUILDING_0.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 64,
                height = 56,
                mediaQualityType = MediaQualityType.HIGH,
            ),
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/ProductCategoryV3/Images/BUILDING_1.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 64,
                height = 56,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    DECOR(
        ProductCategoryV3Group.HOME,
        4,
        "Decor",
        "Decor items",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/ProductCategoryV3/Images/DECOR_0.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 64,
                height = 56,
                mediaQualityType = MediaQualityType.HIGH,
            ),
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/ProductCategoryV3/Images/DECOR_1.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 64,
                height = 56,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    ELECTRIC(
        ProductCategoryV3Group.HOME,
        4,
        "Electric",
        "Electric items",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/ProductCategoryV3/Images/ELECTRIC_0.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 64,
                height = 56,
                mediaQualityType = MediaQualityType.HIGH,
            ),
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/ProductCategoryV3/Images/ELECTRIC_1.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 64,
                height = 56,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    FURNITURE(
        ProductCategoryV3Group.HOME,
        4,
        "Furniture",
        "Furniture",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/ProductCategoryV3/Images/FURNITURE_0.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 64,
                height = 56,
                mediaQualityType = MediaQualityType.HIGH,
            ),
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/ProductCategoryV3/Images/FURNITURE_1.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 64,
                height = 56,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    KITCHEN(
        ProductCategoryV3Group.HOME,
        4,
        "Kitchen",
        "Kitchen",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/ProductCategoryV3/Images/KITCHEN_0.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 64,
                height = 56,
                mediaQualityType = MediaQualityType.HIGH,
            ),
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/ProductCategoryV3/Images/KITCHEN_1.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 64,
                height = 56,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    KNOBS_HANDLES(
        ProductCategoryV3Group.HOME,
        4,
        "Knobs and Handles",
        "Knobs and Handles",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/ProductCategoryV3/Images/KNOBS_HANDLES_0.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 64,
                height = 56,
                mediaQualityType = MediaQualityType.HIGH,
            ),
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/ProductCategoryV3/Images/KNOBS_HANDLES_1.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 64,
                height = 56,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    LIGHTS(
        ProductCategoryV3Group.HOME,
        4,
        "Lights",
        "Lights",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/ProductCategoryV3/Images/LIGHTS_0.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 64,
                height = 56,
                mediaQualityType = MediaQualityType.HIGH,
            ),
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/ProductCategoryV3/Images/LIGHTS_1.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 64,
                height = 56,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    PAINTS(
        ProductCategoryV3Group.HOME,
        4,
        "Paints",
        "Paints",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/ProductCategoryV3/Images/PAINTS_0.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 64,
                height = 56,
                mediaQualityType = MediaQualityType.HIGH,
            ),
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/ProductCategoryV3/Images/PAINTS_1.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 64,
                height = 56,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    SANITARY(
        ProductCategoryV3Group.HOME,
        4,
        "Sanitary",
        "Sanitary",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/ProductCategoryV3/Images/SANITARY_0.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 64,
                height = 56,
                mediaQualityType = MediaQualityType.HIGH,
            ),
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/ProductCategoryV3/Images/SANITARY_1.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 64,
                height = 56,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    STEELWORKS(
        ProductCategoryV3Group.HOME,
        4,
        "Steelworks",
        "Steelworks and Fabrication",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/ProductCategoryV3/Images/STEELWORKS_0.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 64,
                height = 56,
                mediaQualityType = MediaQualityType.HIGH,
            ),
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/ProductCategoryV3/Images/STEELWORKS_1.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 64,
                height = 56,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
}

enum class ProductSubCategory(
    val categories: List<ProductCategoryV3>,
    val placementOrder: Int,
    val displayName: String,
    val subTitle: String,
    val mediaDetails: MediaDetailsV2
) {

    SOFA(
        listOf(ProductCategoryV3.FURNITURE, ProductCategoryV3.DECOR),
        1,
        "Sofa",
        "Sofa",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/CategoryImages/EXTERIOR.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    BED(
        listOf(ProductCategoryV3.FURNITURE, ProductCategoryV3.DECOR),
        2,
        "Bed",
        "Bed",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/CategoryImages/EXTERIOR.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
}

enum class ProductVertical(
    // Index 0 -> Primary
    // Index 1 -> Secondary
    // so on
    val subCategories: List<ProductSubCategory>,
    val placementOrder: Int,
    val displayName: String,
    val subTitle: String,
    val mediaDetails: MediaDetailsV2
) {

    // IMPORTANT
    // Copy over from ProductSubCategory as is so that in cases when the vertical is same as the Sub-Category
    // Only keep one value in the list of verticals for these cases
    SOFA(listOf(ProductSubCategory.SOFA),
        1,
        "Sofa",
        "Sofa",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/CategoryImages/EXTERIOR.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    BED(listOf(ProductSubCategory.BED),
        1,
        "Bed",
        "Bed",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/CategoryImages/EXTERIOR.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),


    RECLINER(listOf(ProductSubCategory.SOFA),
        1,
        "Recliner",
        "Recliner",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/CategoryImages/EXTERIOR.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    AMERICAN(listOf(ProductSubCategory.SOFA),
        1,
        "American Sofa",
        "American Sofa",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/CategoryImages/EXTERIOR.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    KING_SIZE_BED(listOf(ProductSubCategory.BED),
        2,
        "King Size Bed",
        "King Size Bed",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/CategoryImages/EXTERIOR.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
}

