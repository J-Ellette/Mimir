package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Flip
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
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
import com.example.data.seed.RunesData
import com.example.ui.components.CarvedWoodCard
import com.example.ui.components.KnotworkDivider
import com.example.ui.components.LockedFeatureOverlay
import com.example.ui.components.ProLockBadge

enum class ElderFlashcardPromptMode(val label: String, val hint: String) {
    RUNE_TO_DETAILS("Rune -> Name & Sound", "Recall the rune's Proto-Norse name and sound"),
    NAME_TO_RUNE("Name -> Rune & Sound", "Visualize the carved stave and its phoneme"),
    SOUND_TO_RUNE("Sound -> Rune & Name", "Identify the Elder Futhark rune for this phoneme")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ElderFutharkFlashcardsScreen(
    roomRunes: List<RuneEntity>,
    flashcardProgress: List<FlashcardProgressEntity>,
    isProUnlocked: Boolean,
    onBack: () -> Unit,
    onRecordFlashcard: (String, ReviewQuality) -> Unit,
    onRequestPaywall: (PaywallFeature) -> Unit
) {
    BackHandler(onBack = onBack)

    val elderDeck = remember(roomRunes) {
        val fromRoom = roomRunes
            .filter { it.alphabet == RuneAlphabet.ELDER_FUTHARK.name }
            .map { r ->
                RuneItem(
                    id = r.id,
                    alphabet = RuneAlphabet.ELDER_FUTHARK,
                    glyph = r.glyph,
                    name = r.name,
                    soundValue = r.soundValue,
                    meaning = r.meaning,
                    aett = r.aett,
                    lore = r.lore
                )
            }
        fromRoom.ifEmpty { RunesData.elderFuthark }
    }

    val aettFilters = remember {
        listOf("All 24", "Freyr's Ætt", "Heimdallr's Ætt", "Týr's Ætt")
    }
    var selectedAett by remember { mutableStateOf("Freyr's Ætt") }
    var promptMode by remember { mutableStateOf(ElderFlashcardPromptMode.RUNE_TO_DETAILS) }
    var practiceAheadMode by remember { mutableStateOf(false) }
    var sessionReviewedCount by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    var aheadIndex by remember { mutableIntStateOf(0) }

    val activeDeck = remember(elderDeck, selectedAett, isProUnlocked) {
        when {
            !isProUnlocked -> elderDeck.filter { it.aett == "Freyr's Ætt" }
            selectedAett == "All 24" -> elderDeck
            else -> elderDeck.filter { it.aett == selectedAett }
        }
    }
    val filteredDeck = activeDeck
    val runeById = remember(filteredDeck) { filteredDeck.associateBy { it.id } }
    val progressMap = remember(flashcardProgress) { flashcardProgress.associateBy { it.runeId } }

    val (dueIds, upcomingIds) = remember(filteredDeck, progressMap, sessionReviewedCount) {
        SpacedRepetitionEngine.partitionDueIds(
            cardIds = filteredDeck.map { it.id },
            progressMap = progressMap,
            nowMillis = System.currentTimeMillis()
        )
    }

    val activeQueue = remember(dueIds, upcomingIds, practiceAheadMode) {
        if (dueIds.isNotEmpty()) {
            dueIds.mapNotNull { runeById[it] }
        } else if (practiceAheadMode) {
            upcomingIds.mapNotNull { runeById[it] }.ifEmpty { filteredDeck }
        } else {
            emptyList()
        }
    }

    val currentCard: RuneItem? = if (activeQueue.isNotEmpty()) {
        if (dueIds.isNotEmpty()) activeQueue.first() else activeQueue[aheadIndex % activeQueue.size]
    } else {
        null
    }

    val elderIds = remember(elderDeck) { elderDeck.map { it.id }.toSet() }
    val masteredCount = remember(progressMap, elderIds) {
        progressMap.values.count { elderIds.contains(it.runeId) && it.repetitions >= 2 }
    }
    val learningCount = remember(progressMap, elderIds) {
        progressMap.values.count { elderIds.contains(it.runeId) && it.repetitions == 1 }
    }

    val flipRotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "flashcardFlip"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = "Elder Futhark SRS",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("srs_back_button")
                ) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Mastery Header Card
            item {
                CarvedWoodCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ELDER FUTHARK MASTERY (SM-2)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "$masteredCount / ${elderDeck.size} Runes Mastered",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = "${dueIds.size} due · $learningCount learning",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { masteredCount.toFloat() / elderDeck.size.coerceAtLeast(1).toFloat() },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (sessionReviewedCount > 0) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Session reviews completed: $sessionReviewedCount",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            // Aett Filter Chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(aettFilters) { aett ->
                        val isProAett = !isProUnlocked && aett != "Freyr's Ætt"
                        FilterChip(
                            selected = if (!isProUnlocked) aett == "Freyr's Ætt" else selectedAett == aett,
                            onClick = {
                                if (isProAett) {
                                    onRequestPaywall(PaywallFeature.UNLIMITED_SRS)
                                } else {
                                    selectedAett = aett
                                    isFlipped = false
                                    aheadIndex = 0
                                }
                            },
                            trailingIcon = if (isProAett) {
                                { ProLockBadge(label = "Pro") }
                            } else {
                                null
                            },
                            label = { Text(aett) }
                        )
                    }
                }
            }

            // Prompt Direction Chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(ElderFlashcardPromptMode.entries) { mode ->
                        val isProMode = !isProUnlocked && mode != ElderFlashcardPromptMode.RUNE_TO_DETAILS
                        FilterChip(
                            selected = if (!isProUnlocked) mode == ElderFlashcardPromptMode.RUNE_TO_DETAILS else promptMode == mode,
                            onClick = {
                                if (isProMode) {
                                    onRequestPaywall(PaywallFeature.UNLIMITED_SRS)
                                } else {
                                    promptMode = mode
                                    isFlipped = false
                                }
                            },
                            trailingIcon = if (isProMode) {
                                { ProLockBadge(label = "Pro") }
                            } else {
                                null
                            },
                            label = { Text(mode.label) }
                        )
                    }
                }
            }

            // Active Flashcard or Deck Caught-Up Celebration
            item {
                if (currentCard == null) {
                    CarvedWoodCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = "Deck complete",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "All Due Elder Futhark Cards Reviewed!",
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Your SM-2 intervals are saved offline. Return tomorrow as intervals mature, or drill ahead now.",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = {
                                    practiceAheadMode = true
                                    aheadIndex = 0
                                    isFlipped = false
                                },
                                modifier = Modifier.testTag("practice_ahead_button")
                            ) {
                                Icon(Icons.Outlined.Style, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Practice Ahead (${filteredDeck.size} Cards)")
                            }
                        }
                    }
                } else {
                    val cardProgress = progressMap[currentCard.id]
                    val showingBack = flipRotation >= 90f

                    CarvedWoodCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                rotationY = flipRotation
                                cameraDistance = 12f * density
                            }
                            .testTag("srs_active_flashcard"),
                        borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.65f),
                        onClick = { isFlipped = !isFlipped }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer {
                                    if (showingBack) rotationY = 180f
                                }
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = currentCard.aett.uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Text(
                                        text = if (cardProgress == null) {
                                            "NEW CARD"
                                        } else {
                                            "REPS ${cardProgress.repetitions} · EASE ${"%.1f".format(cardProgress.easeFactor)}"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                if (!showingBack) {
                                    when (promptMode) {
                                        ElderFlashcardPromptMode.RUNE_TO_DETAILS -> {
                                            Text(
                                                text = currentCard.glyph,
                                                fontSize = 96.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        ElderFlashcardPromptMode.NAME_TO_RUNE -> {
                                            Text(
                                                text = currentCard.name,
                                                style = MaterialTheme.typography.displayLarge,
                                                color = MaterialTheme.colorScheme.primary,
                                                textAlign = TextAlign.Center
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = currentCard.meaning,
                                                style = MaterialTheme.typography.titleSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        ElderFlashcardPromptMode.SOUND_TO_RUNE -> {
                                            Text(
                                                text = currentCard.soundValue,
                                                style = MaterialTheme.typography.displayLarge,
                                                color = MaterialTheme.colorScheme.primary,
                                                textAlign = TextAlign.Center
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = "Hint: ${currentCard.meaning}",
                                                style = MaterialTheme.typography.titleSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = promptMode.hint,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = { isFlipped = true },
                                        modifier = Modifier.testTag("flip_flashcard_button")
                                    ) {
                                        Icon(Icons.Outlined.Flip, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Reveal Answer")
                                    }
                                } else {
                                    Text(
                                        text = currentCard.glyph,
                                        fontSize = 68.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "${currentCard.name} · ${currentCard.soundValue}",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = currentCard.meaning,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    KnotworkDivider(modifier = Modifier.padding(vertical = 10.dp))
                                    Text(
                                        text = currentCard.lore,
                                        style = MaterialTheme.typography.bodySmall,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    if (isFlipped) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "How well did you recall ${currentCard.name}?",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ReviewQuality.entries.forEach { quality ->
                                val nextLabel = remember(cardProgress, quality) {
                                    SpacedRepetitionEngine.previewIntervalLabel(cardProgress, quality)
                                }
                                OutlinedButton(
                                    onClick = {
                                        onRecordFlashcard(currentCard.id, quality)
                                        sessionReviewedCount += 1
                                        isFlipped = false
                                        aheadIndex += 1
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("srs_rate_${quality.name.lowercase()}"),
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 10.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = quality.label,
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                        Text(
                                            text = nextLabel,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 24-Rune Mastery Overview Strip
            item {
                CarvedWoodCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Deck Status (${filteredDeck.size} Runes)",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        filteredDeck.forEach { rune ->
                            val prog = progressMap[rune.id]
                            val bg = when {
                                prog != null && prog.repetitions >= 2 -> MaterialTheme.colorScheme.primaryContainer
                                prog != null && prog.repetitions >= 1 -> MaterialTheme.colorScheme.secondaryContainer
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = bg
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = rune.glyph,
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = rune.name.take(4),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (!isProUnlocked) {
                item {
                    LockedFeatureOverlay(
                        isLocked = true,
                        label = "Unlimited 24-Rune SM-2 Decks",
                        subtitle = "Unlock Heimdallr's & Týr's Ættir + Reverse Prompt Modes",
                        onUnlockClick = { onRequestPaywall(PaywallFeature.UNLIMITED_SRS) },
                        overlayTag = "locked_srs_deck_card"
                    ) {
                        CarvedWoodCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "HEIMDALLR'S ÆTT · TÝR'S ÆTT · PHONEME RECALL",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "16 Additional Elder Futhark Cards & Reverse Modes",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Practice all 24 Proto-Norse staves with Name-to-Rune and Sound-to-Rune recall.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
