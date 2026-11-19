package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.seed.RuneAlphabet
import com.example.data.seed.RuneTransliterator
import com.example.ui.components.CarvedWoodCard
import com.example.ui.components.KnotworkDivider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElderFutharkTransliteratorScreen(
    onBack: () -> Unit,
    onShowMessage: (String) -> Unit
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    var nameInput by remember { mutableStateOf("Astrid") }

    val sampleNames = remember {
        listOf("Astrid", "Thor", "Sigrún", "Björn", "Freya", "Gunnarr")
    }

    val elderRunesOutput = remember(nameInput) {
        RuneTransliterator.transliterate(nameInput, RuneAlphabet.ELDER_FUTHARK)
    }

    val detailedTokens = remember(nameInput) {
        RuneTransliterator.transliterateElderFutharkDetailed(nameInput)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = "Elder Futhark Transliterator",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("transliterator_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Enter a name or word") },
                    placeholder = { Text("e.g., Astrid, Thor, Ingvar, Þórr") },
                    singleLine = true,
                    trailingIcon = {
                        if (nameInput.isNotEmpty()) {
                            IconButton(
                                onClick = { nameInput = "" },
                                modifier = Modifier.testTag("clear_name_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Clear,
                                    contentDescription = "Clear name"
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("elder_name_input")
                )
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(sampleNames) { sample ->
                        FilterChip(
                            selected = nameInput.equals(sample, ignoreCase = true),
                            onClick = { nameInput = sample },
                            label = { Text(sample) }
                        )
                    }
                }
            }

            // Carved Elder Futhark Result Card
            item {
                CarvedWoodCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.65f)
                ) {
                    Text(
                        text = "ELDER FUTHARK (c. 150–800 CE)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = elderRunesOutput.ifBlank { "— Type a name above —" },
                        fontSize = if (elderRunesOutput.isBlank()) 18.sp else 40.sp,
                        lineHeight = if (elderRunesOutput.isBlank()) 24.sp else 52.sp,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("elder_runes_output")
                    )
                    if (elderRunesOutput.isNotBlank()) {
                        KnotworkDivider(modifier = Modifier.padding(vertical = 10.dp))
                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(
                                    ClipData.newPlainText("Elder Futhark Runes", elderRunesOutput)
                                )
                                onShowMessage("Copied $elderRunesOutput to clipboard")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("copy_runes_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Copy Elder Futhark Runes")
                        }
                    }
                }
            }

            // Informational Disclaimer about Transliteration
            item {
                CarvedWoodCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("transliteration_disclaimer_card"),
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "Transliteration disclaimer",
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Disclaimer: Phonetic Transliteration, Not Translation",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Elder Futhark is a historical writing system, not a language. Typing a name maps its sounds and digraphs (such as 'th' -> ᚦ and 'ng' -> ᛜ) letter-by-sound into the 24-rune Proto-Norse / Proto-Germanic futhark; it does not translate modern names or words into Old Norse grammar or vocabulary.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            // Rune-by-Rune Breakdown
            if (detailedTokens.isNotEmpty()) {
                item {
                    Text(
                        text = "Rune-by-Rune Sound Mapping",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                items(detailedTokens) { token ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = token.glyph,
                                    fontSize = 26.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = token.runeName,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Input \"${token.sourceChars}\" · Sound ${token.soundValue}",
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
    }
}
