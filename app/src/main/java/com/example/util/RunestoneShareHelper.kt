package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.example.R
import java.io.File
import java.io.FileOutputStream

object RunestoneShareHelper {

    fun shareRunestoneCard(
        context: Context,
        title: String,
        oldNorse: String,
        english: String,
        citation: String,
        knotworkStyle: Boolean = false
    ) {
        val bitmap = renderRunestoneCardBitmap(context, title, oldNorse, english, citation, knotworkStyle)
        val dir = File(context.cacheDir, "shared_cards").apply { mkdirs() }
        val file = File(dir, "mimir_verse_card.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 95, out)
        }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "$oldNorse\n\n\"$english\"\n— $citation (Mímir)")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(sendIntent, "Share Runestone Card"))
    }

    private fun renderRunestoneCardBitmap(
        context: Context,
        title: String,
        oldNorse: String,
        english: String,
        citation: String,
        knotworkStyle: Boolean
    ): Bitmap {
        val width = 1080
        val height = 1080
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)

        // Draw runestone texture backdrop
        val bgRaw = BitmapFactory.decodeResource(context.resources, R.drawable.img_runestone_card)
        if (bgRaw != null) {
            canvas.drawBitmap(bgRaw, null, Rect(0, 0, width, height), null)
        } else {
            canvas.drawColor(Color.parseColor("#181614"))
        }

        // Subtle dark readability scrim in center
        val scrimPaint = Paint().apply {
            color = if (knotworkStyle) Color.argb(190, 20, 26, 28) else Color.argb(180, 18, 16, 14)
            isAntiAlias = true
        }
        canvas.drawRoundRect(RectF(84f, 84f, width - 84f, height - 84f), 36f, 36f, scrimPaint)

        // Carved gold border
        val borderPaint = Paint().apply {
            color = Color.parseColor("#D4A853")
            style = Paint.Style.STROKE
            strokeWidth = 4f
            isAntiAlias = true
        }
        canvas.drawRoundRect(RectF(96f, 96f, width - 96f, height - 96f), 28f, 28f, borderPaint)

        val displayFace = ResourcesCompat.getFont(context, R.font.cinzel) ?: Typeface.SERIF
        val serifFace = ResourcesCompat.getFont(context, R.font.cormorant_garamond) ?: Typeface.SERIF

        val headerPaint = TextPaint().apply {
            color = Color.parseColor("#D4A853")
            textSize = 36f
            typeface = displayFace
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("MÍMIR · $title".uppercase(), width / 2f, 175f, headerPaint)

        val norsePaint = TextPaint().apply {
            color = Color.parseColor("#F4EFE6")
            textSize = 44f
            typeface = serifFace
            isAntiAlias = true
        }
        val norseLayout = StaticLayout.Builder
            .obtain(oldNorse, 0, oldNorse.length, norsePaint, width - 260)
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .setLineSpacing(10f, 1f)
            .build()
        canvas.save()
        canvas.translate(130f, 230f)
        norseLayout.draw(canvas)
        canvas.restore()

        val englishPaint = TextPaint().apply {
            color = Color.parseColor("#C9D6D5")
            textSize = 38f
            typeface = Typeface.create(serifFace, Typeface.ITALIC)
            isAntiAlias = true
        }
        val engY = 250f + norseLayout.height + 48f
        val engLayout = StaticLayout.Builder
            .obtain(english, 0, english.length, englishPaint, width - 260)
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .setLineSpacing(8f, 1f)
            .build()
        canvas.save()
        canvas.translate(130f, engY)
        engLayout.draw(canvas)
        canvas.restore()

        val footerPaint = TextPaint().apply {
            color = Color.parseColor("#5CA4A9")
            textSize = 30f
            typeface = displayFace
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(citation, width / 2f, height - 138f, footerPaint)

        return bmp
    }
}
