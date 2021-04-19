package com.dukaankhata.server.enums

enum class ProductUnit {
    KG, //1000 grams // Sold in weight quantity -> For rice, pulse etc.
    GRAM, // 1 grams
    DECIGRAM, // 0.1 grams
    CENTIGRAM, //0.01 grams
    MILLIGRAM,// 0.001 grams


    METER, // Sold in Length quantity -> For Clothes, Land etc.
    DECIMETER,
    CENTIMETER,
    MILLIMETER,

    SQUARE_METER, // Area like land and apartment
    SQUARE_FOOT, // Area like land and apartment

    LITRE, // Sold in liquid quantity -> For milk, oil exc.
    DECILITRE,
    CENTILITRE,
    MILLILITRE,


    HOUR, // Sold in time quantity -> For Online Consultation, Doctor booking etc.
    MINUTE,

    PIECE, // Sold in pieces  -> For mobile phone, TV etc.
}
