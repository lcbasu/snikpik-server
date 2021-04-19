package com.dukaankhata.server.enums

enum class ReadableIdPrefix {
    PRD, // Product
    CLC, // Collection
    ORD; // Order

    fun getPrefix(): String {
        return name.toUpperCase()
    }
}

