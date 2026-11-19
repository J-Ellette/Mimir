package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.R
import com.example.data.local.PaywallFeature
import com.example.data.model.DigitizedManuscriptEntity
import com.example.data.model.GlossaryCategory
import com.example.data.model.GlossaryEntryEntity
import com.example.data.seed.NineWorldNode
import com.example.data.seed.StudyAtlasData
import com.example.service.ManuscriptOcrService
import com.example.ui.components.CarvedWoodCard
import com.example.ui.components.KnotworkDivider
import com.example.ui.components.LockedFeatureOverlay
import com.example.ui.components.ProLockBadge
import com.example.ui.components.SectionHeader
import java.io.File
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyToolsScreen(
    initialTab: Int = 0,
    glossary: List<GlossaryEntryEntity>,
    manuscripts: List<DigitizedManuscriptEntity>,
    ocrProcessing: Boolean,
    vaultUnlocked: Boolean,
    biometricVaultEnabled: Boolean,
    isProUnlocked: Boolean,
    onUnlockVaultRequest: () -> Unit,
    onProcessOcrUri: (Uri, String, Boolean, (String, String) -> Unit) -> Unit,
    onProcessCameraFrame: (ImageProxy, String, Boolean, (String, String) -> Unit) -> Unit,
    onSaveManualManuscript: (String, String, Boolean) -> Unit,
    onDeleteManuscript: (Long) -> Unit,
    onOpenPassage: (String) -> Unit,
    onShowMessage: (String) -> Unit,
    onRequestPaywall: (PaywallFeature) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(initialTab.coerceIn(0, 3)) }
    LaunchedEffect(initialTab) { selectedTab = initialTab.coerceIn(0, 3) }
    val tabs = listOf("Glossary", "Yggdrasil", "Family Trees", "OCR Scan")

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            SectionHeader(
                title = "Study Atlas & Tools",
                subtitle = "Glossary · Nine Worlds · Genealogies · Manuscript OCR"
            )
        }
        PrimaryScrollableTabRow(selectedTabIndex = selectedTab, edgePadding = 16.dp) {
            tabs.forEachIndexed { idx, title ->
                Tab(
                    selected = selectedTab == idx,
                    onClick = { selectedTab = idx },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(title)
                            if (idx == 3 && !isProUnlocked) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Outlined.Lock,
                                    contentDescription = "Pro study tool",
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    },
                    modifier = Modifier.testTag("study_tab_$idx")
                )
            }
        }

        when (selectedTab) {
            0 -> GlossaryTab(glossary)
            1 -> YggdrasilTab(onOpenPassage = onOpenPassage)
            2 -> FamilyTreesTab()
            3 -> OcrManuscriptTab(
                manuscripts = manuscripts,
                ocrProcessing = ocrProcessing,
                vaultUnlocked = vaultUnlocked,
                biometricVaultEnabled = biometricVaultEnabled,
                isProUnlocked = isProUnlocked,
                onUnlockVaultRequest = onUnlockVaultRequest,
                onProcessOcrUri = onProcessOcrUri,
                onProcessCameraFrame = onProcessCameraFrame,
                onSaveManualManuscript = onSaveManualManuscript,
                onDeleteManuscript = onDeleteManuscript,
                onShowMessage = onShowMessage,
                onRequestPaywall = onRequestPaywall
            )
        }
    }
}

