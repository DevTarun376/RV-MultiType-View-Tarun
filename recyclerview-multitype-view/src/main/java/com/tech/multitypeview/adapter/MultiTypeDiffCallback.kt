package com.tech.multitypeview.adapter

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import com.tech.multitypeview.model.MultiTypeItem
import com.tech.multitypeview.model.MultiViewType

internal object PayloadKeys {
    const val KEY_IS_EXPANDED_CHANGED = "KEY_IS_EXPANDED_CHANGED"
    const val KEY_IS_VISIBLE_CHANGED = "KEY_IS_VISIBLE_CHANGED"
    const val KEY_IS_SELECTED_CHANGED = "KEY_IS_SELECTED_CHANGED"
}

internal object MultiTypeDiffCallback : DiffUtil.ItemCallback<MultiTypeItem>() {

    override fun areItemsTheSame(old: MultiTypeItem, new: MultiTypeItem): Boolean =
        old.id == new.id &&
                old.type == new.type &&
                (old.type != MultiViewType.GRID || old.itemUrl == new.itemUrl) &&
                (old.type != MultiViewType.HEADER || old.header == new.header)

    override fun areContentsTheSame(old: MultiTypeItem, new: MultiTypeItem): Boolean =
        old == new

    override fun getChangePayload(old: MultiTypeItem, new: MultiTypeItem): Any? {
        val bundle = Bundle()
        if (old.isExpanded != new.isExpanded) bundle.putBoolean(PayloadKeys.KEY_IS_EXPANDED_CHANGED, true)
        if (old.isVisible != new.isVisible) bundle.putBoolean(PayloadKeys.KEY_IS_VISIBLE_CHANGED, true)
        if (old.isSelected != new.isSelected) bundle.putBoolean(PayloadKeys.KEY_IS_SELECTED_CHANGED, true)
        return if (bundle.isEmpty) null else bundle
    }
}
