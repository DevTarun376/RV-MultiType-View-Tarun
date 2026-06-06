package com.tech.multitypeview.adapter

import com.tech.multitypeview.model.MultiTypeItem

// ISP: each caller only depends on the events it actually handles.

fun interface OnItemClick {
    fun onItemClick(position: Int, list: List<MultiTypeItem>)
}

fun interface OnAdminItemClick {
    fun onAdminItemClick(position: Int, list: List<MultiTypeItem>)
}

fun interface OnItemLongPress {
    fun onLongPressDelete(item: MultiTypeItem?)
}

// Combined convenience interface — implement when all three are needed.
interface MultiTypeAdapterCallback : OnItemClick, OnAdminItemClick, OnItemLongPress
