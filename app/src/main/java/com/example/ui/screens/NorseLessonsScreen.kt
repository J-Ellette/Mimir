package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.example.data.local.PaywallFeature
import com.example.data.model.LessonProgressEntity
import com.example.data.seed.NorseGrammarLesson
import com.example.data.seed.NorseLessonsData
import com.example.ui.components.CarvedWoodCard
import com.example.ui.components.KnotworkDivider
import com.example.ui.components.LockedFeatureOverlay
import com.example.ui.components.ProLockBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NorseLessonsScreen(
    lessonProgress: List<LessonProgressEntity>,
    isProUnlocked: Boolean,
    onBack: () -> Unit,
    onSaveLessonScore: (String, Int, Int) -> Unit,
    onSpeakPhrase: (String, Boolean) -> Unit,
    onOpenPassage: (String) -> Unit,
    onRequestPaywall: (PaywallFeature) -> Unit
) {
    BackHandler(onBack = onBack)

    var selectedTab by remember { mutableIntStateOf(0) }
    var activeLesson by remember { mutableStateOf<NorseGrammarLesson?>(null) }
    val progressMap = remember(lessonProgress) { lessonProgress.associateBy { it.lessonId } }
    val totalXp = remember(lessonProgress) { lessonProgress.sumOf { it.xpEarned } }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Column {
                    Text("Learn Old Norse", style = MaterialTheme.typography.titleLarge)
                    Text("$totalXp XP earned · Built on Eddic lines", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
            },
            navigationIcon = {
                IconButton(onClick = {
                    if (activeLesson != null) activeLesson = null else onBack()
                }) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                }
            }
        )

        if (activeLesson != null) {
            GrammarLessonRunner(
                lesson = activeLesson!!,
                onFinish = { score, total ->
                    onSaveLessonScore(activeLesson!!.id, score, total)
                    activeLesson = null
                }
            )
        } else {
            PrimaryTabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Grammar Path") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Pronunciation & IPA") })
            }

            if (selectedTab == 0) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(NorseLessonsData.lessons, key = { it.id }) { lesson ->
                        val prog = progressMap[lesson.id]
                        val locked = lesson.unitNumber > 1 && !isProUnlocked
                        LockedFeatureOverlay(
                            isLocked = locked,
                            label = "Unit ${lesson.unitNumber}: ${lesson.title}",
                            subtitle = "Advanced Grammar · Tap to unlock Mímir Pro",
                            onUnlockClick = { onRequestPaywall(PaywallFeature.NORSE_LESSONS_PRO) },
                            overlayTag = "locked_lesson_overlay_${lesson.id}"
                        ) {
                            CarvedWoodCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("lesson_${lesson.id}"),
                                onClick = {
                                    if (locked) {
                                        onRequestPaywall(PaywallFeature.NORSE_LESSONS_PRO)
                                    } else {
                                        activeLesson = lesson
                                    }
                                }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "UNIT ${lesson.unitNumber}" + if (lesson.unitNumber == 1) " · FREE CORE" else "",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    if (locked) {
                                        ProLockBadge()
                                    } else if (prog?.isCompleted == true) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Outlined.CheckCircle,
                                                contentDescription = "Completed",
                                                tint = MaterialTheme.colorScheme.secondary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("${prog.xpEarned} XP", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(lesson.title, style = MaterialTheme.typography.titleLarge)
                                Text(lesson.subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(lesson.grammarOverview, style = MaterialTheme.typography.bodySmall)
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = {
                                        if (locked) {
                                            onRequestPaywall(PaywallFeature.NORSE_LESSONS_PRO)
                                        } else {
                                            activeLesson = lesson
                                        }
                                    }) {
                                        if (locked) {
                                            Icon(Icons.Outlined.Lock, contentDescription = "Locked", modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Unlock Unit")
                                        } else {
                                            Text(if (prog?.isCompleted == true) "Practice Again" else "Start Lesson")
                                        }
                                    }
                                    OutlinedButton(onClick = { onOpenPassage(lesson.linkedPassageId) }) {
                                        Text("Source Text")
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!isProUnlocked) {
                        item {
                            CarvedWoodCard(
                                modifier = Modifier.fillMaxWidth(),
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.45f),
                                onClick = { onRequestPaywall(PaywallFeature.AUDIO_PRONUNCIATION) }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Spoken Audio Pronunciation", style = MaterialTheme.typography.titleSmall)
                                        Text("IPA guides are free; tap to unlock Old Norse & Icelandic audio playback.", style = MaterialTheme.typography.bodySmall)
                                    }
                                    ProLockBadge()
                                }
                            }
                        }
                    }
                    items(NorseLessonsData.pronunciationBank, key = { it.id }) { phrase ->
                        CarvedWoodCard(modifier = Modifier.fillMaxWidth()) {
                            Text(phrase.oldNorseText, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                            Text(phrase.englishMeaning, style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic), color = MaterialTheme.colorScheme.onSurfaceVariant)
                            KnotworkDivider(modifier = Modifier.padding(vertical = 8.dp))
                            Text("Reconstructed Old Norse: ${phrase.oldNorseIpa}", style = MaterialTheme.typography.bodySmall)
                            Text("Modern Icelandic: ${phrase.modernIcelandicIpa}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(phrase.soundShiftNote, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilledTonalButton(
                                    onClick = {
                                        if (isProUnlocked) {
                                            onSpeakPhrase(phrase.oldNorseText, false)
                                        } else {
                                            onRequestPaywall(PaywallFeature.AUDIO_PRONUNCIATION)
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (isProUnlocked) Icons.Outlined.VolumeUp else Icons.Outlined.Lock,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Old Norse")
                                }
                                OutlinedButton(
                                    onClick = {
                                        if (isProUnlocked) {
                                            onSpeakPhrase(phrase.oldNorseText, true)
                                        } else {
                                            onRequestPaywall(PaywallFeature.AUDIO_PRONUNCIATION)
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (isProUnlocked) Icons.Outlined.VolumeUp else Icons.Outlined.Lock,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Icelandic")
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
private fun GrammarLessonRunner(
    lesson: NorseGrammarLesson,
    onFinish: (Int, Int) -> Unit
) {
    var qIndex by remember { mutableIntStateOf(0) }
    var selectedChoice by remember { mutableStateOf<Int?>(null) }
    var score by remember { mutableIntStateOf(0) }
    val challenge = lesson.challenges[qIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Question ${qIndex + 1} of ${lesson.challenges.size}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        CarvedWoodCard(modifier = Modifier.fillMaxWidth()) {
            Text(challenge.sourceLine, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
            Text("\"${challenge.sourceTranslation}\"", style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(challenge.prompt, style = MaterialTheme.typography.titleMedium)

        challenge.options.forEachIndexed { idx, opt ->
            val answered = selectedChoice != null
            val isCorrect = idx == challenge.correctIndex
            val isSelected = selectedChoice == idx
            Surface(
                onClick = {
                    if (!answered) {
                        selectedChoice = idx
                        if (isCorrect) score++
                    }
                },
                shape = RoundedCornerShape(12.dp),
                color = when {
                    !answered -> MaterialTheme.colorScheme.surface
                    isCorrect -> MaterialTheme.colorScheme.secondaryContainer
                    isSelected -> MaterialTheme.colorScheme.errorContainer
                    else -> MaterialTheme.colorScheme.surface
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = opt,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        if (selectedChoice != null) {
            CarvedWoodCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            ) {
                Text(
                    text = if (selectedChoice == challenge.correctIndex) "Correct!" else "Philological Note",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(challenge.explanation, style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(10.dp))
                Button(onClick = {
                    if (qIndex < lesson.challenges.lastIndex) {
                        qIndex++
                        selectedChoice = null
                    } else {
                        onFinish(score, lesson.challenges.size)
                    }
                }) {
                    Text(if (qIndex < lesson.challenges.lastIndex) "Next Question" else "Complete Unit")
                }
            }
        }
    }
}
