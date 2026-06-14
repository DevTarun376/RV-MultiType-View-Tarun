package com.example.rv_multitype_view.demo

import com.tech.multitypeview.model.MediaKind
import com.tech.multitypeview.model.MultiTypeItem
import com.tech.multitypeview.model.MultiViewType

object DemoDataFactory {

    private const val PICSUM = "https://picsum.photos/seed"
    private val DATES = listOf(
        "January 10, 2025", "February 14, 2025", "March 3, 2025",
        "April 22, 2025", "May 17, 2025", "June 6, 2025",
        "July 4, 2025", "August 19, 2025", "September 30, 2025",
        "October 11, 2025", "November 28, 2025", "December 5, 2025"
    )

    fun build(): List<MultiTypeItem> = buildList {
        repeat(1000) { i ->
            val num = i + 1
            val id = "I-%04d".format(num)
            addGroup(
                list = this,
                id = id,
                label = id,
                date = DATES[i % DATES.size],
                isExpanded = i % 3 != 1,
                sectionAExpanded = i % 4 != 2,
                sectionBExpanded = i % 5 != 3,
                images = List(3) { j -> "$PICSUM/$id-img$j/300/300" },
                videos = if (i % 3 == 0) listOf("$PICSUM/$id-vid/300/300") else emptyList(),
                documents = List(3) { j ->
                    when (j % 3) {
                        0 -> "file_$num.pdf"
                        1 -> "email_$num.eml"
                        else -> "$PICSUM/$id-doc$j/300/300"
                    }
                }
            )
        }
    }

    private fun addGroup(
        list: MutableList<MultiTypeItem>,
        id: String,
        label: String,
        date: String,
        isExpanded: Boolean,
        sectionAExpanded: Boolean,
        sectionBExpanded: Boolean,
        images: List<String>,
        videos: List<String>,
        documents: List<String>
    ) = with(list) {
        add(MultiTypeItem(
            type = MultiViewType.LABEL,
            id = id,
            label = label,
            isExpanded = isExpanded,
            isVisible = true
        ))

        add(MultiTypeItem(
            type = MultiViewType.HEADER,
            id = id,
            header = date,
            isVisible = isExpanded
        ))

        add(MultiTypeItem(
            type = MultiViewType.SECTION_A,
            id = id,
            label = "Section A",
            isExpanded = sectionAExpanded,
            isVisible = isExpanded
        ))

        var gridIndex = 0
        images.forEach { url ->
            add(MultiTypeItem(
                type = MultiViewType.GRID,
                id = id,
                mediaKind = MediaKind.IMAGE,
                itemUrl = url,
                imageIndex = gridIndex++,
                isVisible = isExpanded && sectionAExpanded
            ))
        }
        videos.forEach { url ->
            add(MultiTypeItem(
                type = MultiViewType.GRID,
                id = id,
                mediaKind = MediaKind.VIDEO,
                itemUrl = url,
                imageIndex = gridIndex++,
                isVisible = isExpanded && sectionAExpanded
            ))
        }

        add(MultiTypeItem(
            type = MultiViewType.SECTION_B,
            id = id,
            label = "Section B",
            isExpanded = sectionBExpanded,
            isVisible = isExpanded
        ))

        documents.forEachIndexed { i, path ->
            add(MultiTypeItem(
                type = MultiViewType.GRID,
                id = id,
                mediaKind = MediaKind.DOCUMENT,
                itemUrl = path,
                name = path.substringAfterLast("/"),
                imageIndex = i,
                isVisible = isExpanded && sectionBExpanded
            ))
        }
    }
}
