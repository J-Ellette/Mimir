package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.data.model.BookEntity
import com.example.data.model.ChapterEntity
import com.example.data.model.CommentaryPackEntity
import com.example.data.model.DigitizedManuscriptEntity
import com.example.data.model.DiscussionPostEntity
import com.example.data.model.FlashcardProgressEntity
import com.example.data.model.GlossaryCategory
import com.example.data.model.GlossaryEntryEntity
import com.example.data.model.HighlightColor
import com.example.data.model.LessonProgressEntity
import com.example.data.model.ReadingPlanProgressEntity
import com.example.data.model.RuneEntity
import com.example.data.model.RunestoneInscriptionEntity
import com.example.data.model.UserAnnotationEntity
import com.example.data.model.VerseEntity
import com.example.data.model.WorkCategory

class MimirConverters {
    @TypeConverter fun fromWorkCategory(v: WorkCategory): String = v.name
    @TypeConverter fun toWorkCategory(v: String): WorkCategory = WorkCategory.valueOf(v)

    @TypeConverter fun fromGlossaryCategory(v: GlossaryCategory): String = v.name
    @TypeConverter fun toGlossaryCategory(v: String): GlossaryCategory = GlossaryCategory.valueOf(v)

    @TypeConverter fun fromHighlightColor(v: HighlightColor): String = v.name
    @TypeConverter fun toHighlightColor(v: String): HighlightColor = HighlightColor.valueOf(v)
}

@Database(
    entities = [
        BookEntity::class,
        ChapterEntity::class,
        VerseEntity::class,
        RuneEntity::class,
        RunestoneInscriptionEntity::class,
        GlossaryEntryEntity::class,
        CommentaryPackEntity::class,
        UserAnnotationEntity::class,
        ReadingPlanProgressEntity::class,
        FlashcardProgressEntity::class,
        DiscussionPostEntity::class,
        DigitizedManuscriptEntity::class,
        LessonProgressEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(MimirConverters::class)
abstract class MimirDatabase : RoomDatabase() {
    abstract fun mimirDao(): MimirDao

    companion object {
        @Volatile
        private var INSTANCE: MimirDatabase? = null

        fun getInstance(context: Context): MimirDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MimirDatabase::class.java,
                    "mimir_corpus.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
