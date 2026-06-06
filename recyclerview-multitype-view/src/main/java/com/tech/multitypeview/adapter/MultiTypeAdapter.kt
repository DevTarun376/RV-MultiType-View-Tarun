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
import com.tech.multitypeview.model.MultiTypeItem
import com.tech.multitypeview.model.MultiViewType
import com.tech.multitypeview.model.MediaKind
import com.tech.multitypeview.ui.MultiTypeTheme
import com.tech.multitypeview.viewholder.GridViewHolder
import com.tech.multitypeview.viewholder.HeaderViewHolder
import com.tech.multitypeview.viewholder.SectionHeaderViewHolder
import com.tech.multitypeview.viewholder.TicketNumberViewHolder

class MultiTypeAdapter(
    var listener: MultiTypeAdapterCallback? = null,
    val theme: MultiTypeTheme = MultiTypeTheme(),
) : ListAdapter<MultiTypeItem, RecyclerView.ViewHolder>(MultiTypeDiffCallback) {

    // ── Companion — backward-compatible view-type constants ───────────────────

    companion object {
        const val VIEW_TYPE_TICKET_NUMBER_LINEAR = MultiViewType.TICKET_NUMBER
        const val VIEW_TYPE_HEADER_LINEAR = MultiViewType.HEADER
        const val VIEW_TYPE_TECH_HEADER_LINEAR = MultiViewType.TECH_HEADER
        const val VIEW_TYPE_ADMIN_HEADER_LINEAR = MultiViewType.ADMIN_HEADER
        const val VIEW_TYPE_GRID = MultiViewType.GRID
        const val DEFAULT_PAGE_SIZE = Paginator.DEFAULT_PAGE_SIZE
    }

    // ── State — delegated to focused controllers (SRP) ────────────────────────

    private val expandController = ExpandController()
    private var paginator = Paginator()
    private var isLoadingPage = false

    var enableDelete = false
        private set

    // ── Public API ────────────────────────────────────────────────────────────

    fun setDeleteMode(enabled: Boolean) {
        if (enableDelete == enabled) return
        enableDelete = enabled
        notifyItemRangeChanged(0, itemCount)
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
        getItem(position).type ?: MultiViewType.TICKET_NUMBER

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            MultiViewType.TICKET_NUMBER -> TicketNumberViewHolder(
                binding = MtvItemTicketNumberBinding.inflate(inflater, parent, false),
                theme = theme,
                itemAt = ::safeGetItem,
                onExpandToggle = ::handleExpandToggle
            )
            MultiViewType.HEADER -> HeaderViewHolder(
                binding = MtvItemHeaderBinding.inflate(inflater, parent, false),
                theme = theme,
            )
            MultiViewType.TECH_HEADER -> SectionHeaderViewHolder(
                binding = MtvItemSectionHeaderBinding.inflate(inflater, parent, false),
                label = parent.context.getString(R.string.mtv_tech_items),
                theme = theme,
                itemAt = ::safeGetItem,
                onExpandToggle = ::handleExpandToggle
            )
            MultiViewType.ADMIN_HEADER -> SectionHeaderViewHolder(
                binding = MtvItemSectionHeaderBinding.inflate(inflater, parent, false),
                label = parent.context.getString(R.string.mtv_admin_items),
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
            is TicketNumberViewHolder -> holder.bind(item)
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
            is TicketNumberViewHolder -> holder.update(item, bundle)
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
        // Preserve how far the user has already scrolled; don't reset to page 1.
        val alreadyLoaded = currentList.size
        paginator.setSource(expandController.visibleItems(), loadAtLeast = alreadyLoaded)
        submitList(paginator.currentPage())
    }

    private fun handleGridItemClick(item: MultiTypeItem) {
        if (item.mediaKind == MediaKind.ADMIN_ITEM) {
            val filtered = currentList.filter {
                it.mediaKind == MediaKind.ADMIN_ITEM &&
                        it.ticketId == item.ticketId &&
                        it.type == MultiViewType.GRID
            }
            val idx = filtered.indexOfFirst { it.picLocation == item.picLocation }
            if (idx != -1) listener?.onAdminItemClick(idx, filtered)
        } else {
            val filtered = currentList.filter {
                (it.mediaKind == MediaKind.TECH_IMAGE || it.mediaKind == MediaKind.TECH_VIDEO) &&
                        it.ticketId == item.ticketId &&
                        it.type == MultiViewType.GRID
            }
            val idx = filtered.indexOfFirst { it.picLocation == item.picLocation }
            if (idx != -1) listener?.onItemClick(idx, filtered)
        }
    }

    private fun toggleSelection(pos: Int, item: MultiTypeItem) {
        val updated = currentList.toMutableList()
        if (pos < updated.size) {
            updated[pos] = item.copy(isSelected = !item.isSelected)
            submitList(updated)
        }
    }
}
