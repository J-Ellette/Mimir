package com.example.data.local

import android.util.Base64
import com.example.data.model.DigitizedManuscriptEntity
import com.example.data.model.DiscussionPostEntity
import com.example.data.model.FlashcardProgressEntity
import com.example.data.model.HighlightColor
import com.example.data.model.LessonProgressEntity
import com.example.data.model.ReadingPlanProgressEntity
import com.example.data.model.UserAnnotationEntity
import com.example.ui.theme.MimirThemeMode
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

@JsonClass(generateAdapter = true)
data class BackupSettingsDto(
    val themeMode: String,
    val readerFontScale: Float,
    val parallelSideBySide: Boolean,
    val morningReminderEnabled: Boolean,
    val morningReminderHour: Int,
    val biometricVaultEnabled: Boolean,
    val displayHandle: String
)

@JsonClass(generateAdapter = true)
data class BackupAnnotationDto(
    val passageId: String,
    val workId: String,
    val highlightColor: String,
    val isBookmarked: Boolean,
    val noteText: String,
    val isPrivateVault: Boolean,
    val updatedAt: Long
)

@JsonClass(generateAdapter = true)
data class BackupPlanProgressDto(
    val planId: String,
    val completedDaysCsv: String,
    val reflectionsSummary: String,
    val lastCompletedEpochDay: Long,
    val streakDays: Int
)

@JsonClass(generateAdapter = true)
data class BackupFlashcardDto(
    val runeId: String,
    val easeFactor: Float,
    val intervalDays: Int,
    val repetitions: Int,
    val nextReviewMillis: Long,
    val totalCorrect: Int,
    val totalAttempts: Int
)

@JsonClass(generateAdapter = true)
data class BackupDiscussionDto(
    val passageId: String,
    val authorName: String,
    val stanceTag: String,
    val body: String,
    val createdAt: Long
)

@JsonClass(generateAdapter = true)
data class BackupManuscriptDto(
    val title: String,
    val extractedText: String,
    val matchedLexiconNotes: String,
    val isPrivateVault: Boolean,
    val createdAt: Long
)

@JsonClass(generateAdapter = true)
data class BackupLessonDto(
    val lessonId: String,
    val isCompleted: Boolean,
    val bestScore: Int,
    val xpEarned: Int
)

@JsonClass(generateAdapter = true)
data class MimirBackupPayload(
    val schemaVersion: Int = 1,
    val exportedAtMillis: Long = System.currentTimeMillis(),
    val settings: BackupSettingsDto,
    val annotations: List<BackupAnnotationDto>,
    val planProgress: List<BackupPlanProgressDto>,
    val flashcards: List<BackupFlashcardDto>,
    val discussions: List<BackupDiscussionDto>,
    val manuscripts: List<BackupManuscriptDto>,
    val lessons: List<BackupLessonDto>,
    val installedPackIds: List<String>
)

@JsonClass(generateAdapter = true)
data class EncryptedBackupEnvelope(
    val format: String = "MIMIR_LOCAL_ENCRYPTED_V1",
    val saltBase64: String,
    val ivBase64: String,
    val cipherTextBase64: String
)

object EncryptedBackupManager {
    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val payloadAdapter = moshi.adapter(MimirBackupPayload::class.java)
    private val envelopeAdapter = moshi.adapter(EncryptedBackupEnvelope::class.java)

    fun encryptPayload(payload: MimirBackupPayload, passphrase: String): String {
        require(passphrase.length >= 4) { "Passphrase must be at least 4 characters." }
        val jsonBytes = payloadAdapter.toJson(payload).toByteArray(Charsets.UTF_8)

        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val iv = ByteArray(12).also { SecureRandom().nextBytes(it) }
        val key = deriveKey(passphrase, salt)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(128, iv))
        val encrypted = cipher.doFinal(jsonBytes)

