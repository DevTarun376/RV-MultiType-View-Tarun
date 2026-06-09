package com.tech.multitypeview.controller

import com.tech.multitypeview.model.MultiTypeItem
import com.tech.multitypeview.model.MultiViewType
import com.tech.multitypeview.model.MediaKind

// SRP: owns only expand/collapse state. Adapter holds no expand logic.
internal class ExpandController {

    private var fullList: List<MultiTypeItem> = emptyList()

    fun setList(list: List<MultiTypeItem>) {
        fullList = list
    }

    fun toggle(item: MultiTypeItem) {
        val isExpanding = !(item.isExpanded ?: false)

        // Snapshot current section states before mutating the list.
        var sectionAExpanded = false
        var sectionBExpanded = false
        fullList.forEach {
            if (it.id != item.id) return@forEach
            when (it.type) {
                MultiViewType.SECTION_A -> sectionAExpanded = it.isExpanded ?: false
                MultiViewType.SECTION_B -> sectionBExpanded = it.isExpanded ?: false
            }
        }

        fullList = fullList.map { current ->
            if (current.id != item.id) return@map current
            when (current.type) {
                item.type -> current.copy(isExpanded = isExpanding)
                MultiViewType.SECTION_A,
                MultiViewType.SECTION_B,
                MultiViewType.HEADER ->
                    if (item.type == MultiViewType.LABEL) current.copy(isVisible = isExpanding)
                    else current
                MultiViewType.GRID -> current.copy(
                    isVisible = when (item.type) {
                        MultiViewType.LABEL ->
                            isExpanding && if (current.mediaKind == MediaKind.DOCUMENT) sectionBExpanded else sectionAExpanded
                        else -> isExpanding
                    }
                )
                else -> current
            }
        }
    }

    fun visibleItems(): List<MultiTypeItem> = fullList.filter { it.isVisible != false }

    fun removeItems(locations: Set<String>) {
        fullList = fullList.filterNot { it.itemUrl != null && it.itemUrl in locations }
    }

    fun clear() {
        fullList = emptyList()
    }
}
