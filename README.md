# MultiTypeView — Android RecyclerView Multi-Type Adapter Library

**A Kotlin Android library for building grouped, multi-type RecyclerView lists with images, videos, and documents.** Supports expand/collapse sections, infinite scroll pagination, delete mode with confirmation, and full visual theming — with zero ANR and zero OOM on 1000+ item lists.

[![JitPack](https://jitpack.io/v/DevTarun376/RV-MultiType-View-Tarun.svg)](https://jitpack.io/#DevTarun376/RV-MultiType-View-Tarun)
[![Min SDK](https://img.shields.io/badge/minSdk-29-brightgreen)](https://developer.android.com/about/versions/10)
[![License](https://img.shields.io/badge/license-MIT-blue)](LICENSE)

### Features
- **Multi-type ViewHolders** — Label, Header, Section, and Grid rows with dedicated recycling per type
- **Paginated loading** — only visible items stay in memory; next page loads automatically on scroll
- **Expand / collapse sections** — animated, without resetting the paginator
- **Delete mode** — long-press to enter, checkbox selection, DiffUtil-animated removal
- **Full theming** — colors, icons, backgrounds via `MultiTypeTheme`
- **Glide image loading** — images and video thumbnails loaded off the main thread
- **Tablet-aware grid** — configurable column count for phones and tablets

---

<img width="216" height="480" alt="MultiTypeView Android RecyclerView demo showing grouped media list with expand/collapse sections, images and videos in a grid layout" src="https://github.com/user-attachments/assets/f2678b72-9fcd-47af-8307-9143cc7d90b4" />

---

## Installation

**Step 1 — add JitPack to your `settings.gradle.kts`:**

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

**Step 2 — add the dependency to your app's `build.gradle.kts`:**

```kotlin
dependencies {
    implementation("com.github.DevTarun376:RV-MultiType-View-Tarun:1.1.0")
}
```

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
LABEL          ← group title row (expandable) — text from item.label
  HEADER       ← subtitle / date row — text from item.header
  SECTION_A    ← first section header (expandable) — text from item.label
    GRID items ← images and videos
  SECTION_B    ← second section header (expandable) — text from item.label
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

    // 1. Label row — expandable group title
    add(MultiTypeItem(
        type = MultiViewType.LABEL,
        id = "group-01",
        label = "Group 01",       // displayed as the group title
        isExpanded = true,
        isVisible = true
    ))

    // 2. Header row — subtitle / date shown below the label
    add(MultiTypeItem(
        type = MultiViewType.HEADER,
        id = "group-01",
        header = "June 9, 2025",  // displayed as the subtitle
        isVisible = true
    ))

    // 3. Section A header — expandable, label is the section title
    add(MultiTypeItem(
        type = MultiViewType.SECTION_A,
        id = "group-01",
        label = "Photos",         // displayed as the section A heading
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

    // 5. Section B header — expandable, label is the section title
    add(MultiTypeItem(
        type = MultiViewType.SECTION_B,
        id = "group-01",
        label = "Documents",      // displayed as the section B heading
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

| Field | Type | Used by | Description |
|---|---|---|---|
| `type` | `Int` | all | Row type — use `MultiViewType` constants |
| `id` | `String?` | all | Group identifier — links all rows of the same group |
| `label` | `String?` | LABEL, SECTION_A, SECTION_B | Display text for the row heading |
| `isExpanded` | `Boolean?` | LABEL, SECTION_A, SECTION_B | Expanded state; collapsed hides all child rows |
| `header` | `String?` | HEADER | Subtitle / date text shown under the label |
| `mediaKind` | `MediaKind` | GRID | Content kind: `IMAGE`, `VIDEO`, `DOCUMENT`, `NONE` |
| `itemUrl` | `String?` | GRID | URL or file path for the media item |
| `name` | `String?` | GRID | Display filename (shown on DOCUMENT cells) |
| `imageIndex` | `Int?` | GRID | Column position within the group (used for grid spacing) |
| `isVisible` | `Boolean?` | all | Controls visibility; `false` hides the row (collapsed children) |
| `isSelected` | `Boolean` | GRID | Selection state in delete mode — managed by the adapter |

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
| `pageSize` | `50` | Items loaded on initial load and each scroll trigger |
| `phoneGridColumns` | `3` | Grid span count on phones (screen width < 600dp) |
| `tabletGridColumns` | `4` | Grid span count on tablets (screen width ≥ 600dp) |
| `scrollPrefetchThreshold` | `5` | Items before end of list that triggers next page load |
| `showScrollbar` | `true` | Whether to show a scrollbar on the right edge |

---

## Theming

Pass a `MultiTypeTheme` to `MultiTypeAdapter` to customise colours and icons:

```kotlin
adapter = MultiTypeAdapter(
    listener = this,
    theme = MultiTypeTheme(
        // base colour — cascades to all row backgrounds unless individually overridden
        itemBackground        = "#E8F0FE".toColorInt(),

        // text colours
        labelTextColor        = "#1A73E8".toColorInt(),
        headerTextColor       = "#555555".toColorInt(),
        sectionLabelTextColor = "#222222".toColorInt(),

        // card background inside each grid cell
        gridCardBackground    = Color.WHITE,

        // optionally override individual row backgrounds
        labelRowBackground    = "#FFFFFF".toColorInt(),
        sectionHeaderBackground = "#F5F5F5".toColorInt(),
    )
)
```

### All theme fields

**Text colours**

| Field | Default | Applies to |
|---|---|---|
| `labelTextColor` | `Color.BLACK` | LABEL row text |
| `headerTextColor` | `Color.BLACK` | HEADER row text |
| `sectionLabelTextColor` | `Color.BLACK` | SECTION_A / SECTION_B label text |

**Backgrounds** — all cascade from `itemBackground`

| Field | Default | Applies to |
|---|---|---|
| `itemBackground` | `Color.TRANSPARENT` | Base — sets all surfaces unless overridden |
| `listBackground` | `itemBackground` | RecyclerView background |
| `labelRowBackground` | `itemBackground` | LABEL row background |
| `headerRowBackground` | `itemBackground` | HEADER row background |
| `sectionHeaderBackground` | `itemBackground` | SECTION_A / SECTION_B header background |
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

// React when selections change
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
| `adapter.onSelectionChanged` | Lambda fired with updated selected count on every tap |
| `adapter.getSelectedItemCount()` | Returns how many items are currently selected |
| `adapter.deleteSelectedItems()` | Removes selected items and exits delete mode |

---

## Updating the List

```kotlin
// Replace entire dataset (resets pagination)
manager.loadItems(newList)

// Update in-place (preserves current pagination position)
adapter.updateListItems(newList)

// Clear all items
adapter.clear()
```

---

## Callbacks

Implement `MultiTypeAdapterCallback` or the individual `fun interface` types:

```kotlin
// All three in one interface
class MyActivity : AppCompatActivity(), MultiTypeAdapterCallback { ... }

// Or as an anonymous object
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

`position` is the index within the filtered sub-list for that group and media kind, not the adapter position. `list` contains only the relevant items (images+videos or documents) for that group.

---

## Get the Code from GitHub

Clone the repository and open it in Android Studio:

```bash
git clone https://github.com/DevTarun376/RV-MultiType-View-Tarun.git
cd RV-MultiType-View-Tarun
```

Open the project in Android Studio, let Gradle sync, then run the **app** module on any device or emulator to see a working sample. The sample app in `app/` wires up 1,000 pre-populated groups so you can see pagination, expand/collapse, delete mode, and theming all in one place.

---

## Complete Example — Photo Gallery Grouped by Date

A full activity showing all features together. Each group is one day; Section A holds photos and videos, Section B holds documents.

```kotlin
class GalleryActivity : AppCompatActivity(), MultiTypeAdapterCallback {

    private lateinit var binding: ActivityGalleryBinding
    private lateinit var adapter: MultiTypeAdapter
    private lateinit var manager: MultiTypeRecyclerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = MultiTypeAdapter(
            listener = this,
            theme = MultiTypeTheme(
                itemBackground        = Color.parseColor("#F8F9FA"),
                labelTextColor        = Color.parseColor("#1A73E8"),
                headerTextColor       = Color.parseColor("#666666"),
                sectionLabelTextColor = Color.parseColor("#333333"),
                gridCardBackground    = Color.WHITE
            )
        )

        manager = MultiTypeRecyclerManager(
            context = this,
            recyclerView = binding.recyclerView,
            adapter = adapter,
            config = MultiTypeConfig(
                pageSize = 30,
                phoneGridColumns = 3,
                tabletGridColumns = 5,
                scrollPrefetchThreshold = 8,
                showScrollbar = true
            )
        )

        manager.loadItems(buildGalleryItems())

        adapter.onSelectionChanged = { count ->
            binding.btnDelete.visibility = if (count > 0) View.VISIBLE else View.GONE
            binding.btnDelete.text = "Delete ($count)"
        }

        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Remove ${adapter.getSelectedItemCount()} item(s)?")
                .setPositiveButton("Delete") { _, _ -> adapter.deleteSelectedItems() }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun buildGalleryItems(): List<MultiTypeItem> = buildList {
        // ── Day 1 ──────────────────────────────────────────────────────────────
        add(MultiTypeItem(type = MultiViewType.LABEL,     id = "day-1", label = "Day 1",     isExpanded = true, isVisible = true))
        add(MultiTypeItem(type = MultiViewType.HEADER,    id = "day-1", header = "June 9, 2025",               isVisible = true))
        add(MultiTypeItem(type = MultiViewType.SECTION_A, id = "day-1", label = "Photos",    isExpanded = true, isVisible = true))
        add(MultiTypeItem(type = MultiViewType.GRID, id = "day-1", mediaKind = MediaKind.IMAGE,
            itemUrl = "https://example.com/day1_photo1.jpg", imageIndex = 0, isVisible = true))
        add(MultiTypeItem(type = MultiViewType.GRID, id = "day-1", mediaKind = MediaKind.VIDEO,
            itemUrl = "https://example.com/day1_video.mp4",  imageIndex = 1, isVisible = true))
        add(MultiTypeItem(type = MultiViewType.SECTION_B, id = "day-1", label = "Documents", isExpanded = true, isVisible = true))
        add(MultiTypeItem(type = MultiViewType.GRID, id = "day-1", mediaKind = MediaKind.DOCUMENT,
            itemUrl = "notes.pdf", name = "notes.pdf", imageIndex = 0, isVisible = true))

        // ── Day 2 ──────────────────────────────────────────────────────────────
        add(MultiTypeItem(type = MultiViewType.LABEL,     id = "day-2", label = "Day 2",     isExpanded = true, isVisible = true))
        add(MultiTypeItem(type = MultiViewType.HEADER,    id = "day-2", header = "June 8, 2025",               isVisible = true))
        add(MultiTypeItem(type = MultiViewType.SECTION_A, id = "day-2", label = "Photos",    isExpanded = true, isVisible = true))
        add(MultiTypeItem(type = MultiViewType.GRID, id = "day-2", mediaKind = MediaKind.IMAGE,
            itemUrl = "https://example.com/day2_photo1.jpg", imageIndex = 0, isVisible = true))
        add(MultiTypeItem(type = MultiViewType.GRID, id = "day-2", mediaKind = MediaKind.IMAGE,
            itemUrl = "https://example.com/day2_photo2.jpg", imageIndex = 1, isVisible = true))
    }

    override fun onItemClick(position: Int, list: List<MultiTypeItem>) {
        val item = list[position]
        // open full-screen viewer with item.itemUrl
    }

    override fun onSecondaryItemClick(position: Int, list: List<MultiTypeItem>) {
        val item = list[position]
        // open document viewer with item.itemUrl / item.name
    }

    override fun onLongPressDelete(item: MultiTypeItem?) {
        adapter.setDeleteMode(true)
    }
}
```

---

## Flexibility & How to Extend

MultiTypeView is intentionally thin: the library owns the adapter, paginator, and ViewHolder wiring — everything else is yours to control.

### Map your own data model

`MultiTypeItem` is a plain data class. Map your domain objects to it in a single function:

```kotlin
fun MyPhoto.toMultiTypeItem(groupId: String, index: Int) = MultiTypeItem(
    type       = MultiViewType.GRID,
    id         = groupId,
    mediaKind  = if (isVideo) MediaKind.VIDEO else MediaKind.IMAGE,
    itemUrl    = url,
    imageIndex = index,
    isVisible  = true
)
```

Call that mapper in a `buildList {}` block and pass the result to `manager.loadItems()`. The library never touches your model layer.

### Drive the list from a ViewModel

Because `loadItems` and `updateListItems` accept any `List<MultiTypeItem>`, you can feed them directly from a `StateFlow` or `LiveData`:

```kotlin
viewModel.galleryState.observe(this) { items ->
    adapter.updateListItems(items)   // DiffUtil handles the diff — no flicker
}
```

### Add new row types

The adapter maps `type` integers to ViewHolder classes. To add a custom row (e.g. an ad banner or a date-divider):

1. Define a new constant outside the `MultiViewType` range (e.g. `const val AD_BANNER = 10`).
2. Subclass `MultiTypeAdapter`, override `getItemViewType`, `onCreateViewHolder`, and `onBindViewHolder` for your new type.
3. Pass your subclass to `MultiTypeRecyclerManager` — the paginator and expand/collapse logic are unaffected.

### Extension points summary

| What you want to change | Where to do it |
|---|---|
| Data mapping | Your own mapper function — no library code touched |
| Image loading / caching | Glide configuration or a subclassed ViewHolder |
| Pagination trigger distance | `MultiTypeConfig.scrollPrefetchThreshold` |
| Column count per screen size | `MultiTypeConfig.phoneGridColumns` / `tabletGridColumns` |
| Colors, icons, backgrounds | `MultiTypeTheme` fields |
| Row heading text | `MultiTypeItem.label` on LABEL / SECTION_A / SECTION_B items |
| New row types | Subclass `MultiTypeAdapter` |
| Callbacks / navigation | `MultiTypeAdapterCallback` implementation |

---

## Requirements

| Requirement | Minimum |
|---|---|
| Android SDK | API 29 (Android 10) |
| Kotlin | 1.8+ |
| RecyclerView | 1.3+ |
| Glide | 4.x |

---

## License

```
MIT License

Copyright (c) 2026 Tarun Kumar

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
