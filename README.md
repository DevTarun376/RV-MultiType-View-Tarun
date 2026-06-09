# MultiTypeView

A RecyclerView library for displaying grouped, multi-type lists with images, videos, and documents. Supports expand/collapse sections, pagination, delete mode with confirmation, and full visual theming.

---

<!-- DEMO VIDEO — replace the line below with your screen recording -->
<!-- Example: ![Demo](assets/demo.gif) or paste the GitHub-uploaded MP4 URL -->

<img width="216" height="480" alt="gif" src="https://github.com/user-attachments/assets/f2678b72-9fcd-47af-8307-9143cc7d90b4" />

---

## Why MultiTypeView

### Load 1000+ images and videos — zero ANR, zero OOM, zero lag

Most RecyclerView setups break down at scale. MultiTypeView is built from the ground up to handle large datasets without any of the common failure modes:

**Pagination — only what's visible is in memory**
The adapter never loads the full dataset into the RecyclerView at once. Items are loaded in pages (default 50 at a time) and the next page is fetched automatically as the user scrolls near the end. A list of 1,000 groups with images, videos, and documents stays smooth from top to bottom.

**Multi-type ViewHolder recycling — no cross-type binding waste**
Each row type (label, header, section, grid) has its own dedicated ViewHolder. RecyclerView recycles each type independently, so a grid cell is never accidentally bound as a header row and vice versa. This eliminates the binding overhead that causes jank in generic single-type adapters.

**Payload-based partial updates — no full rebinds**
When only the selection state or expand state of an item changes, the adapter dispatches a targeted payload instead of rebinding the whole view. The image already loaded in a grid cell is never cleared and reloaded just because a checkbox toggled.

**Glide image loading — off the main thread**
All image and video thumbnails are loaded via Glide, fully off the UI thread. The main thread is never blocked waiting for network or disk I/O, which is the primary cause of ANR on media-heavy lists.

**DiffUtil — O(N) diff, not full notifyDataSetChanged**
List updates use `DiffUtil.ItemCallback` to calculate the minimal set of insertions, removals, and changes. Deleting 10 items from a 1,000-item list animates only those 10 rows — the rest of the list is untouched.

**Expand/collapse without re-pagination**
Expanding or collapsing a section does not reset the paginator back to page 1. The user's scroll position is preserved and only the affected rows are added or removed from the visible list.

| Scenario | Result |
|---|---|
| 1,000 groups × 6 media items each | Smooth scroll, no lag |
| Fast scroll from top to bottom | No dropped frames, no OOM |
| Expanding all sections at once | No ANR, paginator adjusts in-place |
| Deleting 50 selected items | Instant animated removal via DiffUtil |
| Rotating the device mid-scroll | State preserved, no full reload |

---

## Structure

Each group in the list is made up of rows in a fixed order:

```
LABEL          ← group title row (expandable)
  HEADER       ← subtitle / date row
  SECTION_A    ← first section header (expandable)
    GRID items ← images and videos
  SECTION_B    ← second section header (expandable)
    GRID items ← documents (PDF, EML, images)
```

---

## Quick Start

### 1. Add to your layout

```xml
<androidx.recyclerview.widget.RecyclerView
    android:id="@+id/recyclerView"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

### 2. Set up in your Activity

```kotlin
class MyActivity : AppCompatActivity(), MultiTypeAdapterCallback {

