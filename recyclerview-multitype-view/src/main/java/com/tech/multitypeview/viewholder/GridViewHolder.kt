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
import com.tech.multitypeview.R
import com.tech.multitypeview.adapter.PayloadKeys
import com.tech.multitypeview.databinding.MtvItemGridBinding
import com.tech.multitypeview.model.MultiTypeItem
import com.tech.multitypeview.model.InitializationState
import com.tech.multitypeview.model.MediaKind
import com.tech.multitypeview.util.MediaTypeHelper

internal class GridViewHolder(
    val binding: MtvItemGridBinding,
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
        when (item.mediaKind) {
            MediaKind.TECH_IMAGE, MediaKind.TECH_VIDEO -> bindTechItem(item)
            MediaKind.ADMIN_ITEM -> bindAdminItem(item)
            MediaKind.NONE -> hideContent()
        }
    }

    fun update(item: MultiTypeItem, payload: Bundle) {
        if (payload.containsKey(PayloadKeys.KEY_IS_SELECTED_CHANGED) && isDeleteEnabled()) {
            binding.ivSelectedIcon.setImageResource(
                if (item.isSelected) R.drawable.mtv_ic_checkbox_selected
                else R.drawable.mtv_ic_checkbox_unselected
            )
        }
    }

    fun clearImage() {
        Glide.with(binding.root.context.applicationContext).clear(binding.ivIcon)
    }

    private fun bindTechItem(item: MultiTypeItem) {
        val deleteOn = isDeleteEnabled()
        binding.ivIcon.isVisible = true
        binding.ivPlay.isVisible = item.mediaKind == MediaKind.TECH_VIDEO
        binding.ivSelectedIcon.isVisible = deleteOn
        binding.ivInitializationLoader.isVisible = item.initializationState == InitializationState.PENDING
        binding.proofPhoto.isVisible = item.isAIPhotoProof
        if (deleteOn) {
            binding.ivSelectedIcon.setImageResource(
                if (item.isSelected) R.drawable.mtv_ic_checkbox_selected
                else R.drawable.mtv_ic_checkbox_unselected
            )
        }
        loadImage(item.picLocation)
    }

    private fun bindAdminItem(item: MultiTypeItem) {
        binding.ivIcon.isVisible = true
        binding.ivPlay.isVisible = false
        binding.ivSelectedIcon.isVisible = false
        binding.ivInitializationLoader.isVisible = false
        binding.tvInitializedText.isVisible = false
        binding.proofPhoto.isVisible = item.isAIPhotoProof
        loadAdminItem(item.picLocation)
    }

    private fun hideContent() {
        binding.ivIcon.isVisible = false
        binding.loadingTv.isVisible = false
        binding.ivPlay.isVisible = false
        binding.ivSelectedIcon.isVisible = false
        binding.proofPhoto.isVisible = false
    }

    private fun loadImage(url: String?) {
        binding.loadingTv.isVisible = true
        Glide.with(binding.root.context)
            .load(url)
            .placeholder(R.drawable.mtv_placeholder)
            .error(R.drawable.mtv_placeholder)
            .listener(hideLoadingOnComplete())
            .into(binding.ivIcon)
    }

    private fun loadAdminItem(url: String?) {
        val staticDrawable = when {
            url == null -> R.drawable.mtv_placeholder
            MediaTypeHelper.isPdfExtension(url) -> R.drawable.mtv_ic_pdf
            MediaTypeHelper.isEmlExtension(url) -> R.drawable.mtv_ic_email
            else -> null
        }
        if (staticDrawable != null) {
            binding.ivIcon.setImageResource(staticDrawable)
            binding.loadingTv.isVisible = false
        } else {
            if (MediaTypeHelper.isVideoByExtension(url ?: "")) binding.ivPlay.isVisible = true
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
