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
        var techExpanded = false
        var adminExpanded = false
        fullList.forEach {
            if (it.ticketId != item.ticketId) return@forEach
            when (it.type) {
                MultiViewType.TECH_HEADER -> techExpanded = it.isExpanded ?: false
                MultiViewType.ADMIN_HEADER -> adminExpanded = it.isExpanded ?: false
            }
        }

        fullList = fullList.map { current ->
            if (current.ticketId != item.ticketId) return@map current
            when (current.type) {
                item.type -> current.copy(isExpanded = isExpanding)
                MultiViewType.TECH_HEADER,
                MultiViewType.ADMIN_HEADER,
                MultiViewType.HEADER ->
                    if (item.type == MultiViewType.TICKET_NUMBER) current.copy(isVisible = isExpanding)
                    else current
                MultiViewType.GRID -> current.copy(
                    isVisible = when (item.type) {
                        MultiViewType.TICKET_NUMBER ->
                            isExpanding && if (current.mediaKind == MediaKind.ADMIN_ITEM) adminExpanded else techExpanded
                        else -> isExpanding
                    }
                )
                else -> current
            }
        }
    }

    fun visibleItems(): List<MultiTypeItem> = fullList.filter { it.isVisible != false }

    fun clear() {
        fullList = emptyList()
    }
}
