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
    BEDROOM(
        ProductCategoryV3Group.HOME,
        4,
        "Bedroom",
        "Bedroom",
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
    FURNITURE(
        ProductCategoryV3Group.HOME,
        7,
        "Furniture",
        "Furniture",
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
    DECOR(
        ProductCategoryV3Group.HOME,
        20,
        "Decor",
        "Decor",
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

enum class ProductSubCategory(
    val categories: List<ProductCategoryV3>,
    val placementOrder: Int,
    val displayName: String,
    val subTitle: String,
    val mediaDetails: MediaDetailsV2
) {

    SOFA(
        listOf(ProductCategoryV3.FURNITURE,ProductCategoryV3.DECOR),
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
        listOf(ProductCategoryV3.FURNITURE, ProductCategoryV3.DECOR, ProductCategoryV3.BEDROOM),
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

