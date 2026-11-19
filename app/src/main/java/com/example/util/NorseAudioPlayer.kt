package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.PI
import kotlin.math.sin

class NorseAudioPlayer(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isReady = false
    private val scope = CoroutineScope(Dispatchers.Default)

    override fun onInit(status: Int) {
        isReady = status == TextToSpeech.SUCCESS
    }

    fun speak(text: String, isModernIcelandic: Boolean) {
        val engine = tts
        if (isReady && engine != null) {
            val icelandicLocale = Locale.Builder().setLanguage("is").setRegion("IS").build()
            val availability = engine.isLanguageAvailable(icelandicLocale)
            if (availability >= TextToSpeech.LANG_AVAILABLE) {
                engine.language = icelandicLocale
                engine.setSpeechRate(if (isModernIcelandic) 0.92f else 0.78f)
                engine.setPitch(if (isModernIcelandic) 1.0f else 0.9f)
                engine.speak(normalizeForTts(text), TextToSpeech.QUEUE_FLUSH, null, "mimir_tts")
                return
            } else {
                engine.language = Locale.US
                engine.setSpeechRate(if (isModernIcelandic) 0.88f else 0.76f)
                val phoneticApprox = approximateOldNorseInAscii(text)
                val res = engine.speak(phoneticApprox, TextToSpeech.QUEUE_FLUSH, null, "mimir_tts_fallback")
                if (res == TextToSpeech.SUCCESS) return
            }
        }
        // Pure offline PCM chime cadence fallback if TTS engine is unavailable
        playSyllableCadence(text.split(" ").size.coerceIn(2, 6), isModernIcelandic)
    }

    private fun normalizeForTts(input: String): String =
        input.replace("ǫ", "ö").replace("œ", "æ")

    private fun approximateOldNorseInAscii(input: String): String =
        input.replace("þ", "th")
            .replace("Þ", "Th")
            .replace("ð", "dh")
            .replace("Ð", "Dh")
            .replace("ǫ", "o")
            .replace("æ", "ae")
            .replace("œ", "oe")
            .replace("ø", "oe")
            .replace("á", "ah")
            .replace("é", "eh")
            .replace("í", "ee")
            .replace("ó", "oh")
            .replace("ú", "oo")
            .replace("ý", "y")

    private fun playSyllableCadence(syllables: Int, highPitch: Boolean) {
        scope.launch {
            runCatching {
                val sampleRate = 22050
                val noteMs = 110
                val samplesPerNote = sampleRate * noteMs / 1000
                val totalSamples = samplesPerNote * syllables
                val buffer = ShortArray(totalSamples)
                val baseFreq = if (highPitch) 330.0 else 262.0

                for (s in 0 until syllables) {
                    val freq = baseFreq * (1.0 + (s % 3) * 0.12)
                    val offset = s * samplesPerNote
                    for (i in 0 until samplesPerNote) {
                        val env = if (i < 200) i / 200.0 else ((samplesPerNote - i).toDouble() / samplesPerNote)
                        val sample = sin(2.0 * PI * freq * i / sampleRate) * 0.35 * env
                        buffer[offset + i] = (sample * Short.MAX_VALUE).toInt().toShort()
                    }
                }
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()
                track.write(buffer, 0, buffer.size)
                track.play()
            }
        }
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
