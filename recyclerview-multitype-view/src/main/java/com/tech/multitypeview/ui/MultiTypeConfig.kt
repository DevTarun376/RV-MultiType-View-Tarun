package com.tech.multitypeview.ui

import com.tech.multitypeview.controller.Paginator

/**
 * All tunable knobs for [MultiTypeRecyclerManager] in one place.
 *
 * @param pageSize                 Items loaded per page (initial + each scroll trigger).
 * @param phoneGridColumns         Grid span count on phones (smallestScreenWidthDp < 600).
 * @param tabletGridColumns        Grid span count on tablets (smallestScreenWidthDp >= 600).
 * @param scrollPrefetchThreshold  Start loading the next page when this many items remain
 *                                 before the last one. 0 = load only when last item is visible.
 * @param showScrollbar            Whether to show a vertical scrollbar on the right edge.
 */
data class MultiTypeConfig(
    val pageSize: Int = Paginator.DEFAULT_PAGE_SIZE,
    val phoneGridColumns: Int = DEFAULT_PHONE_COLUMNS,
    val tabletGridColumns: Int = DEFAULT_TABLET_COLUMNS,
    val scrollPrefetchThreshold: Int = DEFAULT_PREFETCH_THRESHOLD,
    val showScrollbar: Boolean = true,
) {
    companion object {
        const val DEFAULT_PHONE_COLUMNS = 3
        const val DEFAULT_TABLET_COLUMNS = 4
        const val DEFAULT_PREFETCH_THRESHOLD = 5
    }

    init {
        require(pageSize > 0) { "pageSize must be > 0, got $pageSize" }
        require(phoneGridColumns > 0) { "phoneGridColumns must be > 0, got $phoneGridColumns" }
        require(tabletGridColumns > 0) { "tabletGridColumns must be > 0, got $tabletGridColumns" }
        require(scrollPrefetchThreshold >= 0) { "scrollPrefetchThreshold must be >= 0, got $scrollPrefetchThreshold" }
    }
}
