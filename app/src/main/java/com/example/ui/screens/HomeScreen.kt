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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.AddToHomeScreen
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Park
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.DailyReadingProgress
import com.example.data.local.PaywallFeature
import com.example.data.local.UserSettings
import com.example.data.model.BookEntity
import com.example.data.model.FlashcardProgressEntity
import com.example.data.model.ReadingPlanProgressEntity
import com.example.data.model.VerseEntity
import com.example.data.repository.IcelandicMonthInfo
import com.example.data.seed.RunesData
import com.example.data.seed.StudyAtlasData
import com.example.ui.components.CarvedWoodCard
import com.example.ui.components.DarkModeQuickToggle
import com.example.ui.components.KnotworkDivider
import com.example.ui.components.ProgressRing
import com.example.util.RunestoneShareHelper
import com.example.widget.DailyVerseWidgetUpdater

@Composable
fun HomeScreen(
    settings: UserSettings,
    isProUnlocked: Boolean,
    dailyReadingProgress: DailyReadingProgress,
    dailyVerse: VerseEntity?,
    books: List<BookEntity>,
    planProgress: List<ReadingPlanProgressEntity>,
    flashcardProgress: List<FlashcardProgressEntity>,
    todayIcelandic: IcelandicMonthInfo,
    onToggleDarkMode: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenReader: (String, String?) -> Unit,
    onOpenNorseLessons: () -> Unit,
    onOpenOcrScanner: () -> Unit,
    onOpenYggdrasil: () -> Unit,
    onOpenElderFutharkTransliterator: () -> Unit,
    onOpenElderFutharkFlashcards: () -> Unit,
    onOpenCalendar: () -> Unit,
    onOpenPlans: () -> Unit,
    onOpenPaywall: (PaywallFeature) -> Unit,
    onLogDailyReading: () -> Unit,
    onSpeakOldNorse: (String) -> Unit,
    onShowMessage: (String) -> Unit
) {
    val context = LocalContext.current
    var showShareDialog by remember { mutableStateOf(false) }
    var knotworkCardStyle by remember { mutableStateOf(false) }

    val dailyBookTitle = remember(dailyVerse, books) {
        books.firstOrNull { it.id == dailyVerse?.bookId }?.title ?: "Hávamál"
    }

    val primaryPlan = StudyAtlasData.readingPlans.first()
    val primaryProgress = planProgress.firstOrNull { it.planId == primaryPlan.id }
    val planCompletedCount = primaryProgress?.completedDaysCsv
        ?.split(",")
        ?.count { it.trim().isNotEmpty() } ?: 0
    val currentStreak = maxOf(
        dailyReadingProgress.effectiveCurrentStreak(),
        planProgress.maxOfOrNull { it.streakDays } ?: 0
    )
    val elderMasteredCount = remember(flashcardProgress) {
        val elderIds = RunesData.elderFuthark.map { it.id }.toSet()
        flashcardProgress.count { it.runeId in elderIds && it.repetitions > 0 }
    }
    val todayVerses = dailyReadingProgress.effectiveTodayVerses()
    val dailyGoal = dailyReadingProgress.dailyGoalVerses.coerceAtLeast(1)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(216.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_mimirs_well),
                    contentDescription = "Mímir's Well beneath the roots of Yggdrasil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.25f),
                                    MaterialTheme.colorScheme.background.copy(alpha = 0.92f)
                                )
                            )
                        )
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = {
                            if (isProUnlocked) onOpenSettings() else onOpenPaywall(PaywallFeature.GENERAL)
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
                        modifier = Modifier.testTag("home_tier_badge")
                    ) {
                        Text(
                            text = if (isProUnlocked) "MÍMIR PRO · ALL UNLOCKED" else "FREE CORE · GO PRO",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        DarkModeQuickToggle(
                            isDark = settings.themeMode.isDark,
                            onToggle = onToggleDarkMode
                        )
                        IconButton(
                            onClick = onOpenSettings,
                            modifier = Modifier.testTag("open_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = "Settings, Themes & Encrypted Backup",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "Mímisbrunnr",
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Eddas, Sagas, Runes & Old Norse Study",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Daily Verse Card
        if (dailyVerse != null) {
            item {
                CarvedWoodCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "DAILY STANZA",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "$dailyBookTitle · ${dailyVerse.numberLabel}",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Row {
                            IconButton(
                                onClick = { onSpeakOldNorse(dailyVerse.oldNorseText) },
                                modifier = Modifier.testTag("daily_verse_listen")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.VolumeUp,
                                    contentDescription = "Listen to Old Norse pronunciation",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                            IconButton(
                                onClick = {
                                    val requested = DailyVerseWidgetUpdater.requestPinWidget(context)
                                    onShowMessage(
                                        if (requested) {
                                            "Confirm adding Mímir Daily Verse widget to your home screen"
                                        } else {
                                            "Open your launcher's Widgets drawer and select Mímir Daily Verse"
                                        }
                                    )
                                },
                                modifier = Modifier.testTag("pin_daily_verse_widget")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.AddToHomeScreen,
                                    contentDescription = "Pin Daily Verse Glance widget",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                            IconButton(
                                onClick = { showShareDialog = true },
                                modifier = Modifier.testTag("daily_verse_share")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Share,
                                    contentDescription = "Share as Runestone Card",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = dailyVerse.oldNorseText,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    KnotworkDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        text = dailyVerse.englishText,
                        style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilledTonalButton(
                            onClick = { onOpenReader(dailyVerse.bookId, dailyVerse.id) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("daily_verse_open_reader")
                        ) {
                            Icon(Icons.AutoMirrored.Outlined.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Read Parallel")
                        }
                        OutlinedButton(
                            onClick = { showShareDialog = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Outlined.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Stone Card")
                        }
                    }
                }
            }
        }

        // Old Icelandic Calendar Strip + Reading Plan Progress
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CarvedWoodCard(
                    modifier = Modifier.weight(1f),
                    onClick = onOpenCalendar
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "MISSIERI",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${todayIcelandic.name} ${todayIcelandic.dayOfMonth}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = todayIcelandic.halfYear,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                CarvedWoodCard(
                    modifier = Modifier.weight(1f),
                    onClick = onOpenPlans
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "DAILY STREAK",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$currentStreak day${if (currentStreak == 1) "" else "s"}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.testTag("home_streak_count")
                            )
                            Text(
                                text = "Best ${maxOf(dailyReadingProgress.longestStreakDays, currentStreak)}d · Plan $planCompletedCount/${primaryPlan.days.size}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                        ProgressRing(
                            progress = (todayVerses.toFloat() / dailyGoal.toFloat()).coerceIn(0f, 1f),
                            size = 48.dp,
                            strokeWidth = 5.dp,
                            label = "$todayVerses/$dailyGoal"
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    FilledTonalButton(
                        onClick = onLogDailyReading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("log_daily_reading_button")
                    ) {
                        Text(
                            if (todayVerses >= dailyGoal) "Goal Met (+1)" else "Log +1 Stanza"
                        )
                    }
                }
            }
        }

        // Quick Study Modules
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Explore Mímir",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionTile(
                        title = "Old Norse\nLessons",
                        subtitle = "Grammar & IPA Audio",
                        icon = Icons.Outlined.School,
                        onClick = onOpenNorseLessons,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_norse_lessons")
                    )
                    QuickActionTile(
                        title = "Elder Futhark\nName Runes",
                        subtitle = "Phonetic Transliteration",
                        icon = Icons.Outlined.Translate,
                        onClick = onOpenElderFutharkTransliterator,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_elder_futhark")
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionTile(
                        title = "Yggdrasil\nNine Worlds",
                        subtitle = "Interactive Cosmos",
                        icon = Icons.Outlined.Park,
                        onClick = onOpenYggdrasil,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_yggdrasil")
                    )
                    QuickActionTile(
                        title = "Manuscript\nOCR Scan",
                        subtitle = if (isProUnlocked) "Offline Camera Scanner" else "Sample Free · Camera Pro",
                        icon = Icons.Outlined.DocumentScanner,
                        isLocked = !isProUnlocked,
                        onClick = onOpenOcrScanner,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_ocr_scanner")
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionTile(
                        title = "Elder Futhark\nFlashcards",
                        subtitle = "$elderMasteredCount/24 Runes · SM-2",
                        icon = Icons.Outlined.Style,
                        onClick = onOpenElderFutharkFlashcards,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_elder_flashcards")
                    )
                    QuickActionTile(
                        title = "Profile &\nEncrypted Vault",
                        subtitle = "$currentStreak-day streak · AES-256",
                        icon = Icons.Outlined.Lock,
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_vault_sync")
                    )
                }
            }
        }

        // Scholarly & Anti-Extremism Charter
        item {
            CarvedWoodCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
            ) {
                Text(
                    text = "Scholarly & Ethical Charter",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Mímir welcomes both academic philologists and devotional readers. Every commentary separates primary manuscript attestation from later interpretation and actively rejects extremist misuse of runes and Nordic heritage.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (showShareDialog && dailyVerse != null) {
        AlertDialog(
            onDismissRequest = { showShareDialog = false },
            title = { Text("Share Runestone Card", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = !knotworkCardStyle,
                            onClick = { knotworkCardStyle = false },
                            label = { Text("Runestone Slate") }
                        )
                        FilterChip(
                            selected = knotworkCardStyle,
                            onClick = { knotworkCardStyle = true },
                            label = { Text("Knotwork Teal") }
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                            .clip(RoundedCornerShape(14.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_runestone_card),
                            contentDescription = "Runestone card background",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    if (knotworkCardStyle) Color(0xBF131C1E) else Color(0xBA161311)
                                )
                                .padding(14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$dailyBookTitle · ${dailyVerse.numberLabel}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFD4A853)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = dailyVerse.oldNorseText.lines().take(3).joinToString("\n"),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFFF3EDE2),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = dailyVerse.englishText.lines().take(2).joinToString(" "),
                                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                                    color = Color(0xFFB7D6D8),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showShareDialog = false
                        RunestoneShareHelper.shareRunestoneCard(
                            context = context,
                            title = dailyBookTitle,
                            oldNorse = dailyVerse.oldNorseText,
                            english = dailyVerse.englishText,
                            citation = "$dailyBookTitle · ${dailyVerse.numberLabel}",
                            knotworkStyle = knotworkCardStyle
                        )
                    },
                    modifier = Modifier.testTag("confirm_share_runestone")
                ) {
                    Text("Share PNG Card")
                }
            },
            dismissButton = {
                TextButton(onClick = { showShareDialog = false }) { Text("Close") }
            }
        )
    }
}

@Composable
private fun QuickActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLocked: Boolean = false
) {
    CarvedWoodCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(26.dp)
            )
            if (isLocked) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = "Includes Mímir Pro features",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
