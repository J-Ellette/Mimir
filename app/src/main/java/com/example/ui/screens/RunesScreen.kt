package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Gesture
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PaywallFeature
import com.example.data.model.FlashcardProgressEntity
import com.example.data.model.RuneEntity
import com.example.data.repository.ReviewQuality
import com.example.data.repository.SpacedRepetitionEngine
import com.example.data.seed.RuneAlphabet
import com.example.data.seed.RuneItem
import com.example.data.seed.RuneTransliterator
import com.example.data.seed.RunesData
import com.example.ui.components.CarvedWoodCard
import com.example.ui.components.KnotworkDivider
import com.example.ui.components.LockedFeatureOverlay
import com.example.ui.components.ProLockBadge
import com.example.ui.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RunesScreen(
    roomRunes: List<RuneEntity>,
    flashcardProgress: List<FlashcardProgressEntity>,
    isProUnlocked: Boolean,
    onRecordFlashcard: (String, ReviewQuality) -> Unit,
    onOpenElderFutharkTransliterator: () -> Unit,
    onOpenElderFutharkFlashcards: () -> Unit,
    onRequestPaywall: (PaywallFeature) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Alphabets", "Trace & SRS", "Runestones", "Converter")

    val runeItems = remember(roomRunes) {
        if (roomRunes.isEmpty()) {
            RunesData.allRunes
        } else {
            roomRunes.map { r ->
                RuneItem(
                    id = r.id,
                    alphabet = runCatching { RuneAlphabet.valueOf(r.alphabet) }.getOrDefault(RuneAlphabet.ELDER_FUTHARK),
                    glyph = r.glyph,
                    name = r.name,
                    soundValue = r.soundValue,
                    meaning = r.meaning,
                    aett = r.aett,
                    lore = r.lore
                )
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            SectionHeader(
                title = "Learn the Runes",
                subtitle = "Elder Futhark · Younger Futhark · Anglo-Saxon Futhorc"
            )
        }

        PrimaryScrollableTabRow(selectedTabIndex = selectedTab, edgePadding = 16.dp) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) },
                    modifier = Modifier.testTag("runes_tab_$index")
                )
            }
        }

        when (selectedTab) {
            0 -> RuneAlphabetTab(runeItems)
            1 -> RuneTraceAndSrsTab(
                runeItems = runeItems,
                flashcardProgress = flashcardProgress,
                isProUnlocked = isProUnlocked,
                onRecordFlashcard = onRecordFlashcard,
                onOpenElderFutharkFlashcards = onOpenElderFutharkFlashcards,
                onRequestPaywall = onRequestPaywall
            )
            2 -> RunestoneDecoderTab(
                isProUnlocked = isProUnlocked,
                onRequestPaywall = onRequestPaywall
            )
            3 -> RuneConverterTab(onOpenElderFutharkTransliterator = onOpenElderFutharkTransliterator)
        }
    }
}

