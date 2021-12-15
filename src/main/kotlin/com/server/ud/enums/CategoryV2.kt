package com.server.ud.enums

import com.server.common.enums.MediaQualityType
import com.server.common.enums.MediaType
import com.server.common.model.MediaDetailsV2
import com.server.common.model.SingleMediaDetail

enum class CategoryGroupV2(
    val displayName: String,
    val mediaDetails: MediaDetailsV2
) {
    HOME(
        "Home",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/pexels-atbo-245208.jpg",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    CARS_BUY_SELL(
        "Buy and Sell Cars",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/pexels-atbo-245208.jpg",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    MOBILE_BUY_SELL(
        "Buy and Sell Mobiles",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/pexels-atbo-245208.jpg",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
}
enum class CategoryV2(
    val categoryGroup: CategoryGroupV2,
    val placementOrder: Int,
    val displayName: String,
    val mediaDetails: MediaDetailsV2
) {
    EXTERIOR(
        CategoryGroupV2.HOME,
        1,
        "Exterior",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/pexels-atbo-245208.jpg",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    INTERIOR(
        CategoryGroupV2.HOME,
        1,
        "Interior",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/pexels-atbo-245208.jpg",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    KITCHEN(
        CategoryGroupV2.HOME,
        2,
        "Kitchen",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/pexels-atbo-245208.jpg",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),

    // Only used for filtering
    // But user should not be saving any post with category ALL
    ALL(
        CategoryGroupV2.HOME,
        0,
        "All",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/userUploads/USRT4AnvDzKN0Or7IS98FOOLTNzmxN2/pexels-atbo-245208.jpg",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
}
