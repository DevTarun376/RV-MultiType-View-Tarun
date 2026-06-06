package com.tech.multitypeview.viewholder

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.tech.multitypeview.R
import com.tech.multitypeview.adapter.PayloadKeys
import com.tech.multitypeview.databinding.MtvItemSectionHeaderBinding
import com.tech.multitypeview.model.MultiTypeItem

// Single ViewHolder for both TECH_HEADER and ADMIN_HEADER — identical structure, label differs.
internal class SectionHeaderViewHolder(
    val binding: MtvItemSectionHeaderBinding,
    private val label: String,
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
        binding.labelText = label
        setArrow(item)
    }

    fun update(item: MultiTypeItem, payload: Bundle) {
        if (payload.containsKey(PayloadKeys.KEY_IS_EXPANDED_CHANGED)) setArrow(item)
    }

    private fun setArrow(item: MultiTypeItem) {
        binding.ivExpand.setImageResource(
            if (item.isExpanded == true) R.drawable.mtv_ic_arrow_down else R.drawable.mtv_ic_arrow_up
        )
    }
}
