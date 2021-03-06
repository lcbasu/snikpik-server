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

    // Only used for filtering
    // But user should not be saving any post with category ALL
    ALL(
        CategoryGroupV2.HOME,
        0,
        "All",
        MediaDetailsV2(emptyList())
    ),

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
        2,
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
        3,
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
        4,
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
        5,
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
        6,
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
        7,
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
        8,
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
        9,
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
        10,
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
        11,
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
        12,
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
        13,
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
        14,
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
        15,
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
        16,
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
        17,
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
    DIY(
        CategoryGroupV2.HOME,
        18,
        "DIY (Do It Yourself)",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/CategoryImages/DIY.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    DOOR_DESIGN(
        CategoryGroupV2.HOME,
        19,
        "Door Design",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/CategoryImages/DOOR_DESIGN.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    DECOR(
        CategoryGroupV2.HOME,
        20,
        "Decor",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/CategoryImages/DECOR.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    MATERIALS(
        CategoryGroupV2.HOME,
        21,
        "Materials",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/CategoryImages/MATERIALS.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    BATHROOM(
        CategoryGroupV2.HOME,
        22,
        "Bathroom",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/CategoryImages/BATHROOM.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    HOME_TOUR(
        CategoryGroupV2.HOME,
        23,
        "Home Tour",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/CategoryImages/HOME_TOUR.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    SMART_HOME(
        CategoryGroupV2.HOME,
        24,
        "Smart Home",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/CategoryImages/SMART_HOME.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
    STAIRS(
        CategoryGroupV2.HOME,
        25,
        "Stairs",
        MediaDetailsV2(listOf(
            SingleMediaDetail(
                mediaUrl = "https://d2qrqijxy3rkcj.cloudfront.net/assets01/AppData/CategoryImages/STAIRS.png",
                mimeType = "image",
                mediaType = MediaType.IMAGE,
                width = 3222,
                height = 2327,
                mediaQualityType = MediaQualityType.HIGH,
            )
        ))
    ),
}
