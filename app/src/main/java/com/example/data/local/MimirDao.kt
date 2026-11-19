package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.BookEntity
import com.example.data.model.ChapterEntity
import com.example.data.model.CommentaryPackEntity
import com.example.data.model.DigitizedManuscriptEntity
import com.example.data.model.DiscussionPostEntity
import com.example.data.model.FlashcardProgressEntity
import com.example.data.model.GlossaryEntryEntity
import com.example.data.model.LessonProgressEntity
import com.example.data.model.ReadingPlanProgressEntity
import com.example.data.model.RuneEntity
import com.example.data.model.RunestoneInscriptionEntity
import com.example.data.model.UserAnnotationEntity
import com.example.data.model.VerseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MimirDao {

    // Books, Chapters & Verses
    @Query("SELECT * FROM books ORDER BY sortOrder ASC")
    fun observeBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE id = :bookId LIMIT 1")
    fun observeBook(bookId: String): Flow<BookEntity?>

    @Query("SELECT * FROM chapters WHERE bookId = :bookId ORDER BY sortOrder ASC")
    fun observeChaptersForBook(bookId: String): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM verses WHERE bookId = :bookId ORDER BY sortOrder ASC")
    fun observeVersesForBook(bookId: String): Flow<List<VerseEntity>>

    @Query("SELECT * FROM verses WHERE chapterId = :chapterId ORDER BY sortOrder ASC")
    fun observeVersesForChapter(chapterId: String): Flow<List<VerseEntity>>

    @Query("SELECT * FROM verses WHERE id = :verseId LIMIT 1")
    suspend fun getVerseById(verseId: String): VerseEntity?

    @Query("SELECT * FROM verses ORDER BY sortOrder ASC")
    fun observeAllVerses(): Flow<List<VerseEntity>>

    @Query(
        """
        SELECT * FROM verses
        WHERE oldNorseText LIKE '%' || :query || '%'
           OR englishText LIKE '%' || :query || '%'
           OR scholarlyNote LIKE '%' || :query || '%'
           OR numberLabel LIKE '%' || :query || '%'
        ORDER BY bookId ASC, sortOrder ASC
        LIMIT 80
        """
    )
    fun searchVerses(query: String): Flow<List<VerseEntity>>

    // Runic content in Room
    @Query("SELECT * FROM runes ORDER BY sortOrder ASC")
    fun observeAllRunes(): Flow<List<RuneEntity>>

    @Query("SELECT * FROM runes WHERE alphabet = :alphabet ORDER BY sortOrder ASC")
    fun observeRunesByAlphabet(alphabet: String): Flow<List<RuneEntity>>

    @Query("SELECT * FROM runestone_inscriptions ORDER BY sortOrder ASC")
    fun observeRunestoneInscriptions(): Flow<List<RunestoneInscriptionEntity>>

    // Glossary & Commentary Packs
    @Query("SELECT * FROM glossary_entries ORDER BY term ASC")
    fun observeGlossary(): Flow<List<GlossaryEntryEntity>>

    @Query(
        """
        SELECT * FROM glossary_entries
        WHERE term LIKE '%' || :query || '%'
           OR oldNorseForm LIKE '%' || :query || '%'
           OR definition LIKE '%' || :query || '%'
        ORDER BY term ASC
        """
    )
    fun searchGlossary(query: String): Flow<List<GlossaryEntryEntity>>

    @Query("SELECT * FROM commentary_packs ORDER BY isCoreRequired DESC, title ASC")
    fun observeCommentaryPacks(): Flow<List<CommentaryPackEntity>>

    @Query("UPDATE commentary_packs SET isInstalled = :installed WHERE id = :packId")
    suspend fun setPackInstalled(packId: String, installed: Boolean)

    // User Annotations
    @Query("SELECT * FROM user_annotations ORDER BY updatedAt DESC")
    fun observeAnnotations(): Flow<List<UserAnnotationEntity>>

    @Query("SELECT * FROM user_annotations WHERE workId = :bookId")
    fun observeAnnotationsForBook(bookId: String): Flow<List<UserAnnotationEntity>>

    @Query("SELECT * FROM user_annotations WHERE passageId = :verseId LIMIT 1")
    suspend fun getAnnotationForVerse(verseId: String): UserAnnotationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAnnotation(annotation: UserAnnotationEntity)

    @Query("DELETE FROM user_annotations WHERE passageId = :verseId")
    suspend fun deleteAnnotationForVerse(verseId: String)

    // Reading Plans, Flashcards, Discussions, Digitized Scans, Lessons
    @Query("SELECT * FROM reading_plan_progress")
    fun observePlanProgress(): Flow<List<ReadingPlanProgressEntity>>

    @Query("SELECT * FROM reading_plan_progress WHERE planId = :planId LIMIT 1")
    suspend fun getPlanProgress(planId: String): ReadingPlanProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPlanProgress(progress: ReadingPlanProgressEntity)

    @Query("SELECT * FROM flashcard_progress")
    fun observeFlashcardProgress(): Flow<List<FlashcardProgressEntity>>

    @Query("SELECT * FROM flashcard_progress WHERE runeId = :runeId LIMIT 1")
    suspend fun getFlashcardProgress(runeId: String): FlashcardProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFlashcardProgress(progress: FlashcardProgressEntity)

    @Query("SELECT * FROM discussion_posts WHERE passageId = :verseId ORDER BY createdAt ASC")
    fun observeDiscussionsForVerse(verseId: String): Flow<List<DiscussionPostEntity>>

    @Query("SELECT * FROM discussion_posts ORDER BY createdAt DESC")
    fun observeAllDiscussions(): Flow<List<DiscussionPostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiscussionPost(post: DiscussionPostEntity)

    @Query("SELECT * FROM digitized_manuscripts ORDER BY createdAt DESC")
    fun observeDigitizedManuscripts(): Flow<List<DigitizedManuscriptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDigitizedManuscript(scan: DigitizedManuscriptEntity)

    @Query("DELETE FROM digitized_manuscripts WHERE id = :id")
    suspend fun deleteDigitizedManuscript(id: Long)

    @Query("SELECT * FROM lesson_progress")
    fun observeLessonProgress(): Flow<List<LessonProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLessonProgress(progress: LessonProgressEntity)

    // Seeding helpers
    @Query("SELECT COUNT(*) FROM books")
    suspend fun countBooks(): Int

    @Query("SELECT COUNT(*) FROM runes")
    suspend fun countRunes(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<ChapterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerses(verses: List<VerseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRunes(runes: List<RuneEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRunestoneInscriptions(inscriptions: List<RunestoneInscriptionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGlossary(entries: List<GlossaryEntryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommentaryPacks(packs: List<CommentaryPackEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiscussionPosts(posts: List<DiscussionPostEntity>)

    // Snapshot export/import helpers
    @Query("SELECT * FROM user_annotations")
    suspend fun getAllAnnotationsOnce(): List<UserAnnotationEntity>

    @Query("SELECT * FROM reading_plan_progress")
    suspend fun getAllPlanProgressOnce(): List<ReadingPlanProgressEntity>

    @Query("SELECT * FROM flashcard_progress")
    suspend fun getAllFlashcardProgressOnce(): List<FlashcardProgressEntity>

    @Query("SELECT * FROM discussion_posts")
    suspend fun getAllDiscussionsOnce(): List<DiscussionPostEntity>

    @Query("SELECT * FROM digitized_manuscripts")
    suspend fun getAllManuscriptsOnce(): List<DigitizedManuscriptEntity>

    @Query("SELECT * FROM lesson_progress")
    suspend fun getAllLessonProgressOnce(): List<LessonProgressEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnotations(items: List<UserAnnotationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlanProgressList(items: List<ReadingPlanProgressEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcardProgressList(items: List<FlashcardProgressEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManuscripts(items: List<DigitizedManuscriptEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessonProgressList(items: List<LessonProgressEntity>)
}
