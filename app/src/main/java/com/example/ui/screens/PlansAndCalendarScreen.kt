package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.ReadingPlanProgressEntity
import com.example.data.repository.IcelandicCalendarEngine
import com.example.data.seed.ReadingPlan
import com.example.data.seed.ReadingPlanDay
import com.example.data.seed.StudyAtlasData
import com.example.ui.components.CarvedWoodCard
import com.example.ui.components.KnotworkDivider
import com.example.ui.components.ProgressRing
import com.example.ui.components.SectionHeader
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlansAndCalendarScreen(
    initialTab: Int = 0,
    planProgress: List<ReadingPlanProgressEntity>,
    onTogglePlanDay: (String, Int, String) -> Unit,
    onOpenReader: (String, String?) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(initialTab) }
    LaunchedEffect(initialTab) { selectedTab = initialTab }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            SectionHeader(
                title = "Paths & Misseri",
                subtitle = "Reading Plans, Streaks & the Old Icelandic Calendar"
            )
        }
        PrimaryTabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Reading Plans") },
                modifier = Modifier.testTag("plans_tab_0")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Icelandic Calendar") },
                modifier = Modifier.testTag("plans_tab_1")
            )
        }

        if (selectedTab == 0) {
            ReadingPlansTab(
                planProgress = planProgress,
                onTogglePlanDay = onTogglePlanDay,
                onOpenReader = onOpenReader
            )
        } else {
            IcelandicCalendarTab(onOpenReader = onOpenReader)
        }
    }
}

@Composable
private fun ReadingPlansTab(
    planProgress: List<ReadingPlanProgressEntity>,
    onTogglePlanDay: (String, Int, String) -> Unit,
    onOpenReader: (String, String?) -> Unit
) {
    var expandedPlanId by remember { mutableStateOf<String?>(StudyAtlasData.readingPlans.first().id) }
    var reflectionTarget by remember { mutableStateOf<Pair<ReadingPlan, ReadingPlanDay>?>(null) }
    var reflectionInput by remember { mutableStateOf("") }

    val progressMap = remember(planProgress) { planProgress.associateBy { it.planId } }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(StudyAtlasData.readingPlans, key = { it.id }) { plan ->
            val prog = progressMap[plan.id]
            val completedSet = prog?.completedDaysCsv
                ?.split(",")
                ?.mapNotNull { it.trim().toIntOrNull() }
                ?.toSet() ?: emptySet()
            val total = plan.days.size.coerceAtLeast(1)
            val isExpanded = expandedPlanId == plan.id

            CarvedWoodCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = { expandedPlanId = if (isExpanded) null else plan.id }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${plan.categoryBadge.uppercase()} · ${plan.totalDaysLabel}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(plan.title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                        Text(plan.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if ((prog?.streakDays ?: 0) > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Active streak: ${prog!!.streakDays} day(s)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    ProgressRing(
                        progress = completedSet.size.toFloat() / total.toFloat(),
                        size = 54.dp,
                        label = "${completedSet.size}/$total"
                    )
                }

                if (isExpanded) {
                    KnotworkDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        plan.days.forEach { day ->
                            val done = completedSet.contains(day.dayNumber)
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = done,
                                        onCheckedChange = { checked ->
                                            if (checked) {
                                                reflectionInput = ""
                                                reflectionTarget = plan to day
                                            } else {
                                                onTogglePlanDay(plan.id, day.dayNumber, "")
                                            }
                                        }
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Day ${day.dayNumber}: ${day.title}", style = MaterialTheme.typography.titleSmall)
                                        Text("Prompt: ${day.reflectionPrompt}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    TextButton(onClick = { onOpenReader(day.workId, day.passageId) }) {
                                        Text("Read")
                                    }
                                }
                            }
                        }
                        if (!prog?.reflectionsSummary.isNullOrBlank()) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Your Reflections", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(prog!!.reflectionsSummary, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (reflectionTarget != null) {
        val (plan, day) = reflectionTarget!!
        AlertDialog(
            onDismissRequest = { reflectionTarget = null },
            title = { Text("Day ${day.dayNumber} Reflection", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(day.reflectionPrompt, style = MaterialTheme.typography.bodyMedium)
                    OutlinedTextField(
                        value = reflectionInput,
                        onValueChange = { reflectionInput = it },
                        label = { Text("Optional journal reflection") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    onTogglePlanDay(plan.id, day.dayNumber, reflectionInput)
                    reflectionTarget = null
                }) { Text("Complete Reading") }
            },
            dismissButton = {
                TextButton(onClick = { reflectionTarget = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun IcelandicCalendarTab(onOpenReader: (String, String?) -> Unit) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val misseri = remember(selectedDate) { IcelandicCalendarEngine.toIcelandicDate(selectedDate) }
    val formatter = remember { DateTimeFormatter.ofPattern("EEE, d MMM yyyy") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
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
                    IconButton(onClick = { selectedDate = selectedDate.minusDays(15) }) {
                        Icon(Icons.Outlined.ChevronLeft, contentDescription = "Previous fortnight")
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(selectedDate.format(formatter), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
                        Text("${misseri.name} · Day ${misseri.dayOfMonth}", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
                        Text("\"${misseri.meaning}\" · Week ${misseri.weekNumberInSeason} of ${misseri.halfYear}", style = MaterialTheme.typography.bodySmall)
                    }
                    IconButton(onClick = { selectedDate = selectedDate.plusDays(15) }) {
                        Icon(Icons.Outlined.ChevronRight, contentDescription = "Next fortnight")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(misseri.seasonalLore, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (selectedDate != LocalDate.now()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedButton(onClick = { selectedDate = LocalDate.now() }) {
                        Text("Return to Today")
                    }
                }
            }
        }

        item {
            SectionHeader(
                title = "Seasonal Observances",
                subtitle = "Historical calendar festivals with paired primary readings"
            )
        }

        items(IcelandicCalendarEngine.observances, key = { it.id }) { obs ->
            CarvedWoodCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(obs.name, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    Text(obs.icelandicMonth, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
                }
                Text("${obs.oldNorseName} · ${obs.approxGregorian}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(6.dp))
                Text(obs.description, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(obs.attestationNote, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.height(10.dp))
                Button(onClick = { onOpenReader(obs.linkedWorkId, obs.linkedPassageId) }) {
                    Text("Read Paired Text")
                }
            }
        }
    }
}
