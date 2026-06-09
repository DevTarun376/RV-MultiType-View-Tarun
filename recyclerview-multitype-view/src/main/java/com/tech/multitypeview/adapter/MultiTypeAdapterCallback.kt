package com.tech.multitypeview.adapter

import com.tech.multitypeview.model.MultiTypeItem

fun interface OnItemClick {
    fun onItemClick(position: Int, list: List<MultiTypeItem>)
}

fun interface OnSecondaryItemClick {
    fun onSecondaryItemClick(position: Int, list: List<MultiTypeItem>)
}

fun interface OnItemLongPress {
    fun onLongPressDelete(item: MultiTypeItem?)
}

interface MultiTypeAdapterCallback : OnItemClick, OnSecondaryItemClick, OnItemLongPress