    private lateinit var adapter: MultiTypeAdapter
    private lateinit var manager: MultiTypeRecyclerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = MultiTypeAdapter(listener = this)
        manager = MultiTypeRecyclerManager(this, binding.recyclerView, adapter)
        manager.loadItems(buildMyList())
    }

    override fun onItemClick(position: Int, list: List<MultiTypeItem>) {
        // image or video tapped
    }

    override fun onSecondaryItemClick(position: Int, list: List<MultiTypeItem>) {
        // document tapped
    }

    override fun onLongPressDelete(item: MultiTypeItem?) {
        // long press on any grid item — enter delete mode here
        adapter.setDeleteMode(true)
    }
}
```

---

## Building the Data List

Each group is a flat list of `MultiTypeItem` objects. Build them in order:

```kotlin
fun buildMyList(): List<MultiTypeItem> = buildList {

    // 1. Label row (the expandable group title)
    add(MultiTypeItem(
        type = MultiViewType.LABEL,
        id = "group-01",
        isExpanded = true,
        isVisible = true
    ))

    // 2. Header row (subtitle / date)
    add(MultiTypeItem(
        type = MultiViewType.HEADER,
        id = "group-01",
        header = "June 9, 2025",
        isVisible = true
    ))

    // 3. Section A header
    add(MultiTypeItem(
        type = MultiViewType.SECTION_A,
        id = "group-01",
        isExpanded = true,
        isVisible = true
    ))

    // 4. Grid items under Section A (images / videos)
    add(MultiTypeItem(
        type = MultiViewType.GRID,
        id = "group-01",
        mediaKind = MediaKind.IMAGE,
        itemUrl = "https://example.com/photo.jpg",
        imageIndex = 0,
        isVisible = true
    ))
    add(MultiTypeItem(
        type = MultiViewType.GRID,
        id = "group-01",
        mediaKind = MediaKind.VIDEO,
        itemUrl = "https://example.com/video.mp4",
        imageIndex = 1,
        isVisible = true
    ))

    // 5. Section B header
    add(MultiTypeItem(
        type = MultiViewType.SECTION_B,
        id = "group-01",
        isExpanded = true,
        isVisible = true
    ))

    // 6. Grid items under Section B (documents)
    add(MultiTypeItem(
        type = MultiViewType.GRID,
        id = "group-01",
        mediaKind = MediaKind.DOCUMENT,
        itemUrl = "report.pdf",
        name = "report.pdf",
        imageIndex = 0,
        isVisible = true
    ))
}
```

### MultiTypeItem fields

| Field | Type | Description |
|---|---|---|
| `type` | `Int` | Row type — use `MultiViewType` constants |
| `id` | `String?` | Group identifier — links all rows of the same group |
| `isExpanded` | `Boolean?` | Expanded state for LABEL / SECTION rows |
| `header` | `String?` | Text shown in HEADER rows |
| `mediaKind` | `MediaKind` | Grid content kind: `IMAGE`, `VIDEO`, `DOCUMENT`, `NONE` |
| `itemUrl` | `String?` | URL or file path for grid items |
| `name` | `String?` | Display name (used for documents) |
| `imageIndex` | `Int?` | Column position within the group (used for grid spacing) |
| `isVisible` | `Boolean?` | Controls visibility; set `false` for collapsed children |
| `isSelected` | `Boolean` | Selection state in delete mode (managed by adapter) |

### MultiViewType constants

| Constant | Value | Row type |
|---|---|---|
| `MultiViewType.LABEL` | 1 | Group title row |
| `MultiViewType.HEADER` | 2 | Subtitle / date row |
| `MultiViewType.SECTION_A` | 3 | First section header |
| `MultiViewType.SECTION_B` | 4 | Second section header |
| `MultiViewType.GRID` | 5 | Image / video / document cell |

---

## Configuration

Pass a `MultiTypeConfig` to `MultiTypeRecyclerManager` to tune layout and pagination:

```kotlin
manager = MultiTypeRecyclerManager(
    context = this,
    recyclerView = binding.recyclerView,
    adapter = adapter,
    config = MultiTypeConfig(
        pageSize = 50,               // items loaded per page
        phoneGridColumns = 3,        // grid columns on phones
        tabletGridColumns = 4,       // grid columns on tablets
        scrollPrefetchThreshold = 5, // load next page N items before the end
        showScrollbar = true         // show vertical scrollbar
    )
)
```

| Parameter | Default | Description |
|---|---|---|
| `pageSize` | 50 | Items loaded on initial load and each scroll trigger |
| `phoneGridColumns` | 3 | Grid span count on phones (screen width < 600dp) |
| `tabletGridColumns` | 4 | Grid span count on tablets (screen width ≥ 600dp) |
| `scrollPrefetchThreshold` | 5 | Items before end of list that triggers next page load |
| `showScrollbar` | `true` | Whether to show a scrollbar on the right edge |

---

## Theming

Pass a `MultiTypeTheme` to `MultiTypeAdapter` to customise colours and icons:

```kotlin
adapter = MultiTypeAdapter(
    listener = this,
    theme = MultiTypeTheme(
        // colours
        itemBackground        = "#E8F0FE".toColorInt(), // base — cascades to all rows
        labelTextColor        = "#1A73E8".toColorInt(),
        headerTextColor       = "#555555".toColorInt(),
        sectionLabelTextColor = "#222222".toColorInt(),
        gridCardBackground    = Color.WHITE,

        // override individual rows (optional)
        labelRowBackground    = "#FFFFFF".toColorInt(),
        sectionHeaderBackground = "#F5F5F5".toColorInt(),
    )
)
```

### All theme fields

**Text colours**

| Field | Default | Applies to |
|---|---|---|
| `labelTextColor` | `Color.BLACK` | Group title text |
| `headerTextColor` | `Color.BLACK` | Header / date text |
| `sectionLabelTextColor` | `Color.BLACK` | Section A / B label text |

**Backgrounds** — all cascade from `itemBackground`

| Field | Default | Applies to |
|---|---|---|
| `itemBackground` | `Color.TRANSPARENT` | Base — sets all surfaces unless overridden |
| `listBackground` | `itemBackground` | RecyclerView background |
| `labelRowBackground` | `itemBackground` | Label row background |
| `headerRowBackground` | `itemBackground` | Header row background |
| `sectionHeaderBackground` | `itemBackground` | Section A/B header background |
| `gridItemBackground` | `itemBackground` | Outer grid cell background |
| `gridCardBackground` | `Color.WHITE` | CardView inside each grid cell |

**Icons** — defaults are built-in drawables

| Field | Default drawable | Used for |
|---|---|---|
| `iconArrowExpanded` | `mtv_ic_arrow_down` | Expanded state arrow |
| `iconArrowCollapsed` | `mtv_ic_arrow_up` | Collapsed state arrow |
| `iconPlay` | `mtv_ic_play_video` | Video overlay |
| `iconCheckboxSelected` | `mtv_ic_checkbox_selected` | Selected item in delete mode |
| `iconCheckboxUnselected` | `mtv_ic_checkbox_unselected` | Unselected item in delete mode |
| `iconPlaceholder` | `mtv_placeholder` | Image loading / error fallback |
| `iconPdf` | `mtv_ic_pdf` | PDF document cell |
| `iconEmail` | `mtv_ic_email` | EML document cell |

---

## Delete Mode

Long-pressing any grid item fires `onLongPressDelete`. Implement delete mode in your activity:

```kotlin
// Enter / exit delete mode
adapter.setDeleteMode(true)
adapter.setDeleteMode(false)  // also clears all selections

