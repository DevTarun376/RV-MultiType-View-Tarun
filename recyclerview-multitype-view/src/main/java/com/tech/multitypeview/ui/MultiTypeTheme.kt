package com.tech.multitypeview.ui

import android.graphics.Color
import com.tech.multitypeview.R

data class MultiTypeTheme(

    // ── Text colors ───────────────────────────────────────────────────────────

    val labelTextColor: Int = Color.BLACK,
    val headerTextColor: Int = Color.BLACK,
    val sectionLabelTextColor: Int = Color.BLACK,

    // ── Backgrounds (all cascade from itemBackground) ─────────────────────────

    val itemBackground: Int = Color.TRANSPARENT,
    val listBackground: Int = itemBackground,
    val labelRowBackground: Int = itemBackground,
    val headerRowBackground: Int = itemBackground,
    val sectionHeaderBackground: Int = itemBackground,
    val gridItemBackground: Int = itemBackground,
    val gridCardBackground: Int = Color.WHITE,

    // ── Icons ─────────────────────────────────────────────────────────────────

    val iconArrowExpanded: Int = R.drawable.mtv_ic_arrow_down,
    val iconArrowCollapsed: Int = R.drawable.mtv_ic_arrow_up,
    val iconPlay: Int = R.drawable.mtv_ic_play_video,
    val iconCheckboxSelected: Int = R.drawable.mtv_ic_checkbox_selected,
    val iconCheckboxUnselected: Int = R.drawable.mtv_ic_checkbox_unselected,
    val iconPlaceholder: Int = R.drawable.mtv_placeholder,
    val iconPdf: Int = R.drawable.mtv_ic_pdf,
    val iconEmail: Int = R.drawable.mtv_ic_email,
)
