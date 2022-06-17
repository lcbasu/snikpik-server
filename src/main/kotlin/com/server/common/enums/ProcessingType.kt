package com.server.common.enums

enum class ProcessingType {
    NO_PROCESSING,
    REFRESH,


    /**
     * This is required in cases where data was indexed for some other partition and now that needs to change
     * Like when user moves from one profile to another or
     * when post category is changed from o
     * */
    DELETE_AND_REFRESH,
}
