package com.example.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.MainActivity
import com.example.R
import com.example.data.local.MimirDatabase
import com.example.data.local.UserPreferencesRepository
import com.example.data.repository.MimirRepository
import com.example.widget.DailyVerseWidgetUpdater
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

class MorningReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val db = MimirDatabase.getInstance(applicationContext)
        val repo = MimirRepository(db.mimirDao(), UserPreferencesRepository(applicationContext))
        repo.ensureSeeded()
        val verses = repo.observeAllVerses().first()
        val daily = repo.getDailyVerse(verses) ?: return Result.success()

        // Keep Glance home-screen widget refreshed daily
        DailyVerseWidgetUpdater.updateAllWidgets(applicationContext)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return Result.success()
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("open_work_id", daily.bookId)
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Mímir · Daily Wisdom (${daily.numberLabel})")
            .setContentText(daily.englishText.replace("\n", " "))
            .setStyle(NotificationCompat.BigTextStyle().bigText("${daily.oldNorseText}\n\n${daily.englishText}"))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        runCatching {
            NotificationManagerCompat.from(applicationContext).notify(2026, notification)
        }
        return Result.success()
    }

    companion object {
        const val CHANNEL_ID = "mimir_daily_verse"
        private const val WORK_NAME = "mimir_morning_verse_work"

        fun createChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Daily Hávamál & Saga Verse",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Optional morning stanza from Mímir's offline Eddas & Sagas corpus"
                }
                val manager = context.getSystemService(NotificationManager::class.java)
                manager?.createNotificationChannel(channel)
            }
        }

        fun schedule(context: Context, enabled: Boolean) {
            val wm = WorkManager.getInstance(context)
            if (enabled) {
                val request = PeriodicWorkRequestBuilder<MorningReminderWorker>(24, TimeUnit.HOURS)
                    .setInitialDelay(12, TimeUnit.HOURS)
                    .build()
                wm.enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.UPDATE, request)
            } else {
                wm.cancelUniqueWork(WORK_NAME)
            }
        }
    }
}
