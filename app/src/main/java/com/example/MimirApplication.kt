package com.example

import android.app.Application
import com.example.data.local.DailyReadingDataStore
import com.example.data.local.EntitlementManager
import com.example.data.local.MimirDatabase
import com.example.data.local.UserPreferencesRepository
import com.example.data.repository.MimirRepository
import com.example.util.MorningReminderWorker
import com.example.widget.DailyVerseWidgetUpdater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MimirApplication : Application() {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database: MimirDatabase by lazy { MimirDatabase.getInstance(this) }
    val preferencesRepository: UserPreferencesRepository by lazy { UserPreferencesRepository(this) }
    val dailyReadingDataStore: DailyReadingDataStore by lazy { DailyReadingDataStore(this) }
    val entitlementManager: EntitlementManager by lazy { EntitlementManager(this) }
    val repository: MimirRepository by lazy { MimirRepository(database.mimirDao(), preferencesRepository) }

    override fun onCreate() {
        super.onCreate()
        MorningReminderWorker.createChannel(this)
        appScope.launch {
            repository.ensureSeeded()
            DailyVerseWidgetUpdater.updateAllWidgets(this@MimirApplication)
        }
    }
}
