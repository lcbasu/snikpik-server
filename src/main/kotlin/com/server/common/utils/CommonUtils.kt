package com.server.common.utils

object CommonUtils {
    var STRING_SEPARATOR = "_-_"

    fun getStringWithOnlyCharOrDigit(str: String): String {
        return str.filter { it.isLetterOrDigit() }
    }
}
