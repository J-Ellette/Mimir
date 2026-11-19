package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.MainActivity
import com.example.data.local.MimirDatabase
import com.example.data.local.UserPreferencesRepository
import com.example.data.repository.MimirRepository
import kotlinx.coroutines.flow.first

private val KEY_WORK_ID = ActionParameters.Key<String>("open_work_id")
private val KEY_VERSE_ID = ActionParameters.Key<String>("open_verse_id")

class MimirDailyVerseWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val db = MimirDatabase.getInstance(context)
        val repo = MimirRepository(db.mimirDao(), UserPreferencesRepository(context))
        repo.ensureSeeded()
        val books = repo.observeBooks().first().associateBy { it.id }
        val verses = repo.observeAllVerses().first()
        val daily = repo.getDailyVerse(verses)

        val bookId = daily?.bookId ?: "havamal"
        val verseId = daily?.id ?: "havamal_47"
        val bookTitle = books[bookId]?.title ?: "Hávamál"
        val header = "$bookTitle · ${daily?.numberLabel ?: "Stanza 47"}"

        val oldNorseLine = daily?.oldNorseText?.lines()?.take(2)?.joinToString(" / ")
            ?: "Maðr er manns gaman."
        val englishLine = daily?.englishText?.lines()?.take(2)?.joinToString(" ")
            ?: "Man is the joy of man."

        provideContent {
            DailyVerseWidgetContent(
                header = header,
                oldNorse = oldNorseLine,
                english = englishLine,
                bookId = bookId,
                verseId = verseId
            )
        }
    }

    @Composable
    private fun DailyVerseWidgetContent(
        header: String,
        oldNorse: String,
        english: String,
        bookId: String,
        verseId: String
    ) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(Color(0xFF171513)))
                .padding(14.dp)
                .clickable(
                    actionStartActivity<MainActivity>(
                        actionParametersOf(
                            KEY_WORK_ID to bookId,
                            KEY_VERSE_ID to verseId
                        )
                    )
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MÍMIR · $header",
                    modifier = GlanceModifier.defaultWeight(),
                    style = TextStyle(
                        color = ColorProvider(Color(0xFFD4A853)),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "↻",
                    modifier = GlanceModifier
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .clickable(actionRunCallback<RefreshDailyVerseAction>()),
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF5CA4A9)),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = GlanceModifier.height(6.dp))
            Text(
                text = oldNorse,
                maxLines = 2,
                style = TextStyle(
                    color = ColorProvider(Color(0xFFF0E9DC)),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            Text(
                text = "\"$english\"",
                maxLines = 2,
                style = TextStyle(
                    color = ColorProvider(Color(0xFF9AC6C8)),
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic
                )
            )
        }
    }
}

class RefreshDailyVerseAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        MimirDailyVerseWidget().update(context, glanceId)
    }
}

class MimirDailyVerseReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MimirDailyVerseWidget()
}

object DailyVerseWidgetUpdater {
    suspend fun updateAllWidgets(context: Context) {
        runCatching {
            MimirDailyVerseWidget().updateAll(context)
        }
    }

    fun requestPinWidget(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val provider = ComponentName(context, MimirDailyVerseReceiver::class.java)
        if (!appWidgetManager.isRequestPinAppWidgetSupported) return false
        val callbackIntent = Intent(context, MimirDailyVerseReceiver::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        }
        val successCallback = PendingIntent.getBroadcast(
            context,
            0,
            callbackIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return appWidgetManager.requestPinAppWidget(provider, null, successCallback)
    }
}
