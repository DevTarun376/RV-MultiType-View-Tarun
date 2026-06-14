package com.tech.multitypeview.viewholder

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.tech.multitypeview.adapter.PayloadKeys
import com.tech.multitypeview.databinding.MtvItemSectionHeaderBinding
import com.tech.multitypeview.model.MultiTypeItem
import com.tech.multitypeview.ui.MultiTypeTheme

internal class SectionHeaderViewHolder(
    val binding: MtvItemSectionHeaderBinding,
    private val theme: MultiTypeTheme,
    private val itemAt: (Int) -> MultiTypeItem?,
    private val onExpandToggle: (MultiTypeItem) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.ivExpand.setOnClickListener {
            val pos = bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) itemAt(pos)?.let { onExpandToggle(it) }
        }
    }

    fun bind(item: MultiTypeItem) {
        binding.model = item
        binding.root.setBackgroundColor(theme.sectionHeaderBackground)
        binding.tvText.setTextColor(theme.sectionLabelTextColor)
        setArrow(item)
    }

    fun update(item: MultiTypeItem, payload: Bundle) {
        if (payload.containsKey(PayloadKeys.KEY_IS_EXPANDED_CHANGED)) setArrow(item)
    }

    private fun setArrow(item: MultiTypeItem) {
        binding.ivExpand.setImageResource(
            if (item.isExpanded == true) theme.iconArrowExpanded else theme.iconArrowCollapsed
        )
    }
}
