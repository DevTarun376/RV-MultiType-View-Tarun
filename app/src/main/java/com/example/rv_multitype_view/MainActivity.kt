package com.example.rv_multitype_view

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.rv_multitype_view.R
import com.example.rv_multitype_view.databinding.MtvActivityDemoBinding
import com.example.rv_multitype_view.demo.DemoDataFactory
import com.tech.multitypeview.adapter.MultiTypeAdapter
import com.tech.multitypeview.adapter.MultiTypeAdapterCallback
import com.tech.multitypeview.model.MultiTypeItem
import com.tech.multitypeview.ui.MultiTypeConfig
import com.tech.multitypeview.ui.MultiTypeRecyclerManager
import com.tech.multitypeview.ui.MultiTypeTheme
import androidx.core.graphics.toColorInt

class MainActivity : AppCompatActivity(), MultiTypeAdapterCallback {

    private lateinit var binding: MtvActivityDemoBinding
    private lateinit var adapter: MultiTypeAdapter
    private lateinit var manager: MultiTypeRecyclerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MtvActivityDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "MultiTypeView Demo"

        adapter = MultiTypeAdapter(
            listener = this,
            theme = MultiTypeTheme(
                itemBackground        = "#E8F0FE".toColorInt(), // cascades to listBackground + all rows
                labelTextColor = "#1A73E8".toColorInt(),
                headerTextColor       = "#555555".toColorInt(),
                sectionLabelTextColor = "#222222".toColorInt(),
                gridCardBackground    = Color.WHITE,
            ),
        )
        manager = MultiTypeRecyclerManager(
            context = this,
            recyclerView = binding.recyclerView,
            adapter = adapter,
            config = MultiTypeConfig(
                pageSize = 50,
                phoneGridColumns = 5,
                tabletGridColumns = 4,
                scrollPrefetchThreshold = 5,
                showScrollbar = true,
            ),
        )
        manager.loadItems(DemoDataFactory.build())

        adapter.onSelectionChanged = { count -> updateDeleteSelectedButton(count) }

        binding.btnToggleDelete.setOnClickListener {
            toggleDeleteMode()
        }

        binding.btnDeleteSelected.setOnClickListener {
            confirmAndDelete()
        }
    }

    // ── MultiTypeAdapterCallback ──────────────────────────────────────────────

    override fun onItemClick(position: Int, list: List<MultiTypeItem>) {
        val item = list.getOrNull(position)
        toast("Opened item ${position + 1} of ${list.size} — ${item?.itemUrl?.substringAfterLast("/")}")
    }

    override fun onSecondaryItemClick(position: Int, list: List<MultiTypeItem>) {
        val item = list.getOrNull(position)
        toast("Document: ${item?.name ?: item?.itemUrl}")
    }

    override fun onLongPressDelete(item: MultiTypeItem?) {
        if (!adapter.enableDelete) {
            adapter.setDeleteMode(true)
            binding.btnToggleDelete.text = getString(R.string.mtv_demo_exit_delete)
            updateDeleteSelectedButton(0)
            toast("Delete mode — tap items to select, then tap Delete Selected")
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun toggleDeleteMode() {
        val entering = !adapter.enableDelete
        adapter.setDeleteMode(entering)
        binding.btnToggleDelete.text = getString(
            if (entering) R.string.mtv_demo_exit_delete else R.string.mtv_demo_enter_delete
        )
        updateDeleteSelectedButton(if (entering) 0 else -1)
    }

    private fun updateDeleteSelectedButton(selectedCount: Int) {
        val show = selectedCount > 0
        binding.btnDeleteSelected.visibility = if (show) View.VISIBLE else View.GONE
        if (show) {
            binding.btnDeleteSelected.text = "Delete Selected ($selectedCount)"
        }
    }

    private fun confirmAndDelete() {
        val count = adapter.getSelectedItemCount()
        if (count == 0) {
            toast("No items selected")
            return
        }
        AlertDialog.Builder(this)
            .setTitle("Delete Items")
            .setMessage("Are you sure you want to delete $count selected item${if (count > 1) "s" else ""}? This cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                adapter.deleteSelectedItems()
                binding.btnToggleDelete.text = getString(R.string.mtv_demo_enter_delete)
                updateDeleteSelectedButton(-1)
                toast("$count item${if (count > 1) "s" else ""} deleted")
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
