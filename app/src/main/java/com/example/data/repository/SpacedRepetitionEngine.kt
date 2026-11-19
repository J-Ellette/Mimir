package com.example.data.repository

import com.example.data.model.FlashcardProgressEntity
import kotlin.math.max
import kotlin.math.roundToInt

enum class ReviewQuality(val score: Int, val label: String) {
    AGAIN(1, "Again"),
    HARD(3, "Hard"),
    GOOD(4, "Good"),
    EASY(5, "Easy")
}

object SpacedRepetitionEngine {
    private const val DAY_MILLIS = 86_400_000L

    fun evaluate(
        current: FlashcardProgressEntity?,
        runeId: String,
        quality: ReviewQuality,
        nowMillis: Long = System.currentTimeMillis()
    ): FlashcardProgressEntity {
        val prevEase = current?.easeFactor ?: 2.5f
        val prevReps = current?.repetitions ?: 0
        val prevInterval = current?.intervalDays ?: 0
        val prevCorrect = current?.totalCorrect ?: 0
        val prevAttempts = current?.totalAttempts ?: 0

        val q = quality.score
        val newEase = max(
            1.3f,
            prevEase + (0.1f - (5 - q) * (0.08f + (5 - q) * 0.02f))
        )

        val (newReps, newInterval) = if (q < 3) {
            0 to 1
        } else {
            val reps = prevReps + 1
            val interval = when (reps) {
                1 -> 1
                2 -> 6
                else -> (prevInterval * newEase).roundToInt().coerceAtLeast(1)
            }
            reps to interval
        }

        val nextDue = if (q < 3) {
            nowMillis + 60_000L // Review again in 1 minute
        } else {
            nowMillis + newInterval * DAY_MILLIS
        }

        return FlashcardProgressEntity(
            runeId = runeId,
            easeFactor = newEase,
            intervalDays = newInterval,
            repetitions = newReps,
            nextReviewMillis = nextDue,
            totalCorrect = prevCorrect + if (q >= 3) 1 else 0,
            totalAttempts = prevAttempts + 1
        )
    }

    fun previewIntervalLabel(
        current: FlashcardProgressEntity?,
        quality: ReviewQuality
    ): String {
        if (quality.score < 3) return "1 min"
        val projected = evaluate(
            current = current,
            runeId = current?.runeId ?: "preview",
            quality = quality,
            nowMillis = 0L
        )
        val days = projected.intervalDays
        return when {
            days == 1 -> "1 day"
            days < 30 -> "$days days"
            else -> "${days / 30} mo"
        }
    }

    fun partitionDueIds(
        cardIds: List<String>,
        progressMap: Map<String, FlashcardProgressEntity>,
        nowMillis: Long = System.currentTimeMillis()
    ): Pair<List<String>, List<String>> {
        val due = cardIds.filter { id ->
            val p = progressMap[id]
            p == null || p.nextReviewMillis <= nowMillis
        }.sortedWith(
            compareBy<String>(
                { progressMap[it]?.nextReviewMillis ?: 0L },
                { cardIds.indexOf(it) }
            )
        )
        val upcoming = cardIds.filter { id ->
            val p = progressMap[id]
            p != null && p.nextReviewMillis > nowMillis
        }.sortedBy { progressMap[it]?.nextReviewMillis ?: Long.MAX_VALUE }
        return due to upcoming
    }
}