@Composable
private fun GlossaryTab(glossary: List<GlossaryEntryEntity>) {
    var query by remember { mutableStateOf("") }
    var category by remember { mutableStateOf<GlossaryCategory?>(null) }

    val filtered = remember(glossary, query, category) {
        glossary.filter { item ->
            val catOk = category == null || item.category == category
            val qOk = query.isBlank() ||
                item.term.contains(query, ignoreCase = true) ||
                item.oldNorseForm.contains(query, ignoreCase = true) ||
                item.definition.contains(query, ignoreCase = true)
            catOk && qOk
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Filter gods, giants, places, kennings...") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(selected = category == null, onClick = { category = null }, label = { Text("All") })
                }
                items(GlossaryCategory.entries) { c ->
                    FilterChip(
                        selected = category == c,
                        onClick = { category = if (category == c) null else c },
                        label = { Text(c.label) }
                    )
                }
            }
        }
        items(filtered, key = { it.id }) { entry ->
            CarvedWoodCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(entry.term, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    Text(entry.category.label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                }
                Text(entry.oldNorseForm, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(6.dp))
                Text(entry.definition, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Sources: ${entry.primarySource}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                Text(entry.sourceDistinctionNote, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun YggdrasilTab(onOpenPassage: (String) -> Unit) {
    var selectedWorld by remember { mutableStateOf<NineWorldNode>(StudyAtlasData.nineWorlds.first()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .clip(RoundedCornerShape(18.dp))
            ) {
                val boxWidthPx = constraints.maxWidth.toFloat()
                val boxHeightPx = constraints.maxHeight.toFloat()

                Image(
                    painter = painterResource(id = R.drawable.img_yggdrasil_cosmos),
                    contentDescription = "Interactive Yggdrasil World Tree and Nine Worlds",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.25f))
                )

                StudyAtlasData.nineWorlds.forEach { world ->
                    val isSelected = world.id == selectedWorld.id
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    x = (world.xFraction * boxWidthPx - 110f).roundToInt().coerceIn(8, (boxWidthPx - 220f).roundToInt()),
                                    y = (world.yFraction * boxHeightPx - 32f).roundToInt().coerceIn(8, (boxHeightPx - 64f).roundToInt())
                                )
                            }
                            .clickable { selectedWorld = world }
                    ) {
                        Text(
                            text = world.name,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        item {
            CarvedWoodCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            ) {
                Text(selectedWorld.tier.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                Text("${selectedWorld.name} (${selectedWorld.oldNorseName})", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(6.dp))
                Text(selectedWorld.description, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Inhabitants: ${selectedWorld.inhabitants}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(10.dp))
                Button(onClick = { onOpenPassage(selectedWorld.linkedPassageId) }) {
                    Text("Read Primary Source Passage")
                }
            }
        }
    }
}

@Composable
private fun FamilyTreesTab() {
    var selectedTreeIdx by remember { mutableIntStateOf(0) }
    val tree = StudyAtlasData.familyTrees[selectedTreeIdx]

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(StudyAtlasData.familyTrees.indices.toList()) { idx ->
                    FilterChip(
                        selected = selectedTreeIdx == idx,
                        onClick = { selectedTreeIdx = idx },
                        label = { Text(StudyAtlasData.familyTrees[idx].title) }
                    )
                }
            }
        }
        item {
            CarvedWoodCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
            ) {
                Text(tree.title, style = MaterialTheme.typography.headlineMedium)
                Text(tree.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        items(tree.members, key = { it.id }) { member ->
            CarvedWoodCard(modifier = Modifier.fillMaxWidth()) {
                Text(member.name, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                Text(member.role, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
                KnotworkDivider(modifier = Modifier.padding(vertical = 6.dp))
                Text("Ancestry: ${member.parents}", style = MaterialTheme.typography.bodySmall)
                Text("Descendants: ${member.children}", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Attested in: ${member.sourceCitation}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun OcrManuscriptTab(
    manuscripts: List<DigitizedManuscriptEntity>,
    ocrProcessing: Boolean,
    vaultUnlocked: Boolean,
    biometricVaultEnabled: Boolean,
    isProUnlocked: Boolean,
    onUnlockVaultRequest: () -> Unit,
    onProcessOcrUri: (Uri, String, Boolean, (String, String) -> Unit) -> Unit,
    onProcessCameraFrame: (ImageProxy, String, Boolean, (String, String) -> Unit) -> Unit,
    onSaveManualManuscript: (String, String, Boolean) -> Unit,
    onDeleteManuscript: (Long) -> Unit,
    onShowMessage: (String) -> Unit,
    onRequestPaywall: (PaywallFeature) -> Unit
) {
    val context = LocalContext.current
    var docTitle by remember { mutableStateOf("Codex Leaf Scan") }
    var savePrivate by remember { mutableStateOf(false) }
    var latestExtracted by remember { mutableStateOf<String?>(null) }
    var latestGlosses by remember { mutableStateOf<String?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var showLiveCameraDialog by remember { mutableStateOf(false) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            showLiveCameraDialog = true
        } else {
            onShowMessage("Camera permission denied. Use Pick Page or Sample Folio instead.")
        }
    }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onProcessOcrUri(uri, docTitle, savePrivate) { text, notes ->
                latestExtracted = text
                latestGlosses = notes
            }
        }
    }

    val takePicture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val uri = tempCameraUri
        if (success && uri != null) {
            onProcessOcrUri(uri, docTitle, savePrivate) { text, notes ->
                latestExtracted = text
                latestGlosses = notes
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            CarvedWoodCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Outlined.DocumentScanner, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Offline Manuscript & Page OCR", style = MaterialTheme.typography.titleLarge)
                    }
                    if (!isProUnlocked) {
                        ProLockBadge(label = "Pro OCR")
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isProUnlocked) {
                        "Scan printed Eddas, saga pages, or facsimiles using on-device ML Kit recognition and cross-reference words directly against Mímir's Old Norse Lexicon."
                    } else {
                        "Try Sample Folio free, or unlock Mímir Pro for live CameraX scanning, gallery page OCR, and biometric vault archiving."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = docTitle,
                    onValueChange = { docTitle = it },
                    label = { Text("Archive Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                LockedFeatureOverlay(
                    isLocked = !isProUnlocked,
                    label = "Camera & Gallery OCR Tool",
                    subtitle = "Unlock Live Scan, Pick Page & Vault Archive",
                    onUnlockClick = { onRequestPaywall(PaywallFeature.MANUSCRIPT_OCR) },
                    overlayTag = "locked_ocr_study_tool"
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = savePrivate,
                                onCheckedChange = { checked ->
                                    if (checked && !isProUnlocked) {
                                        onRequestPaywall(PaywallFeature.BIOMETRIC_VAULT)
                                    } else {
                                        savePrivate = checked
                                    }
                                }
                            )
                            Text("Lock inside Biometric Private Vault", style = MaterialTheme.typography.bodySmall)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (!isProUnlocked) {
                                        onRequestPaywall(PaywallFeature.MANUSCRIPT_OCR)
                                        return@Button
                                    }
                                    val hasPerm = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.CAMERA
                                    ) == PackageManager.PERMISSION_GRANTED
                                    if (hasPerm) {
                                        showLiveCameraDialog = true
                                    } else {
                                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                },
                                enabled = !ocrProcessing,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("ocr_live_camera")
                            ) {
                                Icon(Icons.Outlined.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Live Scan")
                            }
                            OutlinedButton(
                                onClick = {
                                    if (!isProUnlocked) {
                                        onRequestPaywall(PaywallFeature.MANUSCRIPT_OCR)
                                        return@OutlinedButton
                                    }
                                    photoPicker.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                enabled = !ocrProcessing,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("ocr_pick_image")
                            ) {
                                Icon(Icons.Outlined.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Pick Page")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = {
                                if (!isProUnlocked) {
                                    onRequestPaywall(PaywallFeature.MANUSCRIPT_OCR)
                                    return@OutlinedButton
                                }
                                val hasPerm = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                ) == PackageManager.PERMISSION_GRANTED
                                if (!hasPerm) {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                    return@OutlinedButton
                                }
                                val dir = File(context.cacheDir, "ocr_captures").apply { mkdirs() }
                                val file = File(dir, "scan_${System.currentTimeMillis()}.jpg")
                                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                                tempCameraUri = uri
                                takePicture.launch(uri)
                            },
                            enabled = !ocrProcessing,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Photo Snap")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        val sampleNorse = "Ár var alda þar er Ymir byggði; Þar er Mímisbrunnr, er spekð ok mannvit er í fólgit."
                        onSaveManualManuscript(docTitle, sampleNorse, false)
                        latestExtracted = sampleNorse
                        latestGlosses = ManuscriptOcrService.formatTranslationSummary(
                            ManuscriptOcrService.tokenizeForTranslation(sampleNorse)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ocr_sample_folio")
                ) {
                    Text("Try Free Sample Folio")
                }

                if (ocrProcessing) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Recognizing text offline...", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        if (latestExtracted != null) {
            item {
                CarvedWoodCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = MaterialTheme.colorScheme.secondary
                ) {
                    Text("Recognized Text", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                    Text(latestExtracted!!, style = MaterialTheme.typography.bodyLarge)
                    KnotworkDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("Lexicon Match", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
                    Text(latestGlosses ?: "", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        item {
            SectionHeader(
                title = "Digitized Archive (${manuscripts.size})",
                subtitle = if (biometricVaultEnabled && !vaultUnlocked) "Private items locked until biometric verification" else null
            )
        }

        items(manuscripts, key = { it.id }) { scan ->
            val locked = scan.isPrivateVault && biometricVaultEnabled && !vaultUnlocked
            CarvedWoodCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(scan.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        if (scan.isPrivateVault) {
                            Text("Private Vault Document", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                    IconButton(onClick = { onDeleteManuscript(scan.id) }) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete digitized manuscript")
                    }
                }
                if (locked) {
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedButton(onClick = onUnlockVaultRequest) {
                        Text("Unlock with Biometrics")
                    }
                } else {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(scan.extractedText, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(scan.matchedLexiconNotes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }

    if (showLiveCameraDialog) {
        CameraXManuscriptScannerDialog(
            ocrProcessing = ocrProcessing,
            onDismiss = { showLiveCameraDialog = false },
            onCaptureFrame = { imageProxy ->
                showLiveCameraDialog = false
                onProcessCameraFrame(imageProxy, docTitle, savePrivate) { text, notes ->
                    latestExtracted = text
                    latestGlosses = notes
                }
            },
            onError = { msg ->
                showLiveCameraDialog = false
                onShowMessage(msg)
            }
        )
    }
}

@Composable
private fun CameraXManuscriptScannerDialog(
    ocrProcessing: Boolean,
    onDismiss: () -> Unit,
    onCaptureFrame: (ImageProxy) -> Unit,
    onError: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var cameraProviderRef by remember { mutableStateOf<ProcessCameraProvider?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            runCatching { cameraProviderRef?.unbindAll() }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ML Kit Manuscript Scanner",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Align historical page inside frame",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    OutlinedButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            val previewView = PreviewView(ctx)
                            val providerFuture = ProcessCameraProvider.getInstance(ctx)
                            providerFuture.addListener(
                                {
                                    runCatching {
                                        val provider = providerFuture.get()
                                        cameraProviderRef = provider
                                        val preview = Preview.Builder().build().also {
                                            it.surfaceProvider = previewView.surfaceProvider
                                        }
                                        val capture = ImageCapture.Builder()
                                            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                            .build()
                                        imageCapture = capture
                                        provider.unbindAll()
                                        provider.bindToLifecycle(
                                            lifecycleOwner,
                                            CameraSelector.DEFAULT_BACK_CAMERA,
                                            preview,
                                            capture
                                        )
                                    }.onFailure { err ->
                                        onError("Camera unavailable: ${err.localizedMessage ?: "Use Pick Page or Sample Folio"}")
                                    }
                                },
                                ContextCompat.getMainExecutor(ctx)
                            )
                            previewView
                        }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            val capture = imageCapture
                            if (capture == null) {
                                onError("Camera preview not ready yet")
                                return@Button
                            }
                            capture.takePicture(
                                ContextCompat.getMainExecutor(context),
                                object : ImageCapture.OnImageCapturedCallback() {
                                    override fun onCaptureSuccess(image: ImageProxy) {
                                        onCaptureFrame(image)
                                    }

                                    override fun onError(exception: ImageCaptureException) {
                                        onError("Capture failed: ${exception.localizedMessage}")
                                    }
                                }
                            )
                        },
                        enabled = !ocrProcessing,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("capture_camera_frame_button")
                    ) {
                        Icon(Icons.Outlined.DocumentScanner, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Scan Page for Translation")
                    }
                }
            }
        }
    }
}
