package com.example.rv_multitype_view.demo

import com.tech.multitypeview.model.InitializationState
import com.tech.multitypeview.model.MediaKind
import com.tech.multitypeview.model.MultiTypeItem
import com.tech.multitypeview.model.MultiViewType
import com.tech.multitypeview.model.TicketBindingType

object DemoDataFactory {

    private const val PICSUM = "https://picsum.photos/seed"
    private val BINDING_TYPES = TicketBindingType.entries.toTypedArray()
    private val DATES = listOf(
        "January 10, 2025", "February 14, 2025", "March 3, 2025",
        "April 22, 2025", "May 17, 2025", "June 6, 2025",
        "July 4, 2025", "August 19, 2025", "September 30, 2025",
        "October 11, 2025", "November 28, 2025", "December 5, 2025"
    )

    fun build(): List<MultiTypeItem> = buildList {
        repeat(1000) { i ->
            val ticketNum = i + 1
            val id = "T-%04d".format(ticketNum)
            addTicket(
                list = this,
                id = id,
                number = id,
                date = DATES[i % DATES.size],
                isExpanded = i % 3 != 1,
                techExpanded = i % 4 != 2,
                adminExpanded = i % 5 != 3,
                bindingType = BINDING_TYPES[i % BINDING_TYPES.size],
                techImages = List(3) { j -> "$PICSUM/$id-ti$j/300/300" },
                techVideos = if (i % 3 == 0) listOf("$PICSUM/$id-tv/300/300") else emptyList(),
                aiProofIndex = if (i % 4 == 0) 1 else -1,
                adminItems = List(3) { j ->
                    when (j % 3) {
                        0 -> "report_$ticketNum.pdf"
                        1 -> "notice_$ticketNum.eml"
                        else -> "$PICSUM/$id-ai$j/300/300"
                    }
                },
                pendingIndex = if (i % 5 == 0) 2 else -1
            )
        }
    }

    private fun addTicket(
        list: MutableList<MultiTypeItem>,
        id: String,
        number: String,
        date: String,
        isExpanded: Boolean,
        techExpanded: Boolean,
        adminExpanded: Boolean,
        bindingType: TicketBindingType,
        techImages: List<String>,
        techVideos: List<String>,
        aiProofIndex: Int,
        adminItems: List<String>,
        pendingIndex: Int
    ) = with(list) {
        add(MultiTypeItem(
            type = MultiViewType.TICKET_NUMBER,
            ticketId = id,
            ticketNumber = number,
            isExpanded = isExpanded,
            bindingType = bindingType,
            isVisible = true
        ))

        add(MultiTypeItem(
            type = MultiViewType.HEADER,
            ticketId = id,
            header = date,
            isVisible = isExpanded
        ))

        add(MultiTypeItem(
            type = MultiViewType.TECH_HEADER,
            ticketId = id,
            isExpanded = techExpanded,
            isVisible = isExpanded
        ))

        var gridIndex = 0
        techImages.forEachIndexed { i, url ->
            add(MultiTypeItem(
                type = MultiViewType.GRID,
                ticketId = id,
                mediaKind = MediaKind.TECH_IMAGE,
                picLocation = url,
                imageIndex = gridIndex++,
                isVisible = isExpanded && techExpanded,
                isAIPhotoProof = i == aiProofIndex,
                initializationState = if (i == pendingIndex) InitializationState.PENDING
                                      else InitializationState.STANDARD
            ))
        }
        techVideos.forEach { url ->
            add(MultiTypeItem(
                type = MultiViewType.GRID,
                ticketId = id,
                mediaKind = MediaKind.TECH_VIDEO,
                picLocation = url,
                imageIndex = gridIndex++,
                isVisible = isExpanded && techExpanded
            ))
        }

        add(MultiTypeItem(
            type = MultiViewType.ADMIN_HEADER,
            ticketId = id,
            isExpanded = adminExpanded,
            isVisible = isExpanded
        ))

        adminItems.forEachIndexed { i, path ->
            add(MultiTypeItem(
                type = MultiViewType.GRID,
                ticketId = id,
                mediaKind = MediaKind.ADMIN_ITEM,
                picLocation = path,
                name = path.substringAfterLast("/"),
                imageIndex = i,
                isVisible = isExpanded && adminExpanded
            ))
        }
    }
}
