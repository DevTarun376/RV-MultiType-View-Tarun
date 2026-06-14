package com.tech.multitypeview.model

/** Content kind of a grid item. */
enum class MediaKind {
    NONE,      // non-grid rows (headers, labels)
    IMAGE,
    VIDEO,
    DOCUMENT   // PDF / EML / file attachment
}

data class MultiTypeItem(
    var type: Int? = null,
    var id: String? = null,
    var label: String? = null,
    var isExpanded: Boolean? = null,
    var header: String? = null,
    var mediaKind: MediaKind = MediaKind.NONE,
    var itemUrl: String? = null,
    var name: String? = null,
    var imageIndex: Int? = null,
    var isVisible: Boolean? = true,
    var isSelected: Boolean = false
)
