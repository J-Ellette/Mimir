package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

private val Context.dailyReadingDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "mimir_daily_reading"
)

data class DailyReadingProgress(
    val currentStreakDays: Int = 0,
    val longestStreakDays: Int = 0,
    val lastReadEpochDay: Long = -1L,
    val todayVersesRead: Int = 0,
    val dailyGoalVerses: Int = 3,
    val totalDaysRead: Int = 0,
    val completedEpochDaysCsv: String = ""
) {
    fun effectiveTodayVerses(todayEpochDay: Long = LocalDate.now().toEpochDay()): Int =
        if (lastReadEpochDay == todayEpochDay) todayVersesRead else 0

    fun isGoalMetToday(todayEpochDay: Long = LocalDate.now().toEpochDay()): Boolean =
        effectiveTodayVerses(todayEpochDay) >= dailyGoalVerses

    fun effectiveCurrentStreak(todayEpochDay: Long = LocalDate.now().toEpochDay()): Int {
        if (lastReadEpochDay < 0L) return 0
        return if (lastReadEpochDay == todayEpochDay || lastReadEpochDay == todayEpochDay - 1L) {
            currentStreakDays
        } else {
            0
        }
    }

    fun recentSevenDaysStatus(todayEpochDay: Long = LocalDate.now().toEpochDay()): List<Boolean> {
        val activeDays = completedEpochDaysCsv
            .split(",")
            .mapNotNull { it.trim().toLongOrNull() }
            .toSet()
        return (6 downTo 0).map { offset ->
            activeDays.contains(todayEpochDay - offset)
        }
    }
}

object DailyReadingStreakCalculator {
    fun applyReadingActivity(
        current: DailyReadingProgress,
        versesDelta: Int,
        todayEpochDay: Long
    ): DailyReadingProgress {
        val safeDelta = versesDelta.coerceAtLeast(1)
        val sameDay = current.lastReadEpochDay == todayEpochDay
        val newTodayVerses = if (sameDay) {
            current.todayVersesRead + safeDelta
        } else {
            safeDelta
        }

        val newStreak = when {
            sameDay -> current.currentStreakDays.coerceAtLeast(1)
            current.lastReadEpochDay == todayEpochDay - 1L -> current.currentStreakDays + 1
            else -> 1
        }
        val newLongest = maxOf(current.longestStreakDays, newStreak)

        val daySet = current.completedEpochDaysCsv
            .split(",")
            .mapNotNull { it.trim().toLongOrNull() }
            .toMutableSet()
        val isNewDay = daySet.add(todayEpochDay)
        val trimmedCsv = daySet.sorted().takeLast(30).joinToString(",")
        val newTotalDays = if (isNewDay) {
            maxOf(current.totalDaysRead + 1, daySet.size)
        } else {
            maxOf(current.totalDaysRead, daySet.size)
        }

        return current.copy(
            currentStreakDays = newStreak,
            longestStreakDays = newLongest,
            lastReadEpochDay = todayEpochDay,
            todayVersesRead = newTodayVerses,
            totalDaysRead = newTotalDays,
            completedEpochDaysCsv = trimmedCsv
        )
    }
}

class DailyReadingDataStore(private val context: Context) {

    private object Keys {
        val CURRENT_STREAK = intPreferencesKey("current_streak_days")
        val LONGEST_STREAK = intPreferencesKey("longest_streak_days")
        val LAST_READ_EPOCH_DAY = longPreferencesKey("last_read_epoch_day")
        val TODAY_VERSES_READ = intPreferencesKey("today_verses_read")
        val DAILY_GOAL_VERSES = intPreferencesKey("daily_goal_verses")
        val TOTAL_DAYS_READ = intPreferencesKey("total_days_read")
        val COMPLETED_DAYS_CSV = stringPreferencesKey("completed_epoch_days_csv")
    }

