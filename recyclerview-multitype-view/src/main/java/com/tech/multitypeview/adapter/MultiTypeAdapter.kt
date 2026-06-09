package com.tech.multitypeview.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tech.multitypeview.R
import com.tech.multitypeview.controller.ExpandController
import com.tech.multitypeview.controller.Paginator
import com.tech.multitypeview.databinding.MtvItemGridBinding
import com.tech.multitypeview.databinding.MtvItemHeaderBinding
import com.tech.multitypeview.databinding.MtvItemSectionHeaderBinding
import com.tech.multitypeview.databinding.MtvItemTicketNumberBinding
import com.tech.multitypeview.model.MediaKind
import com.tech.multitypeview.model.MultiTypeItem
import com.tech.multitypeview.model.MultiViewType
import com.tech.multitypeview.ui.MultiTypeTheme
import com.tech.multitypeview.viewholder.GridViewHolder
import com.tech.multitypeview.viewholder.HeaderViewHolder
import com.tech.multitypeview.viewholder.LabelViewHolder
import com.tech.multitypeview.viewholder.SectionHeaderViewHolder

class MultiTypeAdapter(
    var listener: MultiTypeAdapterCallback? = null,
    val theme: MultiTypeTheme = MultiTypeTheme(),
) : ListAdapter<MultiTypeItem, RecyclerView.ViewHolder>(MultiTypeDiffCallback) {

    companion object {
        const val VIEW_TYPE_LABEL = MultiViewType.LABEL
        const val VIEW_TYPE_HEADER = MultiViewType.HEADER
        const val VIEW_TYPE_SECTION_A = MultiViewType.SECTION_A
        const val VIEW_TYPE_SECTION_B = MultiViewType.SECTION_B
        const val VIEW_TYPE_GRID = MultiViewType.GRID
        const val DEFAULT_PAGE_SIZE = Paginator.DEFAULT_PAGE_SIZE
    }

    private val expandController = ExpandController()
    private var paginator = Paginator()
    private var isLoadingPage = false

    var enableDelete = false
        private set

    var onSelectionChanged: ((selectedCount: Int) -> Unit)? = null

    // ── Public API ────────────────────────────────────────────────────────────

    fun setDeleteMode(enabled: Boolean) {
        if (enableDelete == enabled) return
        enableDelete = enabled
        if (!enabled) {
            val cleared = currentList.map { if (it.isSelected) it.copy(isSelected = false) else it }
            submitList(cleared) { onSelectionChanged?.invoke(0) }
        } else {
            notifyItemRangeChanged(0, itemCount)
        }
    }

    fun getSelectedItemCount(): Int = currentList.count { it.isSelected }

    fun deleteSelectedItems() {
        val locations = currentList.filter { it.isSelected }.mapNotNull { it.itemUrl }.toSet()
        if (locations.isEmpty()) return
        expandController.removeItems(locations)
        val remainingCount = (currentList.size - locations.size).coerceAtLeast(0)
        paginator.setSource(expandController.visibleItems(), loadAtLeast = remainingCount)
        submitList(paginator.currentPage())
        onSelectionChanged?.invoke(0)
        if (enableDelete) {
            enableDelete = false
            notifyItemRangeChanged(0, itemCount)
        }
    }

    fun loadItems(items: List<MultiTypeItem>, pageSize: Int = DEFAULT_PAGE_SIZE) {
        paginator = Paginator(pageSize)
        expandController.setList(items)
        paginator.setSource(expandController.visibleItems())
        submitList(paginator.currentPage())
    }

    fun loadNextPage(pageSize: Int = DEFAULT_PAGE_SIZE): Boolean {
        if (isLoadingPage) return paginator.hasMore()
        val next = paginator.loadNext() ?: return false
        isLoadingPage = true
        submitList(next) { isLoadingPage = false }
        return paginator.hasMore()
    }

    fun updateListItems(list: List<MultiTypeItem>) {
        expandController.setList(list)
        paginator.setSource(expandController.visibleItems(), loadAtLeast = currentList.size)
        submitList(paginator.currentPage())
    }

    fun clear() {
        expandController.clear()
        paginator.clear()
        submitList(emptyList())
    }

    // ── ListAdapter overrides ─────────────────────────────────────────────────

    override fun getItemViewType(position: Int): Int =
        getItem(position).type ?: MultiViewType.LABEL

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            MultiViewType.LABEL -> LabelViewHolder(
                binding = MtvItemTicketNumberBinding.inflate(inflater, parent, false),
                theme = theme,
                itemAt = ::safeGetItem,
                onExpandToggle = ::handleExpandToggle
            )
            MultiViewType.HEADER -> HeaderViewHolder(
                binding = MtvItemHeaderBinding.inflate(inflater, parent, false),
                theme = theme,
            )
            MultiViewType.SECTION_A -> SectionHeaderViewHolder(
                binding = MtvItemSectionHeaderBinding.inflate(inflater, parent, false),
                label = parent.context.getString(R.string.mtv_section_a),
                theme = theme,
                itemAt = ::safeGetItem,
                onExpandToggle = ::handleExpandToggle
            )
            MultiViewType.SECTION_B -> SectionHeaderViewHolder(
                binding = MtvItemSectionHeaderBinding.inflate(inflater, parent, false),
                label = parent.context.getString(R.string.mtv_section_b),
                theme = theme,
                itemAt = ::safeGetItem,
                onExpandToggle = ::handleExpandToggle
            )
            MultiViewType.GRID -> GridViewHolder(
                binding = MtvItemGridBinding.inflate(inflater, parent, false),
                theme = theme,
                itemAt = ::safeGetItem,
                isDeleteEnabled = { enableDelete },
                onGridClick = ::handleGridItemClick,
                onLongPress = { listener?.onLongPressDelete(it) },
                onSelectionToggle = ::toggleSelection
            )
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is LabelViewHolder -> holder.bind(item)
            is HeaderViewHolder -> holder.bind(item)
            is SectionHeaderViewHolder -> holder.bind(item)
            is GridViewHolder -> holder.bind(item)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
            return
        }
        val item = getItem(position)
        val bundle = payloads[0] as Bundle
        when (holder) {
            is LabelViewHolder -> holder.update(item, bundle)
            is SectionHeaderViewHolder -> holder.update(item, bundle)
            is GridViewHolder -> holder.update(item, bundle)
            else -> super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is GridViewHolder) holder.clearImage()
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private fun safeGetItem(pos: Int): MultiTypeItem? =
        if (pos != RecyclerView.NO_POSITION && pos < itemCount) getItem(pos) else null

    private fun handleExpandToggle(item: MultiTypeItem) {
        expandController.toggle(item)
        val alreadyLoaded = currentList.size
        paginator.setSource(expandController.visibleItems(), loadAtLeast = alreadyLoaded)
        submitList(paginator.currentPage())
    }

    private fun handleGridItemClick(item: MultiTypeItem) {
        if (item.mediaKind == MediaKind.DOCUMENT) {
            val filtered = currentList.filter {
                it.mediaKind == MediaKind.DOCUMENT &&
                        it.id == item.id &&
                        it.type == MultiViewType.GRID
            }
            val idx = filtered.indexOfFirst { it.itemUrl == item.itemUrl }
            if (idx != -1) listener?.onSecondaryItemClick(idx, filtered)
        } else {
            val filtered = currentList.filter {
                (it.mediaKind == MediaKind.IMAGE || it.mediaKind == MediaKind.VIDEO) &&
                        it.id == item.id &&
                        it.type == MultiViewType.GRID
            }
            val idx = filtered.indexOfFirst { it.itemUrl == item.itemUrl }
            if (idx != -1) listener?.onItemClick(idx, filtered)
        }
    }

    private fun toggleSelection(pos: Int, item: MultiTypeItem) {
        val updated = currentList.toMutableList()
        if (pos < updated.size) {
            updated[pos] = item.copy(isSelected = !item.isSelected)
            submitList(updated) { onSelectionChanged?.invoke(updated.count { it.isSelected }) }
        }
    }
}
