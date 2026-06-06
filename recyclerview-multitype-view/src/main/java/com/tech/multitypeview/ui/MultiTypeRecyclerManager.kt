package com.tech.multitypeview.ui

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tech.multitypeview.R
import com.tech.multitypeview.adapter.MultiTypeAdapter
import com.tech.multitypeview.model.MultiTypeItem
import com.tech.multitypeview.model.MultiViewType

/**
 * Wires a [MultiTypeAdapter] to a [RecyclerView]:
 *  - GridLayoutManager with correct span sizes per view type
 *  - Grid item margin decoration
 *  - Scroll-based pagination delegated to the adapter
 *
 * Usage:
 *   val manager = MultiTypeRecyclerManager(context, recyclerView, adapter)
 *   manager.loadItems(fullList)
 *
 *   // Custom config:
 *   val manager = MultiTypeRecyclerManager(
 *       context, recyclerView, adapter,
 *       config = MultiTypeConfig(pageSize = 30, phoneGridColumns = 2, tabletGridColumns = 3)
 *   )
 */
class MultiTypeRecyclerManager(
    private val context: Context,
    private val recyclerView: RecyclerView,
    private val adapter: MultiTypeAdapter,
    val config: MultiTypeConfig = MultiTypeConfig(),
) {

    init {
        setupLayoutManager()
        setupScrollListener()
        recyclerView.itemAnimator = null
        setupScrollbar()
    }

    private fun setupScrollbar() {
        recyclerView.isVerticalScrollBarEnabled = config.showScrollbar
        recyclerView.isScrollbarFadingEnabled = !config.showScrollbar
        recyclerView.scrollBarStyle = View.SCROLLBARS_OUTSIDE_OVERLAY
        if (config.showScrollbar) {
            recyclerView.verticalScrollbarThumbDrawable =
                ContextCompat.getDrawable(context, R.drawable.mtv_scrollbar_thumb)
        }
    }

    fun loadItems(items: List<MultiTypeItem>) {
        adapter.loadItems(items, config.pageSize)
        if (recyclerView.adapter == null) {
            recyclerView.adapter = adapter
        }
    }

    private fun setupLayoutManager() {
        val spanCount = if (isTablet(context)) config.tabletGridColumns else config.phoneGridColumns
        val glm = GridLayoutManager(context, spanCount)
        glm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (position >= adapter.itemCount) return 1
                return if (adapter.getItemViewType(position) == MultiViewType.GRID) 1 else spanCount
            }
        }
        recyclerView.layoutManager = glm
        val spacing = context.resources.getDimensionPixelSize(R.dimen.mtv_grid_item_spacing)
        recyclerView.addItemDecoration(GridItemMarginDecoration(spacing, spanCount))
    }

    private fun setupScrollListener() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy <= 0) return
                val glm = recyclerView.layoutManager as? GridLayoutManager ?: return
                val triggerAt = (adapter.itemCount - 1) - config.scrollPrefetchThreshold
                if (glm.findLastVisibleItemPosition() >= triggerAt) {
                    recyclerView.post { adapter.loadNextPage(config.pageSize) }
                }
            }
        })
    }

    private inner class GridItemMarginDecoration(
        private val spacing: Int,
        private val spanCount: Int
    ) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val position = parent.getChildAdapterPosition(view)
            if (position == RecyclerView.NO_POSITION) return
            if (parent.adapter?.getItemViewType(position) != MultiViewType.GRID) return
            val item = adapter.currentList.getOrNull(position) ?: return
            val column = (item.imageIndex ?: 0) % spanCount
            outRect.left = if (column == 0) spacing else 0
            outRect.right = if (column == spanCount - 1) spacing else 0
        }
    }

    private fun isTablet(context: Context): Boolean =
        context.resources.configuration.smallestScreenWidthDp >= 600
}
