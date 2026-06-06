package com.tech.multitypeview.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.tech.multitypeview.databinding.MtvItemHeaderBinding
import com.tech.multitypeview.model.MultiTypeItem

internal class HeaderViewHolder(
    val binding: MtvItemHeaderBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: MultiTypeItem) {
        binding.headerValue = item.header
    }
}