        val envelope = EncryptedBackupEnvelope(
            saltBase64 = Base64.encodeToString(salt, Base64.NO_WRAP),
            ivBase64 = Base64.encodeToString(iv, Base64.NO_WRAP),
            cipherTextBase64 = Base64.encodeToString(encrypted, Base64.NO_WRAP)
        )
        return envelopeAdapter.toJson(envelope)
    }

    fun decryptPayload(rawFileText: String, passphrase: String): MimirBackupPayload {
        val envelope = envelopeAdapter.fromJson(rawFileText)
            ?: throw IllegalArgumentException("Unrecognized Mímir backup format.")
        if (envelope.format != "MIMIR_LOCAL_ENCRYPTED_V1") {
            throw IllegalArgumentException("Unsupported backup version: ${envelope.format}")
        }
        val salt = Base64.decode(envelope.saltBase64, Base64.NO_WRAP)
        val iv = Base64.decode(envelope.ivBase64, Base64.NO_WRAP)
        val cipherBytes = Base64.decode(envelope.cipherTextBase64, Base64.NO_WRAP)

        val key = deriveKey(passphrase, salt)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))
        val plainBytes = cipher.doFinal(cipherBytes)
        val json = String(plainBytes, Charsets.UTF_8)
        return payloadAdapter.fromJson(json)
            ?: throw IllegalStateException("Corrupted backup JSON.")
    }

    private fun deriveKey(passphrase: String, salt: ByteArray): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(passphrase.toCharArray(), salt, 65_536, 256)
        val secretBytes = factory.generateSecret(spec).encoded
        return SecretKeySpec(secretBytes, "AES")
    }

    fun UserSettings.toDto() = BackupSettingsDto(
        themeMode = themeMode.name,
        readerFontScale = readerFontScale,
        parallelSideBySide = parallelSideBySide,
        morningReminderEnabled = morningReminderEnabled,
        morningReminderHour = morningReminderHour,
        biometricVaultEnabled = biometricVaultEnabled,
        displayHandle = displayHandle
    )

    fun BackupSettingsDto.toDomain(): UserSettings {
        val mode = runCatching { MimirThemeMode.valueOf(themeMode) }
            .getOrDefault(MimirThemeMode.CARVED_WOOD_DARK)
        return UserSettings(
            themeMode = mode,
            lastDarkTheme = if (mode.isDark) mode else MimirThemeMode.CARVED_WOOD_DARK,
            lastLightTheme = if (!mode.isDark) mode else MimirThemeMode.PARCHMENT_LIGHT,
            readerFontScale = readerFontScale,
            parallelSideBySide = parallelSideBySide,
            morningReminderEnabled = morningReminderEnabled,
            morningReminderHour = morningReminderHour,
            biometricVaultEnabled = biometricVaultEnabled,
            displayHandle = displayHandle
        )
    }

    fun UserAnnotationEntity.toDto() = BackupAnnotationDto(
        passageId, workId, highlightColor.name, isBookmarked, noteText, isPrivateVault, updatedAt
    )

    fun BackupAnnotationDto.toEntity() = UserAnnotationEntity(
        passageId = passageId,
        workId = workId,
        highlightColor = runCatching { HighlightColor.valueOf(highlightColor) }.getOrDefault(HighlightColor.NONE),
        isBookmarked = isBookmarked,
        noteText = noteText,
        isPrivateVault = isPrivateVault,
        updatedAt = updatedAt
    )

    fun ReadingPlanProgressEntity.toDto() = BackupPlanProgressDto(
        planId, completedDaysCsv, reflectionsSummary, lastCompletedEpochDay, streakDays
    )

    fun BackupPlanProgressDto.toEntity() = ReadingPlanProgressEntity(
        planId, completedDaysCsv, reflectionsSummary, lastCompletedEpochDay, streakDays
    )

    fun FlashcardProgressEntity.toDto() = BackupFlashcardDto(
        runeId, easeFactor, intervalDays, repetitions, nextReviewMillis, totalCorrect, totalAttempts
    )

    fun BackupFlashcardDto.toEntity() = FlashcardProgressEntity(
        runeId, easeFactor, intervalDays, repetitions, nextReviewMillis, totalCorrect, totalAttempts
    )

    fun DiscussionPostEntity.toDto() = BackupDiscussionDto(
        passageId, authorName, stanceTag, body, createdAt
    )

    fun BackupDiscussionDto.toEntity() = DiscussionPostEntity(
        passageId = passageId,
        authorName = authorName,
        stanceTag = stanceTag,
        body = body,
        createdAt = createdAt
    )

    fun DigitizedManuscriptEntity.toDto() = BackupManuscriptDto(
        title, extractedText, matchedLexiconNotes, isPrivateVault, createdAt
    )

    fun BackupManuscriptDto.toEntity() = DigitizedManuscriptEntity(
        title = title,
        extractedText = extractedText,
        matchedLexiconNotes = matchedLexiconNotes,
        isPrivateVault = isPrivateVault,
        createdAt = createdAt
    )

    fun LessonProgressEntity.toDto() = BackupLessonDto(
        lessonId, isCompleted, bestScore, xpEarned
    )

    fun BackupLessonDto.toEntity() = LessonProgressEntity(
        lessonId, isCompleted, bestScore, xpEarned
    )
}
