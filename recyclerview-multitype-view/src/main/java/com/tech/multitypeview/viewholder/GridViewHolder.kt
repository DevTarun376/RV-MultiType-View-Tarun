package com.tech.multitypeview.viewholder

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.tech.multitypeview.adapter.PayloadKeys
import com.tech.multitypeview.databinding.MtvItemGridBinding
import com.tech.multitypeview.model.MultiTypeItem
import com.tech.multitypeview.model.MediaKind
import com.tech.multitypeview.ui.MultiTypeTheme
import com.tech.multitypeview.util.MediaTypeHelper

internal class GridViewHolder(
    val binding: MtvItemGridBinding,
    private val theme: MultiTypeTheme,
    private val itemAt: (Int) -> MultiTypeItem?,
    private val isDeleteEnabled: () -> Boolean,
    private val onGridClick: (MultiTypeItem) -> Unit,
    private val onLongPress: (MultiTypeItem) -> Unit,
    private val onSelectionToggle: (Int, MultiTypeItem) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.ivIcon.setOnLongClickListener {
            val pos = bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION && !isDeleteEnabled()) {
                itemAt(pos)?.let { onLongPress(it) }
            }
            true
        }

        binding.ivIcon.setOnClickListener {
            val pos = bindingAdapterPosition
            if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
            val item = itemAt(pos) ?: return@setOnClickListener
            if (isDeleteEnabled()) onSelectionToggle(pos, item) else onGridClick(item)
        }
    }

    fun bind(item: MultiTypeItem) {
        binding.root.setBackgroundColor(theme.gridItemBackground)
        binding.cardView.setCardBackgroundColor(theme.gridCardBackground)
        when (item.mediaKind) {
            MediaKind.IMAGE, MediaKind.VIDEO -> bindMediaItem(item)
            MediaKind.DOCUMENT -> bindDocumentItem(item)
            MediaKind.NONE -> hideContent()
        }
    }

    fun update(item: MultiTypeItem, payload: Bundle) {
        if (payload.containsKey(PayloadKeys.KEY_IS_SELECTED_CHANGED) && isDeleteEnabled()) {
            binding.ivSelectedIcon.setImageResource(
                if (item.isSelected) theme.iconCheckboxSelected else theme.iconCheckboxUnselected
            )
        }
    }

    fun clearImage() {
        Glide.with(binding.root.context.applicationContext).clear(binding.ivIcon)
    }

    private fun bindMediaItem(item: MultiTypeItem) {
        val deleteOn = isDeleteEnabled()
        binding.ivIcon.isVisible = true
        binding.ivPlay.isVisible = item.mediaKind == MediaKind.VIDEO
        binding.ivSelectedIcon.isVisible = deleteOn
        if (deleteOn) {
            binding.ivSelectedIcon.setImageResource(
                if (item.isSelected) theme.iconCheckboxSelected else theme.iconCheckboxUnselected
            )
        }
        if (item.mediaKind == MediaKind.VIDEO) {
            binding.ivPlay.setImageResource(theme.iconPlay)
        }
        loadImage(item.itemUrl)
    }

    private fun bindDocumentItem(item: MultiTypeItem) {
        binding.ivIcon.isVisible = true
        binding.ivPlay.isVisible = false
        binding.ivSelectedIcon.isVisible = false
        loadDocumentItem(item.itemUrl)
    }

    private fun hideContent() {
        binding.ivIcon.isVisible = false
        binding.loadingTv.isVisible = false
        binding.ivPlay.isVisible = false
        binding.ivSelectedIcon.isVisible = false
    }

    private fun loadImage(url: String?) {
        binding.loadingTv.isVisible = true
        Glide.with(binding.root.context)
            .load(url)
            .placeholder(theme.iconPlaceholder)
            .error(theme.iconPlaceholder)
            .listener(hideLoadingOnComplete())
            .into(binding.ivIcon)
    }

    private fun loadDocumentItem(url: String?) {
        val staticRes = when {
            url == null -> theme.iconPlaceholder
            MediaTypeHelper.isPdfExtension(url) -> theme.iconPdf
            MediaTypeHelper.isEmlExtension(url) -> theme.iconEmail
            else -> null
        }
        if (staticRes != null) {
            binding.ivIcon.setImageResource(staticRes)
            binding.loadingTv.isVisible = false
        } else {
            if (MediaTypeHelper.isVideoByExtension(url ?: "")) {
                binding.ivPlay.isVisible = true
                binding.ivPlay.setImageResource(theme.iconPlay)
            }
            loadImage(url)
        }
    }

    private fun hideLoadingOnComplete() = object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?, model: Any?, target: Target<Drawable?>, isFirstResource: Boolean
        ): Boolean {
            binding.loadingTv.isVisible = false
            return false
        }

        override fun onResourceReady(
            resource: Drawable, model: Any, target: Target<Drawable?>?, dataSource: DataSource, isFirstResource: Boolean
        ): Boolean {
            binding.loadingTv.isVisible = false
            return false
        }
    }
}
