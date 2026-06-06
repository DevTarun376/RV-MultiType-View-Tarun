package com.tech.multitypeview.controller

import com.tech.multitypeview.model.MultiTypeItem

internal class Paginator(private val pageSize: Int = DEFAULT_PAGE_SIZE) {

    companion object {
        const val DEFAULT_PAGE_SIZE = 50
    }

    private var source: List<MultiTypeItem> = emptyList()
    private var loadedCount: Int = 0

    fun setSource(list: List<MultiTypeItem>, loadAtLeast: Int = 0) {
        source = list
        loadedCount = minOf(maxOf(pageSize, loadAtLeast), list.size)
    }

    fun currentPage(): List<MultiTypeItem> = source.take(loadedCount)

    fun loadNext(): List<MultiTypeItem>? {
        if (loadedCount >= source.size) return null
        loadedCount = minOf(loadedCount + pageSize, source.size)
        return source.take(loadedCount)
    }

    fun hasMore(): Boolean = loadedCount < source.size

    fun clear() {
        source = emptyList()
        loadedCount = 0
    }
}
