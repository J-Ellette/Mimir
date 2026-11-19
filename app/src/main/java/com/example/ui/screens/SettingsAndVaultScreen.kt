package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.data.local.DailyReadingProgress
import com.example.data.local.PaywallFeature
import com.example.data.local.ProEntitlement
import com.example.data.local.UserSettings
import com.example.data.model.UserAnnotationEntity
import com.example.ui.components.CarvedWoodCard
import com.example.ui.components.LockedFeatureOverlay
import com.example.ui.components.ProLockBadge
import com.example.ui.components.ProgressRing
import com.example.ui.theme.MimirThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsAndVaultScreen(
    settings: UserSettings,
    proEntitlement: ProEntitlement,
    dailyReadingProgress: DailyReadingProgress,
    annotations: List<UserAnnotationEntity>,
    vaultUnlocked: Boolean,
    canUseBiometrics: Boolean,
    onBack: () -> Unit,
    onToggleDarkMode: () -> Unit,
    onSelectTheme: (MimirThemeMode) -> Unit,
    onChangeFontScale: (Float) -> Unit,
    onToggleMorningReminder: (Boolean) -> Unit,
    onToggleBiometricVault: (Boolean) -> Unit,
    onUnlockVault: () -> Unit,
    onUpdateHandle: (String) -> Unit,
    onLogDailyReading: () -> Unit,
    onSetDailyGoal: (Int) -> Unit,
    onExportBackup: (Uri, String) -> Unit,
    onImportBackup: (Uri, String) -> Unit,
    onOpenPaywall: (PaywallFeature) -> Unit,
    onRevokeProPreview: () -> Unit
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    var handleDraft by remember(settings.displayHandle) { mutableStateOf(settings.displayHandle) }
    var passphraseDialogMode by remember { mutableStateOf<String?>(null) } // "EXPORT" or "IMPORT"
    var passphrase by remember { mutableStateOf("") }
    var pendingExportPassphrase by remember { mutableStateOf("") }
    var pendingImportPassphrase by remember { mutableStateOf("") }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) onToggleMorningReminder(true)
    }

    val createBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null && pendingExportPassphrase.length >= 4) {
            onExportBackup(uri, pendingExportPassphrase)
        }
    }

    val openBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null && pendingImportPassphrase.length >= 4) {
            onImportBackup(uri, pendingImportPassphrase)
        }
    }

    val vaultNotes = remember(annotations) {
        annotations.filter { it.isPrivateVault && it.noteText.isNotBlank() }
    }

    val currentStreak = dailyReadingProgress.effectiveCurrentStreak()
    val todayVerses = dailyReadingProgress.effectiveTodayVerses()
    val dailyGoal = dailyReadingProgress.dailyGoalVerses.coerceAtLeast(1)
    val recentWeek = remember(dailyReadingProgress) { dailyReadingProgress.recentSevenDaysStatus() }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Profile, Streaks & Vault", style = MaterialTheme.typography.titleLarge) },
            navigationIcon = {
                IconButton(onClick = onBack, modifier = Modifier.testTag("settings_back")) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Mímir Membership Tier Card
            item {
                CarvedWoodCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = if (proEntitlement.isProUnlocked) {
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.65f)
                    } else {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = Icons.Outlined.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (proEntitlement.isProUnlocked) {
                                        "Mímir Pro · ${proEntitlement.activePlanTier.title}"
                                    } else {
                                        "Free Core Tier"
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = if (proEntitlement.isProUnlocked) {
                                        "All commentary packs, audio pronunciation, live OCR, vault & 24-rune SM-2 unlocked"
                                    } else {
                                        "Public-domain Eddas & Sagas, lexicon tap lookup, Unit 1, and name runes included"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    if (proEntitlement.isProUnlocked) {
                        OutlinedButton(
                            onClick = onRevokeProPreview,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("profile_revoke_pro_button")
                        ) {
                            Text("Switch to Free Core Preview")
                        }
                    } else {
                        Button(
                            onClick = { onOpenPaywall(PaywallFeature.GENERAL) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("profile_upgrade_pro_button")
                        ) {
                            Text("Upgrade to Mímir Pro")
                        }
                    }
                }
            }

            // Reader Profile & DataStore Daily Streak Card
            item {
                CarvedWoodCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.LocalFireDepartment,
                                    contentDescription = "Reading streak",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Reader Profile · ${settings.displayHandle}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Current Streak: $currentStreak day${if (currentStreak == 1) "" else "s"}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.testTag("profile_streak_count")
                            )
                            Text(
                                text = "Longest: ${dailyReadingProgress.longestStreakDays} days · Total active days: ${dailyReadingProgress.totalDaysRead}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        ProgressRing(
                            progress = (todayVerses.toFloat() / dailyGoal.toFloat()).coerceIn(0f, 1f),
                            size = 56.dp,
                            strokeWidth = 6.dp,
                            label = "$todayVerses/$dailyGoal"
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Last 7 Days Cadence",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        recentWeek.forEach { active ->
                            Surface(
                                shape = CircleShape,
                                color = if (active) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                },
                                modifier = Modifier.size(26.dp)
                            ) {
                                // Empty circle indicator rendered cleanly via Surface color
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Daily Stanza Goal",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(1, 3, 5, 10).forEach { goalOption ->
                            FilterChip(
                                selected = dailyGoal == goalOption,
                                onClick = { onSetDailyGoal(goalOption) },
                                label = { Text("$goalOption/day") }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onLogDailyReading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_log_reading_button")
                    ) {
                        Text("Log +1 Stanza Read Today")
                    }
                }
            }

            // Themes & Dark Mode Toggle
            item {
                CarvedWoodCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Dark Mode", style = MaterialTheme.typography.titleMedium)
                            Text("Dark mode by default; toggle cleanly between nocturnal and paper themes", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = settings.themeMode.isDark,
                            onCheckedChange = { onToggleDarkMode() },
                            modifier = Modifier.testTag("settings_dark_mode_switch")
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Carved & Manuscript Themes", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(6.dp))
                    MimirThemeMode.entries.forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(mode.displayName, style = MaterialTheme.typography.titleSmall)
                                Text(mode.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            FilterChip(
                                selected = settings.themeMode == mode,
                                onClick = { onSelectTheme(mode) },
                                label = { Text(if (settings.themeMode == mode) "Active" else "Use") }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Serif Body Font Scale: ${(settings.readerFontScale * 100).toInt()}%", style = MaterialTheme.typography.labelLarge)
                    Slider(
                        value = settings.readerFontScale,
                        onValueChange = onChangeFontScale,
                        valueRange = 0.85f..1.45f
                    )
                }
            }

            // Biometric Private Vault
            item {
                LockedFeatureOverlay(
                    isLocked = !proEntitlement.isProUnlocked,
                    label = "Biometric Private Vault",
                    subtitle = "Pro study tool · Tap to unlock",
                    onUnlockClick = { onOpenPaywall(PaywallFeature.BIOMETRIC_VAULT) },
                    overlayTag = "locked_biometric_vault"
                ) {
                    CarvedWoodCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Biometric Private Vault", style = MaterialTheme.typography.titleMedium)
                                    if (!proEntitlement.isProUnlocked) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        ProLockBadge(label = "Pro")
                                    }
                                }
                                Text(
                                    text = if (canUseBiometrics) "Protect private study notes & digitized manuscripts with device biometrics" else "Device biometrics unavailable; PIN/credential fallback used when supported",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = settings.biometricVaultEnabled && proEntitlement.isProUnlocked,
                                onCheckedChange = { enable ->
                                    if (enable && !proEntitlement.isProUnlocked) {
                                        onOpenPaywall(PaywallFeature.BIOMETRIC_VAULT)
                                    } else {
                                        onToggleBiometricVault(enable)
                                    }
                                },
                                modifier = Modifier.testTag("biometric_vault_switch")
                            )
                        }
                        if (!proEntitlement.isProUnlocked) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = { onOpenPaywall(PaywallFeature.BIOMETRIC_VAULT) },
                                modifier = Modifier.testTag("vault_pro_unlock_button")
                            ) {
                                Text("Unlock Vault with Mímir Pro")
                            }
                        } else if (settings.biometricVaultEnabled && !vaultUnlocked) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(onClick = onUnlockVault, modifier = Modifier.testTag("unlock_vault_button")) {
                                Text("Authenticate to View Vault (${vaultNotes.size} notes)")
                            }
                        } else if (vaultNotes.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Unlocked Private Notes (${vaultNotes.size})", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
                            vaultNotes.forEach { n ->
                                Text("• ${n.passageId}: ${n.noteText}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            // Encrypted Local-Only Backup Sync
            item {
                LockedFeatureOverlay(
                    isLocked = !proEntitlement.isProUnlocked,
                    label = "Encrypted Backup Sync",
                    subtitle = "AES-256 export & restore · Mímir Pro",
                    onUnlockClick = { onOpenPaywall(PaywallFeature.BIOMETRIC_VAULT) },
                    overlayTag = "locked_encrypted_sync"
                ) {
                    CarvedWoodCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Encrypted Local-Only Sync", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            if (!proEntitlement.isProUnlocked) {
                                ProLockBadge(label = "Pro")
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Sync settings, bookmarks, highlights, reading streaks, SRS flashcards, and private vault notes across devices without cloud servers using AES-256-GCM encrypted .mimir files.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (!proEntitlement.isProUnlocked) {
                                        onOpenPaywall(PaywallFeature.BIOMETRIC_VAULT)
                                    } else {
                                        passphrase = ""
                                        passphraseDialogMode = "EXPORT"
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("export_encrypted_backup")
                            ) {
                                Icon(Icons.Outlined.Upload, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Export")
                            }
                            OutlinedButton(
                                onClick = {
                                    if (!proEntitlement.isProUnlocked) {
                                        onOpenPaywall(PaywallFeature.BIOMETRIC_VAULT)
                                    } else {
                                        passphrase = ""
                                        passphraseDialogMode = "IMPORT"
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("import_encrypted_backup")
                            ) {
                                Icon(Icons.Outlined.Download, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Restore")
                            }
                        }
                    }
                }
            }

            // Morning Notification & Reading Circle Profile
            item {
                CarvedWoodCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.NotificationsActive, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Morning Stanza Reminder", style = MaterialTheme.typography.titleMedium)
                            }
                            Text("Daily Hávamál or Saga line notification", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = settings.morningReminderEnabled,
                            onCheckedChange = { enable ->
                                if (!enable) {
                                    onToggleMorningReminder(false)
                                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    val granted = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.POST_NOTIFICATIONS
                                    ) == PackageManager.PERMISSION_GRANTED
                                    if (granted) {
                                        onToggleMorningReminder(true)
                                    } else {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                } else {
                                    onToggleMorningReminder(true)
                                }
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = handleDraft,
                        onValueChange = { handleDraft = it },
                        label = { Text("Reading Circle Pen Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedButton(onClick = { onUpdateHandle(handleDraft) }) {
                        Text("Save Pen Name")
                    }
                }
            }
        }
    }

    if (passphraseDialogMode != null) {
        val isExport = passphraseDialogMode == "EXPORT"
        AlertDialog(
            onDismissRequest = { passphraseDialogMode = null },
            title = { Text(if (isExport) "Encrypt Local Backup" else "Decrypt Local Backup", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Enter a passphrase (min 4 characters). Mímir uses PBKDF2-HMAC-SHA256 + AES-256-GCM locally on-device.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        value = passphrase,
                        onValueChange = { passphrase = it },
                        label = { Text("Passphrase") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    enabled = passphrase.length >= 4,
                    onClick = {
                        val pass = passphrase
                        passphraseDialogMode = null
                        if (isExport) {
                            pendingExportPassphrase = pass
                            createBackupLauncher.launch("mimir_backup.mimir")
                        } else {
                            pendingImportPassphrase = pass
                            openBackupLauncher.launch(arrayOf("*/*"))
                        }
                    }
                ) { Text(if (isExport) "Choose Save Location" else "Choose Backup File") }
            },
            dismissButton = {
                TextButton(onClick = { passphraseDialogMode = null }) { Text("Cancel") }
            }
        )
    }
}
