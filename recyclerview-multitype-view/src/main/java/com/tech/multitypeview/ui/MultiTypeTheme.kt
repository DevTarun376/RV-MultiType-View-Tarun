package com.tech.multitypeview.ui

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.tech.multitypeview.R

/**
 * Visual theme for all item types rendered by [com.tech.multitypeview.adapter.MultiTypeAdapter].
 * Every field has a sensible default matching the original hardcoded appearance.
 *
 * **Cascading backgrounds:** [itemBackground] is the base for every surface.
 * [listBackground] and all per-type row backgrounds default to [itemBackground],
 * so setting only [itemBackground] colours the whole list uniformly.
 * Override individual fields to change just that surface.
 *
 * ```kotlin
 * // Uniform background everywhere:
 * MultiTypeTheme(itemBackground = "#E8F0FE".toColorInt())
 *
 * // Override one row type:
 * MultiTypeTheme(
 *     itemBackground      = "#E8F0FE".toColorInt(),
 *     ticketRowBackground = "#1A73E8".toColorInt(),
 * )
 * ```
 */
data class MultiTypeTheme(

    // ── Text colors ───────────────────────────────────────────────────────────

    /** Color of the "Ticket: T-0001" label in the ticket-number row. */
    @ColorInt val ticketNumberTextColor: Int = Color.BLACK,

    /** Color of the date/header text in plain header rows. */
    @ColorInt val headerTextColor: Int = Color.BLACK,

    /** Color of the "Tech Items" / "Admin Items" section labels. */
    @ColorInt val sectionLabelTextColor: Int = Color.BLACK,

    // ── Backgrounds (all cascade from itemBackground) ─────────────────────────

    /**
     * Base background for every surface in the list.
     * [listBackground] and all per-type row backgrounds default to this value.
     */
    @ColorInt val itemBackground: Int = Color.TRANSPARENT,

    /** Background of the entire RecyclerView. Defaults to [itemBackground]. */
    @ColorInt val listBackground: Int = itemBackground,

    /** Ticket-number row root background. Defaults to [itemBackground]. */
    @ColorInt val ticketRowBackground: Int = itemBackground,

    /** Plain header (date) row root background. Defaults to [itemBackground]. */
    @ColorInt val headerRowBackground: Int = itemBackground,

    /** Tech / Admin section header row root background. Defaults to [itemBackground]. */
    @ColorInt val sectionHeaderBackground: Int = itemBackground,

    /** Outer root background of each grid cell (behind the card). Defaults to [itemBackground]. */
    @ColorInt val gridItemBackground: Int = itemBackground,

    /** CardView background inside each grid cell. */
    @ColorInt val gridCardBackground: Int = Color.WHITE,

    // ── Expand / collapse arrows ──────────────────────────────────────────────

    /** Arrow icon shown when a section is expanded (pointing down). */
    @DrawableRes val iconArrowExpanded: Int = R.drawable.mtv_ic_arrow_down,

    /** Arrow icon shown when a section is collapsed (pointing up / right). */
    @DrawableRes val iconArrowCollapsed: Int = R.drawable.mtv_ic_arrow_up,

    // ── Grid cell icons ───────────────────────────────────────────────────────

    /** Overlay play icon for video grid cells. */
    @DrawableRes val iconPlay: Int = R.drawable.mtv_ic_play_video,

    /** Checkbox icon for a selected item in delete mode. */
    @DrawableRes val iconCheckboxSelected: Int = R.drawable.mtv_ic_checkbox_selected,

    /** Checkbox icon for an unselected item in delete mode. */
    @DrawableRes val iconCheckboxUnselected: Int = R.drawable.mtv_ic_checkbox_unselected,

    /** Fallback / placeholder shown while an image loads or on error. */
    @DrawableRes val iconPlaceholder: Int = R.drawable.mtv_placeholder,

    /** Icon shown for PDF admin attachments. */
    @DrawableRes val iconPdf: Int = R.drawable.mtv_ic_pdf,

    /** Icon shown for EML (email) admin attachments. */
    @DrawableRes val iconEmail: Int = R.drawable.mtv_ic_email,
)
