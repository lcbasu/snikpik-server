package com.server.common.enums

import com.server.common.model.MediaDetailsV2

enum class ProfileType(
    val category: ProfileCategory,
    val displayName: String,
    val media: MediaDetailsV2
) {
    ARCHITECT_AND_BUILDING_DESIGNERS(
        ProfileCategory.PROFESSIONAL,
        "Architect & Building Designers",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
    INTERIOR_DESIGNERS_AND_DECORATORS(
        ProfileCategory.PROFESSIONAL,
        "Interior Designers & Decorators",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
    CIVIL_ENGINEERS_AND_CONTRACTORS(
        ProfileCategory.PROFESSIONAL,
        "Civil Engineers & Contractors",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
    DESIGN_BUILD_FIRMS(
        ProfileCategory.PROFESSIONAL,
        "Design - Build Firms",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
    LANDSCAPING(
        ProfileCategory.PROFESSIONAL,
        "Landscaping",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
    HOME_BUILDERS_AND_CONSTRUCTION_COMPANIES(
        ProfileCategory.PROFESSIONAL,
        "Home Builders & Construction Companies",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
    REAL_ESTATE_PHOTOGRAPHY(
        ProfileCategory.PROFESSIONAL,
        "Real Estate Photography",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
    FLOORING_AND_CARPET(
        ProfileCategory.PROFESSIONAL,
        "Flooring & Carpet",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
    SANITARY(
        ProfileCategory.PROFESSIONAL,
        "Sanitary",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
    STEEL_WORKS_PRO(
        ProfileCategory.PROFESSIONAL,
        "Steel Works",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
    ALUMINIUM_FABRICATION(
        ProfileCategory.PROFESSIONAL,
        "Aluminium Fabrication",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
    CARPENTERS(
        ProfileCategory.PROFESSIONAL,
        "Carpenters",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
    PAINTER(
        ProfileCategory.PROFESSIONAL,
        "Painter",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
    MASONS(
        ProfileCategory.PROFESSIONAL,
        "Masons",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
    TILE_STONE_AND_COUNTER_TOP(
        ProfileCategory.PROFESSIONAL,
        "Tile, Stone, and Counter-top",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
    GYPSUM_WORK(
        ProfileCategory.PROFESSIONAL,
        "Gypsum work",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
    MURAL_PAINTING(
        ProfileCategory.PROFESSIONAL,
        "Mural Painting",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
    WOODEN_FLOORING(
        ProfileCategory.PROFESSIONAL,
        "Wooden Flooring",
        MediaDetailsV2(
            media = emptyList()
        )
    ),

    ARCHITECTURAL_PHOTOGRAPHY(
        ProfileCategory.PROFESSIONAL,
        "Architectural Photography",
        MediaDetailsV2(
            media = emptyList()
        )
    ),

    // Suppliers

    APPLIANCES(
        ProfileCategory.SUPPLIER,
        "Appliances",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
    DECORS(
        ProfileCategory.SUPPLIER,
        "Decors",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
    ELECTRICAL(
        ProfileCategory.SUPPLIER,
        "Electrical",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
    FURNITURE(
        ProfileCategory.SUPPLIER,
        "Furniture",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
    LIGHTS(
        ProfileCategory.SUPPLIER,
        "Lights",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
    STEEL_WORKS_SUP(
        ProfileCategory.SUPPLIER,
        "Steel Works",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
    HARDWARE_AND_SANITARY(
        ProfileCategory.SUPPLIER,
        "Hardware and Sanitary",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
    PAINTS(
        ProfileCategory.SUPPLIER,
        "Paints",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
    DOOR_LOCKS_AND_HANDLES(
        ProfileCategory.SUPPLIER,
        "Door locks and Handles",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
    BUILDING_MATERIALS(
        ProfileCategory.SUPPLIER,
        "Building materials",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
    FLOORING(
        ProfileCategory.SUPPLIER,
        "Flooring",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
    ROOFING(
        ProfileCategory.SUPPLIER,
        "Roofing",
        MediaDetailsV2(emptyList())
    ),

    // Owner
    HOME_OWNER(
        ProfileCategory.OWNER,
        "Home Owner",
        MediaDetailsV2(
            media = emptyList()
        )
    ),
}

fun getSortedProfileTypes(): List<ProfileType> {
    return ProfileType.values().sortedBy { it.displayName }
}
