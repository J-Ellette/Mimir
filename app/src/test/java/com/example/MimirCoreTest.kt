package com.example

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.BackupAnnotationDto
import com.example.data.local.BackupSettingsDto
import com.example.data.local.DailyReadingDataStore
import com.example.data.local.DailyReadingProgress
import com.example.data.local.DailyReadingStreakCalculator
import com.example.data.local.EncryptedBackupManager
import com.example.data.local.EntitlementManager
import com.example.data.local.MimirBackupPayload
import com.example.data.local.MimirDatabase
import com.example.data.local.PaywallFeature
import com.example.data.local.ProEntitlementDataStore
import com.example.data.local.ProPlanTier
import com.example.data.repository.IcelandicCalendarEngine
import com.example.data.repository.ReviewQuality
import com.example.data.repository.SpacedRepetitionEngine
import com.example.data.seed.CorpusSeedData
import com.example.data.seed.OldNorseLexicon
import com.example.data.seed.RuneAlphabet
import com.example.data.seed.RuneTransliterator
import com.example.data.seed.RunesData
import com.example.service.ManuscriptOcrService
import com.example.ui.navigation.MimirRoutes
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class MimirCoreTest {

    @Test
    fun `rune transliterator handles digraphs across alphabets`() {
        val elder = RuneTransliterator.transliterate("Thor", RuneAlphabet.ELDER_FUTHARK)
        assertEquals("ᚦᛟᚱ", elder)

        val younger = RuneTransliterator.transliterate("Haraldr", RuneAlphabet.YOUNGER_FUTHARK)
        assertTrue(younger.startsWith("ᚼᛅᚱᛅᛚᛏ"))

        val futhorc = RuneTransliterator.transliterate("Eadgar", RuneAlphabet.ANGLO_SAXON_FUTHORC)
        assertTrue(futhorc.startsWith("ᛠ"))
    }

    @Test
    fun `rune catalogs have expected historical counts`() {
        assertEquals(24, RunesData.elderFuthark.size)
        assertEquals(16, RunesData.youngerFuthark.size)
        assertTrue(RunesData.angloSaxonFuthorc.size >= 28)
    }

    @Test
    fun `old norse lexicon parses exact words and inflected suffixes`() {
        val exact = OldNorseLexicon.lookupWord("Mímisbrunni,")
        assertEquals("Mímisbrunnr", exact.lemma)
        assertEquals("Dative Singular", exact.grammarTag)

        val dativePl = OldNorseLexicon.lookupWord("skógum")
        assertTrue(dativePl.grammarTag.contains("Dative Plural"))
    }

    @Test
    fun `icelandic misseri calendar maps key seasonal dates`() {
        val thorri = IcelandicCalendarEngine.toIcelandicDate(LocalDate.of(2026, 1, 25))
        assertEquals("Þorri", thorri.name)

        val harpa = IcelandicCalendarEngine.toIcelandicDate(LocalDate.of(2026, 4, 24))
        assertEquals("Harpa", harpa.name)

        val gorm = IcelandicCalendarEngine.toIcelandicDate(LocalDate.of(2026, 10, 25))
        assertEquals("Gormánuður", gorm.name)
    }

    @Test
    fun `sm2 spaced repetition advances intervals on good recall`() {
        val first = SpacedRepetitionEngine.evaluate(null, "ef_1", ReviewQuality.GOOD, nowMillis = 1000L)
        assertEquals(1, first.intervalDays)
        assertEquals(1, first.repetitions)

        val second = SpacedRepetitionEngine.evaluate(first, "ef_1", ReviewQuality.GOOD, nowMillis = 2000L)
        assertEquals(6, second.intervalDays)
        assertEquals(2, second.repetitions)
    }

    @Test
    fun `encrypted backup roundtrip preserves payload and rejects wrong passphrase`() {
        val payload = MimirBackupPayload(
            settings = BackupSettingsDto(
                themeMode = "CARVED_WOOD_DARK",
                readerFontScale = 1.15f,
                parallelSideBySide = true,
                morningReminderEnabled = true,
                morningReminderHour = 7,
                biometricVaultEnabled = true,
                displayHandle = "Völva"
            ),
            annotations = listOf(
                BackupAnnotationDto("voluspa_28", "voluspa", "GOLD", true, "Well note", true, 1234L)
            ),
            planProgress = emptyList(),
            flashcards = emptyList(),
            discussions = emptyList(),
            manuscripts = emptyList(),
            lessons = emptyList(),
            installedPackIds = listOf("pack_core_pd", "pack_linguistic_meter")
        )

        val encrypted = EncryptedBackupManager.encryptPayload(payload, "yggdrasil99")
        val decrypted = EncryptedBackupManager.decryptPayload(encrypted, "yggdrasil99")
        assertEquals("Völva", decrypted.settings.displayHandle)
        assertEquals(1, decrypted.annotations.size)
        assertEquals("voluspa_28", decrypted.annotations.first().passageId)

        val failed = runCatching {
            EncryptedBackupManager.decryptPayload(encrypted, "wrongpass")
        }
        assertTrue(failed.isFailure)
    }

    @Test
    fun `room schema stores books chapters verses and runes with relational queries`() = runBlocking {
        val db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MimirDatabase::class.java
        ).allowMainThreadQueries().build()

        try {
            val dao = db.mimirDao()
            dao.insertBooks(CorpusSeedData.books)
            dao.insertChapters(CorpusSeedData.chapters)
            dao.insertVerses(CorpusSeedData.verses)
            dao.insertRunes(CorpusSeedData.buildRuneEntities())
            dao.insertRunestoneInscriptions(CorpusSeedData.buildInscriptionEntities())

            val books = dao.observeBooks().first()
            assertTrue(books.any { it.id == "voluspa" })

            val havamalChapters = dao.observeChaptersForBook("havamal").first()
            assertTrue(havamalChapters.isNotEmpty())

            val havamalVerses = dao.observeVersesForBook("havamal").first()
            assertTrue(havamalVerses.isNotEmpty())
            assertTrue(havamalVerses.all { it.bookId == "havamal" })

            val elderRunes = dao.observeRunesByAlphabet(RuneAlphabet.ELDER_FUTHARK.name).first()
            assertEquals(24, elderRunes.size)

            val searchMatches = dao.searchVerses("Mímis").first()
            assertTrue(searchMatches.isNotEmpty())
        } finally {
            db.close()
        }
    }

    @Test
    fun `manuscript ocr service tokenizes raw norse output for translation`() {
        val tokens = ManuscriptOcrService.tokenizeForTranslation(
            "Þar er Mímisbrunnr, er spekð ok mannvit er í fólgit."
        )
        assertTrue(tokens.any { it.lemma == "Mímisbrunnr" || it.surfaceForm.equals("Mímisbrunnr", ignoreCase = true) })
        val summary = ManuscriptOcrService.formatTranslationSummary(tokens)
        assertTrue(summary.contains("->"))
    }

    @Test
    fun `elder futhark detailed transliterator maps digraphs and rune names`() {
        val tokens = RuneTransliterator.transliterateElderFutharkDetailed("Thor")
        assertEquals(3, tokens.size)
        assertEquals("ᚦ", tokens[0].glyph)
        assertEquals("Thurisaz", tokens[0].runeName)
        assertEquals("ᛟ", tokens[1].glyph)
        assertEquals("Othala", tokens[1].runeName)
        assertEquals("ᚱ", tokens[2].glyph)
        assertEquals("Raido", tokens[2].runeName)
    }

    @Test
    fun `daily reading streak calculator and datastore track streaks and goals`() = runBlocking {
        val day1 = 20000L
        val afterDay1 = DailyReadingStreakCalculator.applyReadingActivity(DailyReadingProgress(), 1, day1)
        assertEquals(1, afterDay1.currentStreakDays)
        assertEquals(1, afterDay1.todayVersesRead)

        val sameDay = DailyReadingStreakCalculator.applyReadingActivity(afterDay1, 2, day1)
        assertEquals(1, sameDay.currentStreakDays)
        assertEquals(3, sameDay.todayVersesRead)
        assertTrue(sameDay.isGoalMetToday(day1))

        val day2 = DailyReadingStreakCalculator.applyReadingActivity(sameDay, 1, day1 + 1L)
        assertEquals(2, day2.currentStreakDays)
        assertEquals(2, day2.longestStreakDays)
        assertEquals(1, day2.todayVersesRead)

        val afterGap = DailyReadingStreakCalculator.applyReadingActivity(day2, 1, day1 + 4L)
        assertEquals(1, afterGap.currentStreakDays)
        assertEquals(2, afterGap.longestStreakDays)

        val store = DailyReadingDataStore(ApplicationProvider.getApplicationContext())
        store.setDailyGoal(5)
        store.recordVerseRead(versesDelta = 2, todayEpochDay = day1)
        val stored = store.progressFlow.first()
        assertEquals(5, stored.dailyGoalVerses)
        assertTrue(stored.currentStreakDays >= 1)
        assertTrue(stored.todayVersesRead >= 2)
    }

    @Test
    fun `sm2 preview interval labels and due queue partitioning work accurately`() {
        assertEquals("1 min", SpacedRepetitionEngine.previewIntervalLabel(null, ReviewQuality.AGAIN))
        assertEquals("1 day", SpacedRepetitionEngine.previewIntervalLabel(null, ReviewQuality.GOOD))

        val first = SpacedRepetitionEngine.evaluate(null, "ef_1", ReviewQuality.GOOD, nowMillis = 1_000L)
        assertEquals("6 days", SpacedRepetitionEngine.previewIntervalLabel(first, ReviewQuality.GOOD))

        val second = SpacedRepetitionEngine.evaluate(null, "ef_2", ReviewQuality.EASY, nowMillis = 50_000L)
        val (due, upcoming) = SpacedRepetitionEngine.partitionDueIds(
            cardIds = listOf("ef_1", "ef_2", "ef_3"),
            progressMap = mapOf("ef_1" to first, "ef_2" to second),
            nowMillis = 100_000L
        )
        assertEquals(listOf("ef_3"), due)
        assertEquals(listOf("ef_1", "ef_2"), upcoming)
    }

    @Test
    fun `pro entitlement datastore persists unlock and free core revert`() = runBlocking {
        val store = ProEntitlementDataStore(ApplicationProvider.getApplicationContext())
        store.revokeProPreview()
        val initial = store.entitlementFlow.first()
        assertFalse(initial.isProUnlocked)

        store.unlockPro(ProPlanTier.LIFETIME, nowMillis = 123_456L)
        val unlocked = store.entitlementFlow.first()
        assertTrue(unlocked.isProUnlocked)
        assertEquals(ProPlanTier.LIFETIME, unlocked.activePlanTier)
        assertEquals(123_456L, unlocked.activatedEpochMillis)

        store.revokeProPreview()
        val reverted = store.entitlementFlow.first()
        assertFalse(reverted.isProUnlocked)
    }

    @Test
    fun `entitlement manager tracks isProUser subscribe and restorePurchases`() = runBlocking {
        val manager = EntitlementManager(ApplicationProvider.getApplicationContext())
        manager.revokeProPreview()
        assertFalse(manager.isProUser.first())

        manager.setProUser(enabled = true, plan = ProPlanTier.MONTHLY, nowMillis = 555L)
        assertTrue(manager.isProUser.first())
        assertEquals(ProPlanTier.MONTHLY, manager.entitlementFlow.first().activePlanTier)

        manager.revokeProPreview()
        assertFalse(manager.isProUser.first())

        val restored = manager.restorePurchases(nowMillis = 999L)
        assertTrue(restored)
        assertTrue(manager.isProUser.first())
        manager.revokeProPreview()
    }

    @Test
    fun `navigation route guard identifies pro-gated destinations`() {
        assertEquals(
            PaywallFeature.MANUSCRIPT_OCR,
            MimirRoutes.requiredPaywallFeature(MimirRoutes.STUDY, studyTab = 3, proOnly = true)
        )
        assertEquals(
            PaywallFeature.NORSE_LESSONS_PRO,
            MimirRoutes.requiredPaywallFeature(MimirRoutes.NORSE_LESSONS, proOnly = true)
        )
        assertEquals(
            PaywallFeature.UNLIMITED_SRS,
            MimirRoutes.requiredPaywallFeature(MimirRoutes.ELDER_FUTHARK_FLASHCARDS, proOnly = true)
        )
        assertNull(
            MimirRoutes.requiredPaywallFeature(MimirRoutes.STUDY, studyTab = 3, proOnly = false)
        )
    }
}
