package com.example.rv_multitype_view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rv_multitype_view.R
import com.example.rv_multitype_view.databinding.MtvActivityDemoBinding
import com.example.rv_multitype_view.demo.DemoDataFactory
import com.tech.multitypeview.adapter.MultiTypeAdapter
import com.tech.multitypeview.adapter.MultiTypeAdapterCallback
import com.tech.multitypeview.model.MultiTypeItem
import com.tech.multitypeview.ui.MultiTypeRecyclerManager

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

        adapter = MultiTypeAdapter(listener = this)
        manager = MultiTypeRecyclerManager(this, binding.recyclerView, adapter)
        manager.loadItems(DemoDataFactory.build())

        binding.btnToggleDelete.setOnClickListener {
            toggleDeleteMode()
        }
    }

    // ── MultiTypeAdapterCallback ──────────────────────────────────────────────

    override fun onItemClick(position: Int, list: List<MultiTypeItem>) {
        val item = list.getOrNull(position)
        toast("Opened item ${position + 1} of ${list.size} — ${item?.picLocation?.substringAfterLast("/")}")
    }

    override fun onAdminItemClick(position: Int, list: List<MultiTypeItem>) {
        val item = list.getOrNull(position)
        toast("Admin item: ${item?.name ?: item?.picLocation}")
    }

    override fun onLongPressDelete(item: MultiTypeItem?) {
        adapter.setDeleteMode(true)
        binding.btnToggleDelete.text = getString(R.string.mtv_demo_exit_delete)
        toast("Delete mode — tap items to select, then confirm")
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun toggleDeleteMode() {
        val entering = !adapter.enableDelete
        adapter.setDeleteMode(entering)
        binding.btnToggleDelete.text = getString(
            if (entering) R.string.mtv_demo_exit_delete else R.string.mtv_demo_enter_delete
        )
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