@Composable
private fun RuneAlphabetTab(allRunes: List<RuneItem>) {
    var activeAlphabet by remember { mutableStateOf(RuneAlphabet.ELDER_FUTHARK) }
    var selectedRune by remember { mutableStateOf<RuneItem?>(null) }

    val runes = remember(activeAlphabet, allRunes) {
        allRunes.filter { it.alphabet == activeAlphabet }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(RuneAlphabet.entries) { alpha ->
                    FilterChip(
                        selected = activeAlphabet == alpha,
                        onClick = { activeAlphabet = alpha },
                        label = { Text(alpha.title) }
                    )
                }
            }
        }

        item {
            CarvedWoodCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ) {
                Text(activeAlphabet.era, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(activeAlphabet.summary, style = MaterialTheme.typography.bodySmall)
            }
        }

        items(runes.chunked(2)) { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                pair.forEach { rune ->
                    CarvedWoodCard(
                        modifier = Modifier.weight(1f),
                        onClick = { selectedRune = rune }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = rune.glyph,
                                fontSize = 36.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = rune.soundValue,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(rune.name, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = rune.meaning,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )
                    }
                }
                if (pair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }

    if (selectedRune != null) {
        val r = selectedRune!!
        AlertDialog(
            onDismissRequest = { selectedRune = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(r.glyph, fontSize = 38.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(r.name, style = MaterialTheme.typography.headlineMedium)
                        Text("${r.alphabet.title} · ${r.aett}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Phonetic Value: ${r.soundValue}", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                    Text("Rune Name Meaning: ${r.meaning}", style = MaterialTheme.typography.bodyMedium)
                    KnotworkDivider()
                    Text("Attested Poem Lore", style = MaterialTheme.typography.labelLarge)
                    Text(r.lore, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedRune = null }) { Text("Close") }
            }
        )
    }
}

@Composable
private fun RuneTraceAndSrsTab(
    runeItems: List<RuneItem>,
    flashcardProgress: List<FlashcardProgressEntity>,
    isProUnlocked: Boolean,
    onRecordFlashcard: (String, ReviewQuality) -> Unit,
    onOpenElderFutharkFlashcards: () -> Unit,
    onRequestPaywall: (PaywallFeature) -> Unit
) {
    var modeTrace by remember { mutableStateOf(true) }
    val elderDeck = remember(runeItems, isProUnlocked) {
        val allElder = runeItems.filter { it.alphabet == RuneAlphabet.ELDER_FUTHARK }
            .ifEmpty { RunesData.elderFuthark }
        if (isProUnlocked) allElder else allElder.take(8)
    }
    val progressMap = remember(flashcardProgress) { flashcardProgress.associateBy { it.runeId } }
    val dueRunes = remember(flashcardProgress, elderDeck) {
        val (dueIds, _) = SpacedRepetitionEngine.partitionDueIds(
            cardIds = elderDeck.map { it.id },
            progressMap = progressMap,
            nowMillis = System.currentTimeMillis()
        )
        val byId = elderDeck.associateBy { it.id }
        dueIds.mapNotNull { byId[it] }.ifEmpty { elderDeck }
    }

    var cardIdx by remember { mutableIntStateOf(0) }
    var revealed by remember { mutableStateOf(false) }
    val currentRune = dueRunes[cardIdx % dueRunes.size]

    // Tracing state
    var traceRuneIdx by remember { mutableIntStateOf(0) }
    val traceRune = RunesData.elderFuthark[traceRuneIdx % RunesData.elderFuthark.size]
    val strokes = remember { mutableStateListOf<List<Offset>>() }
    var currentStroke by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var traceFeedback by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = modeTrace,
                    onClick = { modeTrace = true },
                    leadingIcon = { Icon(Icons.Outlined.Gesture, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    label = { Text("Tracing Practice") }
                )
                FilterChip(
                    selected = !modeTrace,
                    onClick = { modeTrace = false },
                    leadingIcon = { Icon(Icons.Outlined.Style, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    label = { Text("SM-2 Flashcards (${dueRunes.size} due)") }
                )
            }
        }

        if (modeTrace) {
            item {
                CarvedWoodCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("TRACE RUNE (${traceRuneIdx + 1}/24)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            Text("${traceRune.name} · ${traceRune.soundValue}", style = MaterialTheme.typography.headlineMedium)
                            Text(traceRune.meaning, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        OutlinedButton(onClick = {
                            strokes.clear()
                            currentStroke = emptyList()
                            traceFeedback = null
                            traceRuneIdx = (traceRuneIdx + 1) % RunesData.elderFuthark.size
                        }) {
                            Text("Next Rune")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    val strokeColor = MaterialTheme.colorScheme.primary
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = traceRune.glyph,
                                fontSize = 165.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.18f)
                            )
                            Canvas(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .pointerInput(traceRune.id) {
                                        detectDragGestures(
                                            onDragStart = { offset ->
                                                currentStroke = listOf(offset)
                                                traceFeedback = null
                                            },
                                            onDrag = { change, _ ->
                                                change.consume()
                                                currentStroke = currentStroke + change.position
                                            },
                                            onDragEnd = {
                                                if (currentStroke.size > 1) {
                                                    strokes.add(currentStroke)
                                                }
                                                currentStroke = emptyList()
                                            }
                                        )
                                    }
                            ) {
                                val all = strokes + listOf(currentStroke)
                                all.forEach { pts ->
                                    if (pts.size > 1) {
                                        val path = Path().apply {
                                            moveTo(pts.first().x, pts.first().y)
                                            for (i in 1 until pts.size) lineTo(pts[i].x, pts[i].y)
                                        }
                                        drawPath(
                                            path = path,
                                            color = strokeColor,
                                            style = Stroke(width = 14f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                strokes.clear()
                                currentStroke = emptyList()
                                traceFeedback = null
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Outlined.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Clear")
                        }
                        Button(
                            onClick = {
                                val totalPoints = strokes.sumOf { it.size }
                                traceFeedback = if (totalPoints >= 12) {
                                    "Well carved! ${strokes.size} stave stroke(s) recorded for ${traceRune.name}."
                                } else {
                                    "Trace firmly along the vertical stave and side-twigs before checking."
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Check Form")
                        }
                    }
                    if (traceFeedback != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(traceFeedback!!, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        } else {
            item {
                val mastered = flashcardProgress.count { it.repetitions >= 2 }
                val currentProgress = progressMap[currentRune.id]
                CarvedWoodCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("SM-2 SPACED REPETITION", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        Text("Mastered: $mastered/24 Elder", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { mastered.toFloat() / 24f },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = onOpenElderFutharkFlashcards,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_elder_flashcard_studio")
                    ) {
                        Text("Open Full Elder Futhark SRS Studio")
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(currentRune.alphabet.title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = currentRune.glyph,
                            fontSize = 84.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (revealed) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("${currentRune.name} · ${currentRune.soundValue}", style = MaterialTheme.typography.headlineMedium)
                            Text(currentRune.meaning, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = currentRune.lore,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { revealed = true },
                                modifier = Modifier.testTag("reveal_flashcard")
                            ) {
                                Text("Reveal Name & Sound")
                            }
                        }
                    }

                    if (revealed) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ReviewQuality.entries.forEach { quality ->
                                val intervalLabel = SpacedRepetitionEngine.previewIntervalLabel(currentProgress, quality)
                                OutlinedButton(
                                    onClick = {
                                        onRecordFlashcard(currentRune.id, quality)
                                        revealed = false
                                        cardIdx = (cardIdx + 1) % dueRunes.size
                                    },
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(quality.label, style = MaterialTheme.typography.labelSmall)
                                        Text(intervalLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RunestoneDecoderTab(
    isProUnlocked: Boolean,
    onRequestPaywall: (PaywallFeature) -> Unit
) {
    var selectedStoneIdx by remember { mutableIntStateOf(0) }
    var stepIdx by remember { mutableIntStateOf(0) }
    val stone = RunesData.inscriptions[selectedStoneIdx]
    val step = stone.steps[stepIdx.coerceIn(0, stone.steps.lastIndex)]

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(RunesData.inscriptions.indices.toList()) { idx ->
                    val isLockedStone = idx > 0 && !isProUnlocked
                    FilterChip(
                        selected = selectedStoneIdx == idx,
                        onClick = {
                            if (isLockedStone) {
                                onRequestPaywall(PaywallFeature.UNLIMITED_SRS)
                            } else {
                                selectedStoneIdx = idx
                                stepIdx = 0
                            }
                        },
                        trailingIcon = if (isLockedStone) {
                            { ProLockBadge(label = "Pro") }
                        } else {
                            null
                        },
                        label = { Text(RunesData.inscriptions[idx].title.substringBefore(" (")) }
                    )
                }
            }
        }

        item {
            CarvedWoodCard(modifier = Modifier.fillMaxWidth()) {
                Text(stone.title, style = MaterialTheme.typography.headlineMedium)
                Text("${stone.locationAndDate} · ${stone.alphabetUsed}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(6.dp))
                Text(stone.overview, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        item {
            CarvedWoodCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CLAUSE ${stepIdx + 1} OF ${stone.steps.size}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { if (stepIdx > 0) stepIdx-- },
                            enabled = stepIdx > 0
                        ) { Text("Prev") }
                        Button(
                            onClick = { if (stepIdx < stone.steps.lastIndex) stepIdx++ },
                            enabled = stepIdx < stone.steps.lastIndex
                        ) { Text("Next") }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text("1. Carved Younger/Elder Runes", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                Text(step.runes, fontSize = 24.sp, color = MaterialTheme.colorScheme.primary)
                KnotworkDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text("2. Epigraphic Transliteration", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                Text(step.transliteration, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(6.dp))
                Text("3. Normalized Old Norse", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                Text(step.normalizedOldNorse, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(6.dp))
                Text("4. English Translation", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                Text("\"${step.englishTranslation}\"", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = step.grammarNote,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }

        if (!isProUnlocked && RunesData.inscriptions.size > 1) {
            items(RunesData.inscriptions.drop(1), key = { it.id }) { lockedStone ->
                LockedFeatureOverlay(
                    isLocked = true,
                    label = lockedStone.title.substringBefore(" ("),
                    subtitle = "Historic Runestone Decoder · Tap to unlock Mímir Pro",
                    onUnlockClick = { onRequestPaywall(PaywallFeature.UNLIMITED_SRS) },
                    overlayTag = "locked_runestone_${lockedStone.id}"
                ) {
                    CarvedWoodCard(modifier = Modifier.fillMaxWidth()) {
                        Text(lockedStone.title, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "${lockedStone.locationAndDate} · ${lockedStone.alphabetUsed}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            lockedStone.overview,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RuneConverterTab(onOpenElderFutharkTransliterator: () -> Unit) {
    var inputName by remember { mutableStateOf("Astrid") }
    var targetAlphabet by remember { mutableStateOf(RuneAlphabet.ELDER_FUTHARK) }
    val converted = remember(inputName, targetAlphabet) {
        RuneTransliterator.transliterate(inputName, targetAlphabet)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            CarvedWoodCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.55f)
            ) {
                Text(
                    text = "Transliteration, Not Translation",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Runes are a phonetic writing system, not a separate language. Converting a modern name maps its sounds letter-by-letter into historical runic orthography—it does not translate English words into Old Norse.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onOpenElderFutharkTransliterator,
                    modifier = Modifier.testTag("open_elder_futhark_studio")
                ) {
                    Text("Open Elder Futhark Name Studio")
                }
            }
        }

        item {
            OutlinedTextField(
                value = inputName,
                onValueChange = { inputName = it },
                label = { Text("Enter name or phrase (supports þ, ð, ng, th)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("rune_converter_input"),
                singleLine = true
            )
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(RuneAlphabet.entries) { alpha ->
                    FilterChip(
                        selected = targetAlphabet == alpha,
                        onClick = { targetAlphabet = alpha },
                        label = { Text(alpha.title) }
                    )
                }
            }
        }

        item {
            CarvedWoodCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            ) {
                Text(
                    text = targetAlphabet.title.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = converted.ifBlank { "—" },
                    fontSize = 36.sp,
                    color = MaterialTheme.colorScheme.primary,
                    lineHeight = 46.sp
                )
            }
        }
    }
}
