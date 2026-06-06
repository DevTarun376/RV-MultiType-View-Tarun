package com.tech.multitypeview.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.tech.multitypeview.databinding.MtvItemHeaderBinding
import com.tech.multitypeview.model.MultiTypeItem
import com.tech.multitypeview.ui.MultiTypeTheme

internal class HeaderViewHolder(
    val binding: MtvItemHeaderBinding,
    private val theme: MultiTypeTheme,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: MultiTypeItem) {
        binding.headerValue = item.header
        binding.root.setBackgroundColor(theme.headerRowBackground)
        binding.tvPhotosTitle.setTextColor(theme.headerTextColor)
    }
}
