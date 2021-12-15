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
        MediaDetailsV2(emptyList())
    ),
    CARS_BUY_SELL(
        "Buy and Sell Cars",
        MediaDetailsV2(emptyList())
    ),
    MOBILE_BUY_SELL(
        "Buy and Sell Mobiles",
        MediaDetailsV2(emptyList())
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
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/CategoryImages/EXTERIOR.png",
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
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/CategoryImages/INTERIOR.png",
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
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/CategoryImages/KITCHEN.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    BEDROOM(
        CategoryGroupV2.HOME,
        3,
        "Bedroom",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/CategoryImages/BEDROOM.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    LIVING(
        CategoryGroupV2.HOME,
        4,
        "Living",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/CategoryImages/LIVING.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    FLOORING(
        CategoryGroupV2.HOME,
        5,
        "Flooring",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/CategoryImages/FLOORING.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    FURNITURE(
        CategoryGroupV2.HOME,
        5,
        "Furniture",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/CategoryImages/FURNITURE.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    PAINT(
        CategoryGroupV2.HOME,
        5,
        "Paint",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/CategoryImages/PAINT.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    DOORS(
        CategoryGroupV2.HOME,
        5,
        "Doors",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/CategoryImages/DOORS.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    WINDOWS(
        CategoryGroupV2.HOME,
        5,
        "Windows",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/CategoryImages/WINDOWS.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    LIGHTING(
        CategoryGroupV2.HOME,
        5,
        "Lighting",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/CategoryImages/LIGHTING.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    GARDEN(
        CategoryGroupV2.HOME,
        5,
        "Garden",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/CategoryImages/GARDEN.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    KIDS_ROOM(
        CategoryGroupV2.HOME,
        5,
        "Kids Room",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/CategoryImages/KIDS_ROOM.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    ROOFING(
        CategoryGroupV2.HOME,
        5,
        "Roofing",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/CategoryImages/ROOFING.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    OFFICE(
        CategoryGroupV2.HOME,
        5,
        "Office",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/CategoryImages/OFFICE.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    BEFORE_AND_AFTER(
        CategoryGroupV2.HOME,
        5,
        "Before & After",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/CategoryImages/BEFORE_AND_AFTER.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    HOUSE_ARCHITECTURE_PLAN(
        CategoryGroupV2.HOME,
        5,
        "Plan",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/CategoryImages/HOUSE_ARCHITECTURE_PLAN.png",
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
        MediaDetailsV2(emptyList())
    ),
}
