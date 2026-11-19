package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class WorkCategory(val label: String) {
    POETIC_EDDA("Poetic Edda"),
    PROSE_EDDA("Prose Edda"),
    FAMILY_SAGA("Icelandic Family Sagas"),
    LEGENDARY_SAGA("Legendary Sagas"),
    HEIMSKRINGLA("Heimskringla")
}

enum class GlossaryCategory(val label: String) {
    GOD("Gods & Spirits"),
    GIANT("Jötnar & Monsters"),
    PLACE("Realms & Places"),
    KENNING("Kennings")
}

enum class HighlightColor(val label: String) {
    NONE("None"),
    GOLD("Amber Gold"),
    TEAL("Well Teal"),
    RUST("Birch Rust")
}

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val id: String,
    val title: String,
    val oldNorseTitle: String,
    val category: WorkCategory,
    val translator: String,
    val licenseNote: String,
    val summary: String,
    val manuscriptSource: String,
    val sortOrder: Int
)

@Entity(
    tableName = "chapters",
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("bookId")]
)
data class ChapterEntity(
    @PrimaryKey val id: String,
    val bookId: String,
    val chapterNumber: Int,
    val title: String,
    val oldNorseHeading: String,
    val summary: String,
    val sortOrder: Int
)

@Entity(
    tableName = "verses",
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ChapterEntity::class,
            parentColumns = ["id"],
            childColumns = ["chapterId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("bookId"), Index("chapterId")]
)
data class VerseEntity(
    @PrimaryKey val id: String,
    val bookId: String,
    val chapterId: String,
    val verseNumber: Int,
    val numberLabel: String,
    val oldNorseText: String,
    val englishText: String,
    val isPrimaryAttested: Boolean,
    val attestationSummary: String,
    val scholarlyNote: String,
    val linguisticPackNote: String,
    val kenningPackNote: String,
    val runologyPackNote: String,
    val sortOrder: Int
)

@Entity(
    tableName = "runes",
    indices = [Index("alphabet")]
)
data class RuneEntity(
    @PrimaryKey val id: String,
    val alphabet: String,
    val glyph: String,
    val name: String,
    val soundValue: String,
    val meaning: String,
    val aett: String,
    val lore: String,
    val sortOrder: Int
)

@Entity(tableName = "runestone_inscriptions")
data class RunestoneInscriptionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val locationAndDate: String,
    val alphabetUsed: String,
    val overview: String,
    val sortOrder: Int
)

@Entity(tableName = "glossary_entries")
data class GlossaryEntryEntity(
    @PrimaryKey val id: String,
    val term: String,
    val oldNorseForm: String,
    val category: GlossaryCategory,
    val definition: String,
    val primarySource: String,
    val sourceDistinctionNote: String
)

@Entity(tableName = "commentary_packs")
data class CommentaryPackEntity(
    @PrimaryKey val id: String,
    val title: String,
    val editionSubtitle: String,
    val description: String,
    val sizeLabel: String,
    val isInstalled: Boolean,
    val isCoreRequired: Boolean,
    val isLicensedPreview: Boolean
)

@Entity(
    tableName = "user_annotations",
    indices = [Index("passageId", unique = true)]
)
data class UserAnnotationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val passageId: String,
    val workId: String,
    val highlightColor: HighlightColor = HighlightColor.NONE,
    val isBookmarked: Boolean = false,
    val noteText: String = "",
    val isPrivateVault: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reading_plan_progress")
data class ReadingPlanProgressEntity(
    @PrimaryKey val planId: String,
    val completedDaysCsv: String = "",
    val reflectionsSummary: String = "",
    val lastCompletedEpochDay: Long = 0L,
    val streakDays: Int = 0
)

@Entity(tableName = "flashcard_progress")
data class FlashcardProgressEntity(
    @PrimaryKey val runeId: String,
    val easeFactor: Float = 2.5f,
    val intervalDays: Int = 0,
    val repetitions: Int = 0,
    val nextReviewMillis: Long = 0L,
    val totalCorrect: Int = 0,
    val totalAttempts: Int = 0
)

@Entity(
    tableName = "discussion_posts",
    indices = [Index("passageId")]
)
data class DiscussionPostEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val passageId: String,
    val authorName: String,
    val stanceTag: String,
    val body: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "digitized_manuscripts")
data class DigitizedManuscriptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val extractedText: String,
    val matchedLexiconNotes: String,
    val isPrivateVault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "lesson_progress")
data class LessonProgressEntity(
    @PrimaryKey val lessonId: String,
    val isCompleted: Boolean = false,
    val bestScore: Int = 0,
    val xpEarned: Int = 0
)
