package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.PaywallFeature
import com.example.data.model.BookEntity
import com.example.data.model.CommentaryPackEntity
import com.example.data.model.UserAnnotationEntity
import com.example.data.model.VerseEntity
import com.example.data.model.WorkCategory
import com.example.ui.components.CarvedWoodCard
import com.example.ui.components.LockedFeatureOverlay
import com.example.ui.components.ProLockBadge
import com.example.ui.components.SectionHeader

@Composable
fun LibraryScreen(
    books: List<BookEntity>,
    searchQuery: String,
    searchResults: List<VerseEntity>,
    annotations: List<UserAnnotationEntity>,
    commentaryPacks: List<CommentaryPackEntity>,
    isProUnlocked: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onTogglePackInstalled: (String, Boolean) -> Unit,
    onOpenBook: (String, String?) -> Unit,
    onRequestPaywall: (PaywallFeature) -> Unit
) {
    var selectedCategory by remember { mutableStateOf<WorkCategory?>(null) }
    var showBookmarksOnly by remember { mutableStateOf(false) }
    var showPacksDialog by remember { mutableStateOf(false) }

    val bookmarkedVerseIds = remember(annotations) {
        annotations.filter { it.isBookmarked }.map { it.passageId }.toSet()
    }
    val bookmarkedBookIds = remember(annotations) {
        annotations.filter { it.isBookmarked }.map { it.workId }.toSet()
    }

    val filteredBooks = remember(books, selectedCategory, showBookmarksOnly, bookmarkedBookIds) {
        books.filter { book ->
            val catMatch = selectedCategory == null || book.category == selectedCategory
            val bmMatch = !showBookmarksOnly || bookmarkedBookIds.contains(book.id)
            catMatch && bmMatch
        }
    }

    val bookMap = remember(books) { books.associateBy { it.id } }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(104.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_manuscript_scroll),
                contentDescription = "Icelandic vellum manuscript library header",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.45f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "The Norse Corpus",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Public Domain Eddas, Sagas & Modular Packs",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                OutlinedButton(
                    onClick = { showPacksDialog = true },
                    modifier = Modifier.testTag("manage_packs_button")
                ) {
                    Icon(Icons.Outlined.Extension, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Packs")
                }
            }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("library_search_input"),
            placeholder = { Text("Search Old Norse, English, or notes (e.g., Mímir, Ymir, Guðrún)") },
            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = "Search corpus") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Outlined.Close, contentDescription = "Clear search")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedCategory == null && !showBookmarksOnly,
                    onClick = {
                        selectedCategory = null
                        showBookmarksOnly = false
                    },
                    label = { Text("All (${books.size})") }
                )
            }
            item {
                FilterChip(
                    selected = showBookmarksOnly,
                    onClick = { showBookmarksOnly = !showBookmarksOnly },
                    leadingIcon = { Icon(Icons.Outlined.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    label = { Text("Bookmarks (${bookmarkedVerseIds.size})") }
                )
            }
            items(WorkCategory.entries) { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = if (selectedCategory == cat) null else cat },
                    label = { Text(cat.label) }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (searchQuery.trim().length >= 2) {
                item {
                    SectionHeader(
                        title = "Corpus Matches (${searchResults.size})",
                        subtitle = "Instant Room SQL query across Old Norse & translations"
                    )
                }
                if (searchResults.isEmpty()) {
                    item {
                        CarvedWoodCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "No verses matched \"$searchQuery\". Try terms like Óðinn, Yggdrasill, dómr, or Hlíðarendi.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(searchResults, key = { it.id }) { verse ->
                        val b = bookMap[verse.bookId]
                        CarvedWoodCard(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onOpenBook(verse.bookId, verse.id) }
                        ) {
                            Text(
                                text = "${b?.title ?: verse.bookId} · ${verse.numberLabel}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = verse.oldNorseText,
                                style = MaterialTheme.typography.bodyLarge,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = verse.englishText,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            } else {
                items(filteredBooks, key = { it.id }) { book ->
                    CarvedWoodCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("work_card_${book.id}"),
                        onClick = { onOpenBook(book.id, null) }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = book.category.label.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = book.title,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = book.oldNorseTitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = book.licenseNote,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = book.summary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "MS: ${book.manuscriptSource} · ${book.translator}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }
    }

    if (showPacksDialog) {
        AlertDialog(
            onDismissRequest = { showPacksDialog = false },
            title = { Text("Modular Commentary Packs", style = MaterialTheme.typography.titleLarge) },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(commentaryPacks, key = { it.id }) { pack ->
                        val isProLocked = !pack.isCoreRequired && !isProUnlocked
                        LockedFeatureOverlay(
                            isLocked = isProLocked,
                            label = pack.title,
                            subtitle = "Scholarly Pack · Tap to unlock Mímir Pro",
                            onUnlockClick = {
                                showPacksDialog = false
                                onRequestPaywall(PaywallFeature.LICENSED_PACKS)
                            },
                            overlayTag = "locked_pack_${pack.id}"
                        ) {
                            Surface(
                                onClick = {
                                    if (isProLocked) {
                                        showPacksDialog = false
                                        onRequestPaywall(PaywallFeature.LICENSED_PACKS)
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = pack.title,
                                                style = MaterialTheme.typography.titleSmall,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.weight(1f, fill = false)
                                            )
                                            if (isProLocked) {
                                                ProLockBadge()
                                            }
                                        }
                                        Text(
                                            text = pack.editionSubtitle,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = pack.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = pack.sizeLabel,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Switch(
                                        checked = if (isProLocked) false else pack.isInstalled,
                                        enabled = !pack.isCoreRequired,
                                        onCheckedChange = { checked ->
                                            if (isProLocked) {
                                                showPacksDialog = false
                                                onRequestPaywall(PaywallFeature.LICENSED_PACKS)
                                            } else {
                                                onTogglePackInstalled(pack.id, checked)
                                            }
                                        },
                                        modifier = Modifier.testTag("pack_switch_${pack.id}")
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPacksDialog = false }) { Text("Done") }
            }
        )
    }
}
