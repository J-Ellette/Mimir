package com.example.data.repository

import com.example.data.local.EncryptedBackupManager
import com.example.data.local.EncryptedBackupManager.toDomain
import com.example.data.local.EncryptedBackupManager.toDto
import com.example.data.local.EncryptedBackupManager.toEntity
import com.example.data.local.MimirBackupPayload
import com.example.data.local.MimirDao
import com.example.data.local.UserPreferencesRepository
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
import com.example.data.model.RunestoneInscriptionEntity
import com.example.data.model.UserAnnotationEntity
import com.example.data.model.VerseEntity
import com.example.data.seed.CorpusSeedData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class MimirRepository(
    private val dao: MimirDao,
    private val prefs: UserPreferencesRepository
) {

    suspend fun ensureSeeded() {
        if (dao.countBooks() == 0) {
            dao.insertBooks(CorpusSeedData.books)
            dao.insertChapters(CorpusSeedData.chapters)
            dao.insertVerses(CorpusSeedData.verses)
            dao.insertGlossary(CorpusSeedData.glossary)
            dao.insertCommentaryPacks(CorpusSeedData.commentaryPacks)
            dao.insertDiscussionPosts(CorpusSeedData.initialDiscussions)
        }
        if (dao.countRunes() == 0) {
            dao.insertRunes(CorpusSeedData.buildRuneEntities())
            dao.insertRunestoneInscriptions(CorpusSeedData.buildInscriptionEntities())
        }
    }

    fun observeBooks(): Flow<List<BookEntity>> = dao.observeBooks()
    fun observeBook(bookId: String): Flow<BookEntity?> = dao.observeBook(bookId)
    fun observeChaptersForBook(bookId: String): Flow<List<ChapterEntity>> = dao.observeChaptersForBook(bookId)
    fun observeVersesForBook(bookId: String): Flow<List<VerseEntity>> = dao.observeVersesForBook(bookId)
    fun observeAllVerses(): Flow<List<VerseEntity>> = dao.observeAllVerses()
    fun searchVerses(query: String): Flow<List<VerseEntity>> = dao.searchVerses(query)
    suspend fun getVerseById(id: String): VerseEntity? = dao.getVerseById(id)

    fun observeAllRunes(): Flow<List<RuneEntity>> = dao.observeAllRunes()
    fun observeRunesByAlphabet(alphabet: String): Flow<List<RuneEntity>> = dao.observeRunesByAlphabet(alphabet)
    fun observeRunestoneInscriptions(): Flow<List<RunestoneInscriptionEntity>> = dao.observeRunestoneInscriptions()

    fun observeGlossary(): Flow<List<GlossaryEntryEntity>> = dao.observeGlossary()
    fun searchGlossary(query: String): Flow<List<GlossaryEntryEntity>> = dao.searchGlossary(query)

    fun observeCommentaryPacks(): Flow<List<CommentaryPackEntity>> = dao.observeCommentaryPacks()
    suspend fun toggleCommentaryPack(packId: String, installed: Boolean) = dao.setPackInstalled(packId, installed)

    fun observeAnnotations(): Flow<List<UserAnnotationEntity>> = dao.observeAnnotations()
    fun observeAnnotationsForBook(bookId: String): Flow<List<UserAnnotationEntity>> = dao.observeAnnotationsForBook(bookId)

    suspend fun updateAnnotation(
        verseId: String,
        bookId: String,
        highlightColor: HighlightColor? = null,
        isBookmarked: Boolean? = null,
        noteText: String? = null,
        isPrivateVault: Boolean? = null
    ) {
        val existing = dao.getAnnotationForVerse(verseId)
        val updated = UserAnnotationEntity(
            id = existing?.id ?: 0L,
            passageId = verseId,
            workId = bookId,
            highlightColor = highlightColor ?: existing?.highlightColor ?: HighlightColor.NONE,
            isBookmarked = isBookmarked ?: existing?.isBookmarked ?: false,
            noteText = noteText ?: existing?.noteText ?: "",
            isPrivateVault = isPrivateVault ?: existing?.isPrivateVault ?: false,
            updatedAt = System.currentTimeMillis()
        )
        if (updated.highlightColor == HighlightColor.NONE && !updated.isBookmarked && updated.noteText.isBlank()) {
            dao.deleteAnnotationForVerse(verseId)
        } else {
            dao.upsertAnnotation(updated)
        }
    }

    fun observePlanProgress(): Flow<List<ReadingPlanProgressEntity>> = dao.observePlanProgress()

    suspend fun togglePlanDayCompleted(planId: String, dayNumber: Int, reflectionNote: String) {
        val existing = dao.getPlanProgress(planId)
        val completedSet = existing?.completedDaysCsv
            ?.split(",")
            ?.mapNotNull { it.trim().toIntOrNull() }
            ?.toMutableSet() ?: mutableSetOf()

        val wasAdded = !completedSet.contains(dayNumber)
        if (wasAdded) completedSet.add(dayNumber) else completedSet.remove(dayNumber)

        val todayEpoch = LocalDate.now().toEpochDay()
        val prevEpoch = existing?.lastCompletedEpochDay ?: 0L
        val newStreak = when {
            !wasAdded -> existing?.streakDays ?: 0
            prevEpoch == todayEpoch -> (existing?.streakDays ?: 1).coerceAtLeast(1)
            prevEpoch == todayEpoch - 1L -> (existing?.streakDays ?: 0) + 1
            else -> 1
        }

        val combinedReflection = buildString {
            if (!existing?.reflectionsSummary.isNullOrBlank()) {
                append(existing!!.reflectionsSummary)
            }
            if (reflectionNote.isNotBlank()) {
                if (isNotEmpty()) append("\n")
                append("Day $dayNumber: ${reflectionNote.trim()}")
            }
        }

        dao.upsertPlanProgress(
            ReadingPlanProgressEntity(
                planId = planId,
                completedDaysCsv = completedSet.sorted().joinToString(","),
                reflectionsSummary = combinedReflection,
                lastCompletedEpochDay = if (wasAdded) todayEpoch else prevEpoch,
                streakDays = newStreak
            )
        )
    }

    fun observeFlashcardProgress(): Flow<List<FlashcardProgressEntity>> = dao.observeFlashcardProgress()

    suspend fun recordFlashcardReview(runeId: String, quality: ReviewQuality) {
        val current = dao.getFlashcardProgress(runeId)
        val next = SpacedRepetitionEngine.evaluate(current, runeId, quality)
        dao.upsertFlashcardProgress(next)
    }

    fun observeDiscussionsForVerse(verseId: String): Flow<List<DiscussionPostEntity>> =
        dao.observeDiscussionsForVerse(verseId)

    fun observeAllDiscussions(): Flow<List<DiscussionPostEntity>> = dao.observeAllDiscussions()

    suspend fun addDiscussionPost(verseId: String, authorName: String, stanceTag: String, body: String) {
        if (body.isBlank()) return
        dao.insertDiscussionPost(
            DiscussionPostEntity(
                passageId = verseId,
                authorName = authorName.ifBlank { "Gestr" },
                stanceTag = stanceTag,
                body = body.trim(),
                createdAt = System.currentTimeMillis()
            )
        )
    }

    fun observeDigitizedManuscripts(): Flow<List<DigitizedManuscriptEntity>> = dao.observeDigitizedManuscripts()

    suspend fun saveDigitizedManuscript(
        title: String,
        extractedText: String,
        matchedLexiconNotes: String,
        isPrivateVault: Boolean
    ) {
        dao.insertDigitizedManuscript(
            DigitizedManuscriptEntity(
                title = title.ifBlank { "Manuscript Leaf" },
                extractedText = extractedText,
                matchedLexiconNotes = matchedLexiconNotes,
                isPrivateVault = isPrivateVault
            )
        )
    }

    suspend fun deleteDigitizedManuscript(id: Long) = dao.deleteDigitizedManuscript(id)

    fun observeLessonProgress(): Flow<List<LessonProgressEntity>> = dao.observeLessonProgress()

    suspend fun saveLessonResult(lessonId: String, score: Int, totalQuestions: Int) {
        val passed = score >= (totalQuestions.coerceAtLeast(1) + 1) / 2
        dao.upsertLessonProgress(
            LessonProgressEntity(
                lessonId = lessonId,
                isCompleted = passed,
                bestScore = score,
                xpEarned = score * 25
            )
        )
    }

    suspend fun exportEncryptedBackup(passphrase: String): String {
        val settings = prefs.settingsFlow.first()
        val packs = dao.observeCommentaryPacks().first()
        val payload = MimirBackupPayload(
            settings = settings.toDto(),
            annotations = dao.getAllAnnotationsOnce().map { it.toDto() },
            planProgress = dao.getAllPlanProgressOnce().map { it.toDto() },
            flashcards = dao.getAllFlashcardProgressOnce().map { it.toDto() },
            discussions = dao.getAllDiscussionsOnce().map { it.toDto() },
            manuscripts = dao.getAllManuscriptsOnce().map { it.toDto() },
            lessons = dao.getAllLessonProgressOnce().map { it.toDto() },
            installedPackIds = packs.filter { it.isInstalled }.map { it.id }
        )
        return EncryptedBackupManager.encryptPayload(payload, passphrase)
    }

    suspend fun importEncryptedBackup(rawEncryptedText: String, passphrase: String) {
        val payload = EncryptedBackupManager.decryptPayload(rawEncryptedText, passphrase)
        prefs.restoreFromSettings(payload.settings.toDomain())
        dao.insertAnnotations(payload.annotations.map { it.toEntity() })
        dao.insertPlanProgressList(payload.planProgress.map { it.toEntity() })
        dao.insertFlashcardProgressList(payload.flashcards.map { it.toEntity() })
        dao.insertManuscripts(payload.manuscripts.map { it.toEntity() })
        dao.insertLessonProgressList(payload.lessons.map { it.toEntity() })
        val allPacks = dao.observeCommentaryPacks().first()
        for (pack in allPacks) {
            if (!pack.isCoreRequired && !pack.isLicensedPreview) {
                dao.setPackInstalled(pack.id, payload.installedPackIds.contains(pack.id))
            }
        }
    }

    fun getDailyVerse(verses: List<VerseEntity>): VerseEntity? {
        if (verses.isEmpty()) return null
        val havamalAndSagas = verses.filter {
            it.bookId == "havamal" || it.bookId == "voluspa" || it.bookId == "laxdaela_saga" || it.bookId == "njals_saga"
        }.ifEmpty { verses }
        val dayIndex = (LocalDate.now().dayOfYear - 1).coerceAtLeast(0)
        return havamalAndSagas[dayIndex % havamalAndSagas.size]
    }
}
