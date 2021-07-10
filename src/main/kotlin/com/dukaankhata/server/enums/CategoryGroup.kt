package com.dukaankhata.server.enums

import com.dukaankhata.server.model.MediaDetails

enum class CategoryGroup(
    val id: String,
    val displayName: String,
    val mediaDetails: MediaDetails
) {
    ElectronicsAndAppliances(
        "ElectronicsAndAppliances",
        "Electronics & Appliances",
        MediaDetails(
            media = emptyList()
        )
    ),
    Electrical(
        "Electrical",
        "Electrical",
        MediaDetails(
            media = emptyList()
        )
    ),
    Clothing(
        "Clothing",
        "Clothing & Lifestyle",
        MediaDetails(
            media = emptyList()
        )
    ),
    LuggageAndBackpack(
        "LuggageAndBackpack",
        "Luggage & Backpacks",
        MediaDetails(
            media = emptyList()
        )
    ),
    FoodAndFMCG(
        "FoodAndFMCG",
        "Food & FMCG",
        MediaDetails(
            media = emptyList()
        )
    ),
    Pharma(
        "Pharma",
        "Pharma",
        MediaDetails(
            media = emptyList()
        )
    ),
    HomeAndKitchen(
        "HomeAndKitchen",
        "Home & Kitchen",
        MediaDetails(
            media = emptyList()
        )
    ),
    Footwear(
        "Footwear",
        "Footwear",
        MediaDetails(
            media = emptyList()
        )
    ),
    ToysAndBabyCare(
        "ToysAndBabyCare",
        "Toys & Baby Care",
        MediaDetails(
            media = emptyList()
        )
    ),
    Hardware(
        "Hardware",
        "Hardware",
        MediaDetails(
            media = emptyList()
        )
    ),
    Utilities(
        "Utilities",
        "Utilities",
        MediaDetails(
            media = emptyList()
        )
    ),
    Unknown(
        "Unknown",
        "Unknown",
        MediaDetails(
            media = emptyList()
        )
    )
}
