package com.server.shop.enums

import com.server.common.enums.MediaQualityType
import com.server.common.enums.MediaType
import com.server.common.model.MediaDetailsV2
import com.server.common.model.SingleMediaDetail

enum class VariantTypeV3 {
    ORIGINAL,
    VARIANT,
}

enum class DimensionUnits {
    CM,
    INCH,
    FEET,
}

enum class WeightUnits {
    KG,
    GRAM,
}

enum class VariantInfoTypeV3 {
    DIMENSION,
    WEIGHT,
    COLOR,
    SCALE_SIZE, // Like Large, Medium
    MATERIAL,
}

enum class SpecificationType(
    val displayName: String,
    val mediaDetails: MediaDetailsV2
) {
    IMAGE_OR_VIDEO_BASED_DESCRIPTION(
        "Media based description",
        MediaDetailsV2(emptyList())
    ),
    KEY_PAIR_DESCRIPTION(
        "Key pair description",
        MediaDetailsV2(emptyList())
    ),

    MEDIA_CAROUSEL(
        "Media carousel",
        MediaDetailsV2(emptyList())
    ),

    PRODUCT_DESCRIPTION_MEDIA_CAROUSEL(
        "Product Description",
        MediaDetailsV2(emptyList())
    ),
}

enum class ProductUnitV3(val rank: Int, val displayName: String)  {
    PIECE(1, "Piece"),
    KG(2, "KG"),
    GRAM(3, "gram"),
    MILLIGRAM(4, "mg"),
    QUINTAL(5, "quintal"),
    TON(6, "ton"),

    METER(7, "Meter"),
    SQUARE_METER(8, "Sq. Meter"),
    CENTIMETER(9, "cm"),
    MILLIMETER(10, "mm"),

    INCH(11, "Inch"),

    FEET(12, "Feet"),
    SQUARE_FEET(13, "Sq. Feet"),

    LITRE(14, "Litre"),
    MILLILITRE(15, "ml"),

    CAPSULE(16, "Capsule"),
    TABLET(17, "Tablet"),

    PLATE(18, "Plate"),

    HOUR(19, "Hour"),
    MINUTE(20, "Minute"),

    ACRE(21, "Acre"),
    KATHA(22, "Katha"),
    BIGHA(23, "Bigha"),
    KILLA(24, "Killa"),
    KANAL(25, "Kanal"),
    CHATHAK(26, "Chathak"),
    GUNTHA(27, "Guntha"),

    YEAR(28, "Year"),
    MONTH(29, "Month"),
    WEEK(30, "Week"),
    DAY(31, "Day"),

    WORK(32, "Work"),

    SERVICE(33, "Service"),

    PACKET(34, "Packet"),

    BOX(35, "Box"),

    DOZEN(36, "Dozen"),
    PAIR(37, "Pair"),

    SET(38, "Set"),

    BUNCH(39, "Bunch"),

    BUNDLE(40, "Bundle"),

    POUND(41, "Pound"),
}

enum class ProductPropertyType {
    ADDITIONAL_UNITS,
    WARRANTY,
    WARRANTY_TILL_DATE,
    WARRANTY_FOR_NEXT_MONTHS,
}

enum class ProductStatusV3 {
    PENDING_APPROVAL,
    DRAFT,
    ACTIVE,
    OUT_OF_STOCK,
    INACTIVE,
    ARCHIVED,
}


enum class ProductVariantStatusV3 {
    PENDING_APPROVAL,
    REJECTED,
    ACTIVE,
    OUT_OF_STOCK,
    INACTIVE,
    ARCHIVED,
}
