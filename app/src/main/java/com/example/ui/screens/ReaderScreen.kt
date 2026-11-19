package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.FormatSize
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ViewColumn
import androidx.compose.material.icons.outlined.ViewStream
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserSettings
import com.example.data.model.BookEntity
import com.example.data.model.ChapterEntity
import com.example.data.model.CommentaryPackEntity
import com.example.data.model.DiscussionPostEntity
import com.example.data.model.GlossaryEntryEntity
import com.example.data.model.HighlightColor
import com.example.data.model.UserAnnotationEntity
import com.example.data.model.VerseEntity
import com.example.data.seed.NorseLexiconEntry
import com.example.data.seed.OldNorseLexicon
import com.example.ui.components.AttestationBadge
import com.example.ui.components.CarvedWoodCard
import com.example.ui.components.KnotworkDivider
import com.example.ui.theme.HighlightGold
import com.example.ui.theme.HighlightRust
import com.example.ui.theme.HighlightTeal
import com.example.util.RunestoneShareHelper

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ReaderScreen(
    book: BookEntity?,
    chapters: List<ChapterEntity>,
    verses: List<VerseEntity>,
    targetVerseId: String?,
    settings: UserSettings,
    annotations: List<UserAnnotationEntity>,
    glossary: List<GlossaryEntryEntity>,
    commentaryPacks: List<CommentaryPackEntity>,
    discussions: List<DiscussionPostEntity>,
    onBack: () -> Unit,
    onToggleParallelMode: (Boolean) -> Unit,
    onChangeFontScale: (Float) -> Unit,
    onSetHighlight: (String, String, HighlightColor) -> Unit,
    onToggleBookmark: (String, String, Boolean) -> Unit,
    onSaveNote: (String, String, String, Boolean) -> Unit,
    onPostDiscussion: (String, String, String) -> Unit,
    onSpeakOldNorse: (String) -> Unit
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val listState = rememberLazyListState()
    var inspectedWord by remember { mutableStateOf<NorseLexiconEntry?>(null) }
    var inspectedGlossary by remember { mutableStateOf<GlossaryEntryEntity?>(null) }
    var noteDialogVerse by remember { mutableStateOf<VerseEntity?>(null) }
    var discussionVerse by remember { mutableStateOf<VerseEntity?>(null) }
    var showFontDialog by remember { mutableStateOf(false) }

    val installedPackIds = remember(commentaryPacks) {
        commentaryPacks.filter { it.isInstalled }.map { it.id }.toSet()
    }
    val annotationMap = remember(annotations) { annotations.associateBy { it.passageId } }
    val chapterMap = remember(chapters) { chapters.associateBy { it.id } }

    LaunchedEffect(targetVerseId, verses) {
        if (targetVerseId != null) {
            val idx = verses.indexOfFirst { it.id == targetVerseId }
            if (idx >= 0) listState.animateScrollToItem(idx)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = book?.title ?: "Reader",
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1
                    )
                    Text(
                        text = book?.oldNorseTitle ?: "",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack, modifier = Modifier.testTag("reader_back")) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back to Library")
                }
            },
            actions = {
                IconButton(
                    onClick = { onToggleParallelMode(!settings.parallelSideBySide) },
                    modifier = Modifier.testTag("toggle_parallel_view")
                ) {
                    Icon(
                        imageVector = if (settings.parallelSideBySide) Icons.Outlined.ViewColumn else Icons.Outlined.ViewStream,
                        contentDescription = "Toggle side-by-side or stacked parallel view",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = { showFontDialog = true }) {
                    Icon(Icons.Outlined.FormatSize, contentDescription = "Adjust serif font size")
                }
            }
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (book != null) {
                item {
                    CarvedWoodCard(
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                    ) {
                        Text(
                            text = "Tap any Old Norse word for instant grammar & meaning.",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Manuscript: ${book.manuscriptSource} · Translation: ${book.translator}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            items(verses, key = { it.id }) { verse ->
                val chapter = chapterMap[verse.chapterId]
                val ann = annotationMap[verse.id]
                val highlightBg = when (ann?.highlightColor) {
                    HighlightColor.GOLD -> HighlightGold
                    HighlightColor.TEAL -> HighlightTeal
                    HighlightColor.RUST -> HighlightRust
                    else -> MaterialTheme.colorScheme.surface
                }
                val verseDiscussions = discussions.filter { it.passageId == verse.id }
                val inlineGlossary = remember(verse.id, glossary) {
                    glossary.filter { g ->
                        verse.oldNorseText.contains(g.term, ignoreCase = true) ||
                            verse.englishText.contains(g.term, ignoreCase = true) ||
                            verse.oldNorseText.contains(g.oldNorseForm.substringBefore(" "), ignoreCase = true)
                    }
                }

                CarvedWoodCard(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = highlightBg
                ) {
                    if (chapter != null) {
                        Text(
                            text = "Chapter ${chapter.chapterNumber}: ${chapter.title}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = verse.numberLabel,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { onSpeakOldNorse(verse.oldNorseText) }) {
                                Icon(
                                    Icons.Outlined.VolumeUp,
                                    contentDescription = "Hear Old Norse pronunciation",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                            IconButton(
                                onClick = {
                                    onToggleBookmark(
                                        verse.id,
                                        verse.bookId,
                                        ann?.isBookmarked == true
                                    )
                                },
                                modifier = Modifier.testTag("bookmark_${verse.id}")
                            ) {
                                Icon(
                                    imageVector = if (ann?.isBookmarked == true) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = "Bookmark verse",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(onClick = { noteDialogVerse = verse }) {
                                Icon(Icons.Outlined.EditNote, contentDescription = "Add study note")
                            }
                            IconButton(
                                onClick = {
                                    RunestoneShareHelper.shareRunestoneCard(
                                        context = context,
                                        title = book?.title ?: "Mímir",
                                        oldNorse = verse.oldNorseText,
                                        english = verse.englishText,
                                        citation = "${book?.title ?: ""} · ${verse.numberLabel}"
                                    )
                                }
                            ) {
                                Icon(Icons.Outlined.Share, contentDescription = "Share verse card")
                            }
                        }
                    }

                    AttestationBadge(
                        isPrimaryAttested = verse.isPrimaryAttested,
                        label = verse.attestationSummary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val bodySize = (18f * settings.readerFontScale).sp
                    val bodyLine = (26f * settings.readerFontScale).sp

                    if (settings.parallelSideBySide) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("OLD NORSE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(4.dp))
                                InteractiveOldNorseText(
                                    text = verse.oldNorseText,
                                    fontSizeSp = bodySize.value,
                                    onWordTap = { word -> inspectedWord = OldNorseLexicon.lookupWord(word) }
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("ENGLISH", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = verse.englishText,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = bodySize,
                                        lineHeight = bodyLine,
                                        fontStyle = FontStyle.Italic
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        Text("OLD NORSE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(4.dp))
                        InteractiveOldNorseText(
                            text = verse.oldNorseText,
                            fontSizeSp = bodySize.value,
                            onWordTap = { word -> inspectedWord = OldNorseLexicon.lookupWord(word) }
                        )
                        KnotworkDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Text(
                            text = verse.englishText,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = bodySize,
                                lineHeight = bodyLine,
                                fontStyle = FontStyle.Italic
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (inlineGlossary.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            inlineGlossary.forEach { entry ->
                                AssistChip(
                                    onClick = { inspectedGlossary = entry },
                                    label = { Text("${entry.term} (${entry.category.label})") }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Highlight:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        HighlightColor.entries.forEach { color ->
                            val selected = (ann?.highlightColor ?: HighlightColor.NONE) == color
                            FilterChip(
                                selected = selected,
                                onClick = { onSetHighlight(verse.id, verse.bookId, color) },
                                label = { Text(color.label, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }

                    if (!ann?.noteText.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = (if (ann!!.isPrivateVault) "[Vault Note] " else "[Note] ") + ann.noteText,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("Scholarly & Source Note", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                            Text(verse.scholarlyNote, style = MaterialTheme.typography.bodySmall)
                            if (installedPackIds.contains("pack_linguistic_meter")) {
                                Text("Philology Pack: ${verse.linguisticPackNote}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                            }
                            if (installedPackIds.contains("pack_skaldic_kennings")) {
                                Text("Kennings Pack: ${verse.kenningPackNote}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (installedPackIds.contains("pack_runology_epigraphy")) {
                                Text("Runology Pack: ${verse.runologyPackNote}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    TextButton(
                        onClick = { discussionVerse = verse },
                        modifier = Modifier.testTag("open_thread_${verse.id}")
                    ) {
                        Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reading Circle Thread (${verseDiscussions.size})")
                    }
                }
            }
        }
    }

    if (inspectedWord != null) {
        val entry = inspectedWord!!
        ModalBottomSheet(onDismissRequest = { inspectedWord = null }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(entry.surfaceForm, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                        Text("Lemma: ${entry.lemma}", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
                    }
                    IconButton(onClick = { onSpeakOldNorse(entry.surfaceForm) }) {
                        Icon(Icons.Outlined.VolumeUp, contentDescription = "Hear word pronunciation")
                    }
                }
                Text("${entry.partOfSpeech} · ${entry.grammarTag}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Meaning: ${entry.englishGloss}", style = MaterialTheme.typography.titleMedium)
                if (entry.note.isNotBlank()) {
                    Text(entry.note, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (inspectedGlossary != null) {
        val g = inspectedGlossary!!
        AlertDialog(
            onDismissRequest = { inspectedGlossary = null },
            title = { Text("${g.term} (${g.oldNorseForm})", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(g.definition, style = MaterialTheme.typography.bodyMedium)
                    Text("Primary Sources: ${g.primarySource}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Text(g.sourceDistinctionNote, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = { TextButton(onClick = { inspectedGlossary = null }) { Text("Close") } }
        )
    }

    if (noteDialogVerse != null) {
        val v = noteDialogVerse!!
        val existing = annotationMap[v.id]
        var draft by remember(v.id) { mutableStateOf(existing?.noteText ?: "") }
        var isPrivate by remember(v.id) { mutableStateOf(existing?.isPrivateVault ?: false) }
        AlertDialog(
            onDismissRequest = { noteDialogVerse = null },
            title = { Text("Study Note · ${v.numberLabel}", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = draft,
                        onValueChange = { draft = it },
                        label = { Text("Your commentary or reflection") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isPrivate, onCheckedChange = { isPrivate = it })
                        Text("Protect in Private Vault (Biometric Lock)", style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    onSaveNote(v.id, v.bookId, draft, isPrivate)
                    noteDialogVerse = null
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { noteDialogVerse = null }) { Text("Cancel") } }
        )
    }

    if (discussionVerse != null) {
        val v = discussionVerse!!
        val thread = discussions.filter { it.passageId == v.id }
        var replyText by remember { mutableStateOf("") }
        var stance by remember { mutableStateOf("SCHOLARLY") }

        ModalBottomSheet(onDismissRequest = { discussionVerse = null }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Reading Circle · ${v.numberLabel}", style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "Open to scholarly & devotional perspectives. Stored offline; syncable via encrypted local backup.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (thread.isEmpty()) {
                    Text("No notes posted on this verse yet. Start the discussion below.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    thread.forEach { post ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(post.authorName, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                                    Text(post.stanceTag, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(post.body, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("SCHOLARLY", "DEVOTIONAL", "LINGUISTIC").forEach { tag ->
                        FilterChip(
                            selected = stance == tag,
                            onClick = { stance = tag },
                            label = { Text(tag, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
                OutlinedTextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    label = { Text("Add your reflection or philological note") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        if (replyText.isNotBlank()) {
                            onPostDiscussion(v.id, stance, replyText)
                            replyText = ""
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) { Text("Post to Thread") }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showFontDialog) {
        AlertDialog(
            onDismissRequest = { showFontDialog = false },
            title = { Text("Serif Reading Scale", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column {
                    Text("Scale: ${(settings.readerFontScale * 100).toInt()}%")
                    Slider(
                        value = settings.readerFontScale,
                        onValueChange = onChangeFontScale,
                        valueRange = 0.85f..1.45f
                    )
                }
            },
            confirmButton = { TextButton(onClick = { showFontDialog = false }) { Text("Done") } }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun InteractiveOldNorseText(
    text: String,
    fontSizeSp: Float,
    onWordTap: (String) -> Unit
) {
    val lines = remember(text) { text.split("\n") }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        lines.forEach { line ->
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                line.split(" ").filter { it.isNotEmpty() }.forEach { token ->
                    Text(
                        text = token,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = fontSizeSp.sp,
                            lineHeight = (fontSizeSp * 1.38f).sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { onWordTap(token) }
                            .padding(horizontal = 2.dp, vertical = 1.dp)
                    )
                }
            }
        }
    }
}
