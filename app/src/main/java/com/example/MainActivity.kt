package com.example

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MimirViewModel
import com.example.ui.navigation.MimirApp
import com.example.ui.theme.MimirTheme

class MainActivity : FragmentActivity() {
    private val viewModel: MimirViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val initialWorkId = intent?.getStringExtra("open_work_id")
        val initialVerseId = intent?.getStringExtra("open_verse_id")

        setContent {
            val settings by viewModel.settings.collectAsStateWithLifecycle()
            MimirTheme(themeMode = settings.themeMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.safeDrawing)
                    ) {
                        MimirApp(
                            viewModel = viewModel,
                            activity = this@MainActivity,
                            initialWorkId = initialWorkId,
                            initialVerseId = initialVerseId
                        )
                    }
                }
            }
        }
    }
}