// Know when selections change
adapter.onSelectionChanged = { selectedCount ->
    btnDelete.visibility = if (selectedCount > 0) View.VISIBLE else View.GONE
    btnDelete.text = "Delete Selected ($selectedCount)"
}

// Delete all selected items (removes them from the list permanently)
btnDelete.setOnClickListener {
    val count = adapter.getSelectedItemCount()
    AlertDialog.Builder(this)
        .setTitle("Delete Items")
        .setMessage("Delete $count item(s)? This cannot be undone.")
        .setPositiveButton("Delete") { _, _ -> adapter.deleteSelectedItems() }
        .setNegativeButton("Cancel", null)
        .show()
}
```

### Delete mode API

| Method / Property | Description |
|---|---|
| `adapter.enableDelete` | `true` when delete mode is active |
| `adapter.setDeleteMode(Boolean)` | Enable or disable delete mode |
| `adapter.onSelectionChanged` | Callback fired with updated count on every tap |
| `adapter.getSelectedItemCount()` | Returns how many items are currently selected |
| `adapter.deleteSelectedItems()` | Removes selected items and exits delete mode |

---

## Updating the List

```kotlin
// Replace entire dataset
manager.loadItems(newList)

// Update in-place (preserves pagination position)
adapter.updateListItems(newList)

// Clear all items
adapter.clear()
```

---

## Callbacks

Implement `MultiTypeAdapterCallback` or individual interfaces:

```kotlin
// All three in one interface
class MyActivity : AppCompatActivity(), MultiTypeAdapterCallback { ... }

// Or individual fun interfaces
val adapter = MultiTypeAdapter(
    listener = object : MultiTypeAdapterCallback {
        override fun onItemClick(position: Int, list: List<MultiTypeItem>) { }
        override fun onSecondaryItemClick(position: Int, list: List<MultiTypeItem>) { }
        override fun onLongPressDelete(item: MultiTypeItem?) { }
    }
)
```

| Callback | Triggered when |
|---|---|
| `onItemClick(position, list)` | An IMAGE or VIDEO grid cell is tapped |
| `onSecondaryItemClick(position, list)` | A DOCUMENT grid cell is tapped |
| `onLongPressDelete(item)` | Any grid cell is long-pressed |
