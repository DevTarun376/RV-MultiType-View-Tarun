package com.tech.multitypeview.util

internal object MediaTypeHelper {

    fun isVideoByExtension(filePath: String): Boolean =
        filePath.endsWith(".mp4", ignoreCase = true) ||
                filePath.endsWith(".mkv", ignoreCase = true) ||
                filePath.endsWith(".avi", ignoreCase = true) ||
                filePath.endsWith(".mov", ignoreCase = true) ||
                filePath.endsWith(".wmv", ignoreCase = true) ||
                filePath.endsWith(".hevc", ignoreCase = true) ||
                filePath.endsWith(".flv", ignoreCase = true)

    fun isPdfExtension(filePath: String): Boolean =
        filePath.endsWith(".pdf", ignoreCase = true)

    fun isEmlExtension(filePath: String): Boolean =
        filePath.endsWith(".eml", ignoreCase = true)
}
