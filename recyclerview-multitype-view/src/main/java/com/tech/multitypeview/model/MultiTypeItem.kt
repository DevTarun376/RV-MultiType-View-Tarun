package com.tech.multitypeview.model

// ── Supporting enums ──────────────────────────────────────────────────────────

/** Content kind of a grid item — replaces the old isImage + isVideo + admin triple. */
enum class MediaKind {
    NONE,        // non-grid rows (headers, labels)
    TECH_IMAGE,
    TECH_VIDEO,
    ADMIN_ITEM   // PDF / EML / image / video owned by admin
}

/** Linking role of a ticket-group header — replaces isBindingTicket + isBindingParent. */
enum class TicketBindingType { NONE, PARENT, CHILD }

/** Initialization-capture lifecycle — replaces type String + isAttached Boolean. */
enum class InitializationState {
    STANDARD,   // normal item
    PENDING,    // capture in progress — show loading overlay
    COMPLETE    // capture processed — overlay hidden
}

// ── Data class ────────────────────────────────────────────────────────────────

data class MultiTypeItem(
    var type: Int? = null,

    var ticketId: String? = null,
    var ticketNumber: String? = null,

    /** Expand / collapse state for any header row. Replaces ivExpand + ivTechExpand + ivAdminExpand. */
    var isExpanded: Boolean? = null,

    /** Ticket linking role. Replaces isBindingTicket + isBindingParent. */
    var bindingType: TicketBindingType = TicketBindingType.NONE,

    var header: String? = null,

    /** Grid content kind. Replaces isImage + isVideo + admin. */
    var mediaKind: MediaKind = MediaKind.NONE,

    var date: String? = null,
    var imageUrl: String? = null,
    var picLocation: String? = null,
    var name: String? = null,
    var source: String? = null,
    var attachId: String? = null,
    var imageIndex: Int? = null,
    var isVisible: Boolean? = true,
    var isSelected: Boolean = false,
    var isAIPhotoProof: Boolean = false,

    /** Initialization capture state. Replaces type String + isAttached Boolean. */
    var initializationState: InitializationState = InitializationState.STANDARD
)
