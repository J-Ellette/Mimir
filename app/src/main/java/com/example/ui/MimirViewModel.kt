package com.example.ui

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.camera.core.ImageProxy
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.MimirApplication
import com.example.data.local.DailyReadingProgress
import com.example.data.local.PaywallFeature
import com.example.data.local.ProEntitlement
import com.example.data.local.ProPlanTier
import com.example.data.local.UserSettings
import com.example.data.model.BookEntity
import com.example.data.model.ChapterEntity
import com.example.data.model.CommentaryPackEntity
import com.example.data.model.DigitizedManuscriptEntity
import com.example.data.model.DiscussionPostEntity
import com.example.data.model.FlashcardProgressEntity
import com.example.data.model.GlossaryEntryEntity
import com.example.data.model.HighlightColor
import com.example.data.model.LessonProgressEntity
import com.example.data.model.ReadingPlanProgressEntity
import com.example.data.model.RuneEntity
import com.example.data.model.UserAnnotationEntity
import com.example.data.model.VerseEntity
import com.example.data.repository.IcelandicCalendarEngine
import com.example.data.repository.IcelandicMonthInfo
import com.example.data.repository.ReviewQuality
import com.example.service.ManuscriptOcrService
import com.example.ui.theme.MimirThemeMode
import com.example.util.MorningReminderWorker
import com.example.util.NorseAudioPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class MimirViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as MimirApplication
    private val repo = app.repository
    private val prefs = app.preferencesRepository
    private val dailyReadingStore = app.dailyReadingDataStore
    private val entitlementManager = app.entitlementManager
    val audioPlayer = NorseAudioPlayer(application)
    val ocrService = ManuscriptOcrService()

    init {
        viewModelScope.launch {
            repo.ensureSeeded()
        }
    }

    val settings: StateFlow<UserSettings> = prefs.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())

    val dailyReadingProgress: StateFlow<DailyReadingProgress> = dailyReadingStore.progressFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DailyReadingProgress())

    val proEntitlement: StateFlow<ProEntitlement> = entitlementManager.entitlementFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProEntitlement())

    val isProUser: StateFlow<Boolean> = entitlementManager.isProUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val books: StateFlow<List<BookEntity>> = repo.observeBooks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allVerses: StateFlow<List<VerseEntity>> = repo.observeAllVerses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyVerse: StateFlow<VerseEntity?> = allVerses
        .map { repo.getDailyVerse(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allRunes: StateFlow<List<RuneEntity>> = repo.observeAllRunes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val glossary: StateFlow<List<GlossaryEntryEntity>> = repo.observeGlossary()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val commentaryPacks: StateFlow<List<CommentaryPackEntity>> = repo.observeCommentaryPacks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val annotations: StateFlow<List<UserAnnotationEntity>> = repo.observeAnnotations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val planProgress: StateFlow<List<ReadingPlanProgressEntity>> = repo.observePlanProgress()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val flashcardProgress: StateFlow<List<FlashcardProgressEntity>> = repo.observeFlashcardProgress()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDiscussions: StateFlow<List<DiscussionPostEntity>> = repo.observeAllDiscussions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val digitizedManuscripts: StateFlow<List<DigitizedManuscriptEntity>> = repo.observeDigitizedManuscripts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lessonProgress: StateFlow<List<LessonProgressEntity>> = repo.observeLessonProgress()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults: StateFlow<List<VerseEntity>> = _searchQuery
        .flatMapLatest { query ->
            if (query.trim().length < 2) flowOf(emptyList()) else repo.searchVerses(query.trim())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _vaultUnlocked = MutableStateFlow(false)
    val vaultUnlocked: StateFlow<Boolean> = _vaultUnlocked.asStateFlow()

    private val _statusBanner = MutableStateFlow<String?>(null)
    val statusBanner: StateFlow<String?> = _statusBanner.asStateFlow()

    private val _ocrProcessing = MutableStateFlow(false)
    val ocrProcessing: StateFlow<Boolean> = _ocrProcessing.asStateFlow()

    private val _paywallTrigger = MutableStateFlow<PaywallFeature?>(null)
    val paywallTrigger: StateFlow<PaywallFeature?> = _paywallTrigger.asStateFlow()

    val todayIcelandicDate: IcelandicMonthInfo
        get() = IcelandicCalendarEngine.toIcelandicDate(LocalDate.now())

    fun openPaywall(feature: PaywallFeature = PaywallFeature.GENERAL) {
        _paywallTrigger.value = feature
    }

    fun dismissPaywall() {
        _paywallTrigger.value = null
    }

    fun unlockPro(plan: ProPlanTier) {
        subscribePro(plan)
    }

    fun subscribePro(plan: ProPlanTier) {
        viewModelScope.launch {
            entitlementManager.unlockPro(plan)
            _paywallTrigger.value = null
            _statusBanner.value = "Subscribed to Mímir Pro (${plan.title})"
        }
    }

    fun restorePurchases() {
        viewModelScope.launch {
            entitlementManager.restorePurchases()
            _paywallTrigger.value = null
            _statusBanner.value = "Mímir Pro purchases restored"
        }
    }

    fun revokeProPreview() {
        viewModelScope.launch {
            entitlementManager.revokeProPreview()
            _statusBanner.value = "Switched back to Mímir Free Core preview"
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun showMessage(message: String) {
        _statusBanner.value = message
    }

    fun clearStatusBanner() {
        _statusBanner.value = null
    }

    fun setVaultUnlocked(unlocked: Boolean) {
        _vaultUnlocked.value = unlocked
    }

    fun observeBook(bookId: String): Flow<BookEntity?> = repo.observeBook(bookId)
    fun observeChaptersForBook(bookId: String): Flow<List<ChapterEntity>> = repo.observeChaptersForBook(bookId)
    fun observeVersesForBook(bookId: String): Flow<List<VerseEntity>> = repo.observeVersesForBook(bookId)

    fun toggleDarkMode() {
        viewModelScope.launch { prefs.toggleDarkMode() }
    }

    fun setThemeMode(mode: MimirThemeMode) {
        viewModelScope.launch { prefs.setThemeMode(mode) }
    }

    fun setFontScale(scale: Float) {
        viewModelScope.launch { prefs.setFontScale(scale) }
    }

    fun setParallelSideBySide(enabled: Boolean) {
        viewModelScope.launch { prefs.setParallelSideBySide(enabled) }
    }

    fun setMorningReminder(enabled: Boolean, hour: Int = 7) {
        viewModelScope.launch {
            prefs.setMorningReminder(enabled, hour)
            MorningReminderWorker.schedule(app, enabled)
        }
    }

    fun setBiometricVaultEnabled(enabled: Boolean) {
        viewModelScope.launch {
            prefs.setBiometricVaultEnabled(enabled)
            _vaultUnlocked.value = !enabled
        }
    }

    fun setDisplayHandle(handle: String) {
        viewModelScope.launch { prefs.setDisplayHandle(handle) }
    }

    fun toggleCommentaryPack(packId: String, installed: Boolean) {
        viewModelScope.launch { repo.toggleCommentaryPack(packId, installed) }
    }

    fun setHighlight(verseId: String, bookId: String, color: HighlightColor) {
        viewModelScope.launch { repo.updateAnnotation(verseId, bookId, highlightColor = color) }
    }

    fun toggleBookmark(verseId: String, bookId: String, currentlyBookmarked: Boolean) {
        viewModelScope.launch { repo.updateAnnotation(verseId, bookId, isBookmarked = !currentlyBookmarked) }
    }

    fun saveVerseNote(verseId: String, bookId: String, note: String, isPrivateVault: Boolean) {
        viewModelScope.launch {
            repo.updateAnnotation(verseId, bookId, noteText = note, isPrivateVault = isPrivateVault)
            _statusBanner.value = if (isPrivateVault) "Saved to Private Vault" else "Study note saved"
        }
    }

    fun logDailyReadingVerse(count: Int = 1, showBanner: Boolean = true) {
        viewModelScope.launch {
            dailyReadingStore.recordVerseRead(count)
            if (showBanner) {
                _statusBanner.value = "Daily reading logged toward your streak"
            }
        }
    }

    fun markTodayReadingGoalComplete() {
        viewModelScope.launch {
            dailyReadingStore.markTodayCompleted()
            _statusBanner.value = "Today's reading goal complete — streak updated"
        }
    }

    fun setDailyReadingGoal(goalVerses: Int) {
        viewModelScope.launch {
            dailyReadingStore.setDailyGoal(goalVerses)
        }
    }

    fun togglePlanDay(planId: String, dayNumber: Int, reflectionNote: String) {
        viewModelScope.launch {
            repo.togglePlanDayCompleted(planId, dayNumber, reflectionNote)
            dailyReadingStore.recordVerseRead(1)
            _statusBanner.value = "Reading progress & streak updated"
        }
    }

    fun recordFlashcard(runeId: String, quality: ReviewQuality) {
        viewModelScope.launch { repo.recordFlashcardReview(runeId, quality) }
    }

    fun postDiscussion(verseId: String, stanceTag: String, body: String) {
        viewModelScope.launch {
            val handle = settings.value.displayHandle
            repo.addDiscussionPost(verseId, handle, stanceTag, body)
        }
    }

    fun saveLessonScore(lessonId: String, score: Int, total: Int) {
        viewModelScope.launch { repo.saveLessonResult(lessonId, score, total) }
    }

    fun processManuscriptCameraFrame(
        imageProxy: ImageProxy,
        title: String,
        saveToPrivateVault: Boolean,
        onComplete: (String, String) -> Unit
    ) {
        viewModelScope.launch {
            _ocrProcessing.value = true
            runCatching {
                ocrService.scanCameraFrame(imageProxy)
            }.onSuccess { output ->
                val raw = output.rawText.ifBlank { "(No legible Latin/Norse text detected in frame)" }
                repo.saveDigitizedManuscript(
                    title = title,
                    extractedText = raw,
                    matchedLexiconNotes = output.formattedGlossSummary,
                    isPrivateVault = saveToPrivateVault
                )
                _statusBanner.value = "Camera scan digitized offline"
                onComplete(raw, output.formattedGlossSummary)
            }.onFailure { err ->
                _statusBanner.value = "Camera OCR error: ${err.localizedMessage ?: "Unable to scan frame"}"
            }
            _ocrProcessing.value = false
        }
    }

    fun processManuscriptOcr(
        context: Context,
        uri: Uri,
        title: String,
        saveToPrivateVault: Boolean,
        onComplete: (String, String) -> Unit
    ) {
        viewModelScope.launch {
            _ocrProcessing.value = true
            runCatching {
                ocrService.scanUri(context, uri)
            }.onSuccess { output ->
                val raw = output.rawText.ifBlank { "(No legible Latin/Norse text detected in image)" }
                repo.saveDigitizedManuscript(
                    title = title,
                    extractedText = raw,
                    matchedLexiconNotes = output.formattedGlossSummary,
                    isPrivateVault = saveToPrivateVault
                )
                _statusBanner.value = "Manuscript digitized offline"
                onComplete(raw, output.formattedGlossSummary)
            }.onFailure { err ->
                _statusBanner.value = "OCR error: ${err.localizedMessage ?: "Unable to read image"}"
            }
            _ocrProcessing.value = false
        }
    }

    fun saveManualManuscript(title: String, text: String, saveToPrivateVault: Boolean) {
        viewModelScope.launch {
            val entries = ManuscriptOcrService.tokenizeForTranslation(text)
            val notes = ManuscriptOcrService.formatTranslationSummary(entries)
            repo.saveDigitizedManuscript(title, text, notes, saveToPrivateVault)
            _statusBanner.value = "Manuscript leaf archived offline"
        }
    }

    fun deleteManuscript(id: Long) {
        viewModelScope.launch { repo.deleteDigitizedManuscript(id) }
    }

    fun exportEncryptedBackupToUri(context: Context, uri: Uri, passphrase: String) {
        viewModelScope.launch {
            runCatching {
                val encryptedJson = withContext(Dispatchers.Default) {
                    repo.exportEncryptedBackup(passphrase)
                }
                withContext(Dispatchers.IO) {
                    context.contentResolver.openOutputStream(uri)?.use { os ->
                        os.write(encryptedJson.toByteArray(Charsets.UTF_8))
                    } ?: error("Cannot open destination file")
                }
            }.onSuccess {
                _statusBanner.value = "Encrypted AES-256 backup exported"
            }.onFailure { err ->
                _statusBanner.value = "Export failed: ${err.localizedMessage}"
            }
        }
    }

    fun importEncryptedBackupFromUri(context: Context, uri: Uri, passphrase: String) {
        viewModelScope.launch {
            runCatching {
                val raw = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                        ?: error("Cannot read backup file")
                }
                withContext(Dispatchers.Default) {
                    repo.importEncryptedBackup(raw, passphrase)
                }
            }.onSuccess {
                _statusBanner.value = "Backup restored across settings & study vault"
            }.onFailure {
                _statusBanner.value = "Restore failed: Wrong passphrase or corrupted file"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
        ocrService.close()
    }
}
