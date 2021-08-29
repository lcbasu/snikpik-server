package com.dukaankhata.server.enums

import com.dukaankhata.server.model.MediaDetails

enum class CategoryGroup(
    val id: String, // DO NOT CHANGE THE ID. EVER.
    val displayName: String,
    val description: String,
    val mediaDetails: MediaDetails
) {
    ElectronicsAndAppliances(
        "ElectronicsAndAppliances",
        "Electronics & Appliances",
        "Mobile accessories, IT & Accessories, Electrical appliances, etc.",
        MediaDetails(
            media = emptyList()
        )
    ),
//    Electrical(
//        "Electrical",
//        "Electrical",
//        "Switches, wires, lights & accessories etc.",
//        MediaDetails(
//            media = emptyList()
//        )
//    ),
    Clothing(
        "Clothing",
        "Clothing & Lifestyle",
        "Women's, Men's, Kid's wear, Fabric etc.",
        MediaDetails(
            media = emptyList()
        )
    ),
    LuggageAndBackpack(
        "LuggageAndBackpack",
        "Luggage & Backpacks",
        "Luggage & Backpacks",
        MediaDetails(
            media = emptyList()
        )
    ),
//    FoodAndFMCG(
//        "FoodAndFMCG",
//        "Food & FMCG",
//        "Food & FMCG",
//        MediaDetails(
//            media = emptyList()
//        )
//    ),
//    Pharma(
//        "Pharma",
//        "Medicines and Pharma",
//        "OTC/FMCG, Generics, Ethicals, Medical devices",
//        MediaDetails(
//            media = emptyList()
//        )
//    ),
    HomeAndKitchen(
        "HomeAndKitchen",
        "Home & Kitchen",
        "Steel , Aluminium, Copper utensils etc",
        MediaDetails(
            media = emptyList()
        )
    ),
    Footwear(
        "Footwear",
        "Footwear",
        "Women's, Men's and Kid's footwear",
        MediaDetails(
            media = emptyList()
        )
    ),
    ToysAndBabyCare(
        "ToysAndBabyCare",
        "Toys & Baby Care",
        "Toys & Baby Care",
        MediaDetails(
            media = emptyList()
        )
    ),
//    Hardware(
//        "Hardware",
//        "Hardware",
//        "Hardware",
//        MediaDetails(
//            media = emptyList()
//        )
//    ),
//    Utilities(
//        "Utilities",
//        "Utilities",
//        "Utilities item for home, office, kitchen etc.",
//        MediaDetails(
//            media = emptyList()
//        )
//    ),
    General(
        "General",
        "General Store",
        "Sell anything and everything",
        MediaDetails(
            media = emptyList()
        )
    )
}
