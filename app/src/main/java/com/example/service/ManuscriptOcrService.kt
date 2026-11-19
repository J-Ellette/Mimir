package com.example.service

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.example.data.seed.NorseLexiconEntry
import com.example.data.seed.OldNorseLexicon
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class ManuscriptScanOutput(
    val rawText: String,
    val textBlocks: List<String>,
    val translationTokens: List<NorseLexiconEntry>,
    val formattedGlossSummary: String
)

/**
 * Offline ML Kit Text Recognition service for scanning historical manuscript pages
 * via device camera (CameraX ImageProxy / Bitmap) or Photo Picker Uri and outputting
 * raw OCR text for Old Norse translation processing.
 */
class ManuscriptOcrService {

    private val recognizer: TextRecognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    @OptIn(ExperimentalGetImage::class)
    suspend fun scanCameraFrame(imageProxy: ImageProxy): ManuscriptScanOutput {
        return try {
            val mediaImage = imageProxy.image
                ?: throw IllegalStateException("Camera frame has no image buffer")
            val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            processInputImage(inputImage)
        } finally {
            imageProxy.close()
        }
    }

    suspend fun scanBitmap(bitmap: Bitmap, rotationDegrees: Int = 0): ManuscriptScanOutput {
        val inputImage = InputImage.fromBitmap(bitmap, rotationDegrees)
        return processInputImage(inputImage)
    }

    suspend fun scanUri(context: Context, uri: Uri): ManuscriptScanOutput {
        val inputImage = InputImage.fromFilePath(context, uri)
        return processInputImage(inputImage)
    }

    private suspend fun processInputImage(image: InputImage): ManuscriptScanOutput {
        val visionText = suspendCancellableCoroutine<Text> { cont ->
            recognizer.process(image)
                .addOnSuccessListener { result -> cont.resume(result) }
                .addOnFailureListener { exc -> cont.resumeWithException(exc) }
        }
        return buildOutput(visionText)
    }

    private fun buildOutput(visionText: Text): ManuscriptScanOutput {
        val raw = visionText.text.trim()
        val blocks = visionText.textBlocks.map { it.text.trim() }.filter { it.isNotEmpty() }
        val entries = tokenizeForTranslation(raw)
        val summary = formatTranslationSummary(entries)
        return ManuscriptScanOutput(
            rawText = raw,
            textBlocks = blocks,
            translationTokens = entries,
            formattedGlossSummary = summary
        )
    }

    fun close() {
        recognizer.close()
    }

    companion object {
        fun tokenizeForTranslation(rawText: String): List<NorseLexiconEntry> {
            val tokens = rawText.split(Regex("\\s+"))
                .map { it.trim { ch -> !ch.isLetter() && ch != 'þ' && ch != 'ð' && ch != 'ǫ' && ch != 'æ' && ch != 'œ' && ch != 'ø' } }
                .filter { it.length >= 2 }
                .distinctBy { it.lowercase() }
                .take(18)
            return tokens.map { OldNorseLexicon.lookupWord(it) }
        }

        fun formatTranslationSummary(entries: List<NorseLexiconEntry>): String {
            if (entries.isEmpty()) return "No recognizable Old Norse or Latin tokens extracted."
            return entries.joinToString("\n") { entry ->
                "• ${entry.surfaceForm} -> ${entry.lemma} (${entry.grammarTag}): ${entry.englishGloss}"
            }
        }
    }
}