    val progressFlow: Flow<DailyReadingProgress> = context.dailyReadingDataStore.data.map { prefs ->
        DailyReadingProgress(
            currentStreakDays = prefs[Keys.CURRENT_STREAK] ?: 0,
            longestStreakDays = prefs[Keys.LONGEST_STREAK] ?: 0,
            lastReadEpochDay = prefs[Keys.LAST_READ_EPOCH_DAY] ?: -1L,
            todayVersesRead = prefs[Keys.TODAY_VERSES_READ] ?: 0,
            dailyGoalVerses = (prefs[Keys.DAILY_GOAL_VERSES] ?: 3).coerceIn(1, 20),
            totalDaysRead = prefs[Keys.TOTAL_DAYS_READ] ?: 0,
            completedEpochDaysCsv = prefs[Keys.COMPLETED_DAYS_CSV] ?: ""
        )
    }

    suspend fun recordVerseRead(
        versesDelta: Int = 1,
        todayEpochDay: Long = LocalDate.now().toEpochDay()
    ) {
        context.dailyReadingDataStore.edit { prefs ->
            val current = DailyReadingProgress(
                currentStreakDays = prefs[Keys.CURRENT_STREAK] ?: 0,
                longestStreakDays = prefs[Keys.LONGEST_STREAK] ?: 0,
                lastReadEpochDay = prefs[Keys.LAST_READ_EPOCH_DAY] ?: -1L,
                todayVersesRead = prefs[Keys.TODAY_VERSES_READ] ?: 0,
                dailyGoalVerses = (prefs[Keys.DAILY_GOAL_VERSES] ?: 3).coerceIn(1, 20),
                totalDaysRead = prefs[Keys.TOTAL_DAYS_READ] ?: 0,
                completedEpochDaysCsv = prefs[Keys.COMPLETED_DAYS_CSV] ?: ""
            )
            val updated = DailyReadingStreakCalculator.applyReadingActivity(
                current = current,
                versesDelta = versesDelta,
                todayEpochDay = todayEpochDay
            )
            prefs[Keys.CURRENT_STREAK] = updated.currentStreakDays
            prefs[Keys.LONGEST_STREAK] = updated.longestStreakDays
            prefs[Keys.LAST_READ_EPOCH_DAY] = updated.lastReadEpochDay
            prefs[Keys.TODAY_VERSES_READ] = updated.todayVersesRead
            prefs[Keys.TOTAL_DAYS_READ] = updated.totalDaysRead
            prefs[Keys.COMPLETED_DAYS_CSV] = updated.completedEpochDaysCsv
        }
    }

    suspend fun markTodayCompleted(todayEpochDay: Long = LocalDate.now().toEpochDay()) {
        context.dailyReadingDataStore.edit { prefs ->
            val goal = (prefs[Keys.DAILY_GOAL_VERSES] ?: 3).coerceIn(1, 20)
            val lastDay = prefs[Keys.LAST_READ_EPOCH_DAY] ?: -1L
            val existingToday = if (lastDay == todayEpochDay) (prefs[Keys.TODAY_VERSES_READ] ?: 0) else 0
            val delta = (goal - existingToday).coerceAtLeast(1)
            val current = DailyReadingProgress(
                currentStreakDays = prefs[Keys.CURRENT_STREAK] ?: 0,
                longestStreakDays = prefs[Keys.LONGEST_STREAK] ?: 0,
                lastReadEpochDay = lastDay,
                todayVersesRead = existingToday,
                dailyGoalVerses = goal,
                totalDaysRead = prefs[Keys.TOTAL_DAYS_READ] ?: 0,
                completedEpochDaysCsv = prefs[Keys.COMPLETED_DAYS_CSV] ?: ""
            )
            val updated = DailyReadingStreakCalculator.applyReadingActivity(
                current = current,
                versesDelta = delta,
                todayEpochDay = todayEpochDay
            )
            prefs[Keys.CURRENT_STREAK] = updated.currentStreakDays
            prefs[Keys.LONGEST_STREAK] = updated.longestStreakDays
            prefs[Keys.LAST_READ_EPOCH_DAY] = updated.lastReadEpochDay
            prefs[Keys.TODAY_VERSES_READ] = updated.todayVersesRead
            prefs[Keys.TOTAL_DAYS_READ] = updated.totalDaysRead
            prefs[Keys.COMPLETED_DAYS_CSV] = updated.completedEpochDaysCsv
        }
    }

    suspend fun setDailyGoal(goalVerses: Int) {
        context.dailyReadingDataStore.edit { prefs ->
            prefs[Keys.DAILY_GOAL_VERSES] = goalVerses.coerceIn(1, 20)
        }
    }
}
