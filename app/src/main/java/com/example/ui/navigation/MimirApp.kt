package com.example.ui.navigation

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.HistoryEdu
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.local.PaywallFeature
import com.example.ui.MimirViewModel
import com.example.ui.components.MimirProPaywallSheet
import com.example.ui.screens.ElderFutharkFlashcardsScreen
import com.example.ui.screens.ElderFutharkTransliteratorScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.NorseLessonsScreen
import com.example.ui.screens.PlansAndCalendarScreen
import com.example.ui.screens.ReaderScreen
import com.example.ui.screens.RunesScreen
import com.example.ui.screens.SettingsAndVaultScreen
import com.example.ui.screens.StudyToolsScreen
import com.example.util.BiometricHelper

object MimirRoutes {
    const val HOME = "home"
    const val LIBRARY = "library"
    const val RUNES = "runes"
    const val STUDY = "study"
    const val PLANS = "plans"
    const val READER = "reader/{workId}?passageId={passageId}"
    const val NORSE_LESSONS = "norse_lessons"
    const val ELDER_FUTHARK_TRANSLITERATOR = "elder_futhark_transliterator"
    const val ELDER_FUTHARK_FLASHCARDS = "elder_futhark_flashcards"
    const val PRO_OCR_STUDY_TOOL = "study?tab=3&proOnly=true"
    const val SETTINGS_VAULT = "settings_vault"

    fun reader(workId: String, passageId: String? = null): String =
        if (passageId != null) "reader/$workId?passageId=$passageId" else "reader/$workId"

    fun studyTab(tab: Int, proOnly: Boolean = false): String = "$STUDY?tab=$tab&proOnly=$proOnly"
    fun plansTab(tab: Int): String = "$PLANS?tab=$tab"

    /**
     * Navigation-level entitlement policy for restricted destinations.
     */
    fun requiredPaywallFeature(route: String?, studyTab: Int = 0, proOnly: Boolean = false): PaywallFeature? {
        if (route == null) return null
        return when {
            proOnly && route.startsWith(STUDY) && studyTab == 3 -> PaywallFeature.MANUSCRIPT_OCR
            proOnly && route.startsWith(NORSE_LESSONS) -> PaywallFeature.NORSE_LESSONS_PRO
            proOnly && route.startsWith(ELDER_FUTHARK_FLASHCARDS) -> PaywallFeature.UNLIMITED_SRS
            else -> null
        }
    }
}

private data class TopDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val testTag: String
)

private val topDestinations = listOf(
    TopDestination(MimirRoutes.HOME, "Well", Icons.Outlined.WaterDrop, "nav_well"),
    TopDestination(MimirRoutes.LIBRARY, "Library", Icons.AutoMirrored.Outlined.MenuBook, "nav_library"),
    TopDestination(MimirRoutes.RUNES, "Runes", Icons.Outlined.HistoryEdu, "nav_runes"),
    TopDestination(MimirRoutes.STUDY, "Study", Icons.Outlined.Explore, "nav_study"),
    TopDestination(MimirRoutes.PLANS, "Paths", Icons.Outlined.CalendarMonth, "nav_paths")
)

@Composable
fun MimirApp(
    viewModel: MimirViewModel,
    activity: FragmentActivity,
    initialWorkId: String? = null,
    initialVerseId: String? = null
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val isProUser by viewModel.isProUser.collectAsStateWithLifecycle()
    val proEntitlement by viewModel.proEntitlement.collectAsStateWithLifecycle()
    val paywallTrigger by viewModel.paywallTrigger.collectAsStateWithLifecycle()
    val dailyReadingProgress by viewModel.dailyReadingProgress.collectAsStateWithLifecycle()
    val books by viewModel.books.collectAsStateWithLifecycle()
    val allVerses by viewModel.allVerses.collectAsStateWithLifecycle()
    val allRunes by viewModel.allRunes.collectAsStateWithLifecycle()
    val dailyVerse by viewModel.dailyVerse.collectAsStateWithLifecycle()
    val glossary by viewModel.glossary.collectAsStateWithLifecycle()
    val commentaryPacks by viewModel.commentaryPacks.collectAsStateWithLifecycle()
    val annotations by viewModel.annotations.collectAsStateWithLifecycle()
    val planProgress by viewModel.planProgress.collectAsStateWithLifecycle()
    val flashcardProgress by viewModel.flashcardProgress.collectAsStateWithLifecycle()
    val allDiscussions by viewModel.allDiscussions.collectAsStateWithLifecycle()
    val manuscripts by viewModel.digitizedManuscripts.collectAsStateWithLifecycle()
    val lessonProgress by viewModel.lessonProgress.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val vaultUnlocked by viewModel.vaultUnlocked.collectAsStateWithLifecycle()
    val statusBanner by viewModel.statusBanner.collectAsStateWithLifecycle()
    val ocrProcessing by viewModel.ocrProcessing.collectAsStateWithLifecycle()

    LaunchedEffect(statusBanner) {
        val msg = statusBanner
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearStatusBanner()
        }
    }

    LaunchedEffect(initialWorkId, initialVerseId) {
        if (!initialWorkId.isNullOrBlank()) {
            navController.navigate(MimirRoutes.reader(initialWorkId, initialVerseId))
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: MimirRoutes.HOME
    val showTopLevelNav = topDestinations.any { currentRoute.startsWith(it.route) }

    val unlockVault = {
        if (BiometricHelper.canAuthenticate(activity)) {
            BiometricHelper.authenticate(
                activity = activity,
                title = "Unlock Mímir Vault",
                subtitle = "Verify identity to view private study notes and digitized manuscripts",
                onSuccess = { viewModel.setVaultUnlocked(true) },
                onError = { /* handled gracefully */ }
            )
        } else {
            viewModel.setVaultUnlocked(true)
        }
    }

    val openPassageById: (String) -> Unit = { verseId ->
        val found = allVerses.firstOrNull { it.id == verseId }
        val targetBook = found?.bookId ?: "voluspa"
        navController.navigate(MimirRoutes.reader(targetBook, verseId))
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useRail = maxWidth >= 600.dp

        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                if (showTopLevelNav && !useRail) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        topDestinations.forEach { dest ->
                            val selected = currentRoute.startsWith(dest.route)
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(dest.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(dest.icon, contentDescription = dest.label) },
                                label = { Text(dest.label) },
                                modifier = Modifier.testTag(dest.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (showTopLevelNav && useRail) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        topDestinations.forEach { dest ->
                            val selected = currentRoute.startsWith(dest.route)
                            NavigationRailItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(dest.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(dest.icon, contentDescription = dest.label) },
                                label = { Text(dest.label) },
                                modifier = Modifier.testTag(dest.testTag)
                            )
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = MimirRoutes.HOME,
                    modifier = Modifier.weight(1f)
                ) {
                    composable(MimirRoutes.HOME) {
                        HomeScreen(
                            settings = settings,
                            isProUnlocked = isProUser,
                            dailyReadingProgress = dailyReadingProgress,
                            dailyVerse = dailyVerse,
                            books = books,
                            planProgress = planProgress,
                            flashcardProgress = flashcardProgress,
                            todayIcelandic = viewModel.todayIcelandicDate,
                            onToggleDarkMode = viewModel::toggleDarkMode,
                            onOpenSettings = { navController.navigate(MimirRoutes.SETTINGS_VAULT) },
                            onOpenReader = { bId, vId -> navController.navigate(MimirRoutes.reader(bId, vId)) },
                            onOpenNorseLessons = { navController.navigate(MimirRoutes.NORSE_LESSONS) },
                            onOpenOcrScanner = { navController.navigate(MimirRoutes.studyTab(3)) },
                            onOpenYggdrasil = { navController.navigate(MimirRoutes.studyTab(1)) },
                            onOpenElderFutharkTransliterator = { navController.navigate(MimirRoutes.ELDER_FUTHARK_TRANSLITERATOR) },
                            onOpenElderFutharkFlashcards = { navController.navigate(MimirRoutes.ELDER_FUTHARK_FLASHCARDS) },
                            onOpenCalendar = { navController.navigate(MimirRoutes.plansTab(1)) },
                            onOpenPlans = { navController.navigate(MimirRoutes.plansTab(0)) },
                            onOpenPaywall = viewModel::openPaywall,
                            onLogDailyReading = { viewModel.logDailyReadingVerse(1) },
                            onSpeakOldNorse = { text ->
                                if (isProUser) {
                                    viewModel.audioPlayer.speak(text, isModernIcelandic = false)
                                } else {
                                    viewModel.openPaywall(PaywallFeature.AUDIO_PRONUNCIATION)
                                }
                            },
                            onShowMessage = viewModel::showMessage
                        )
                    }

                    composable(MimirRoutes.LIBRARY) {
                        LibraryScreen(
                            books = books,
                            searchQuery = searchQuery,
                            searchResults = searchResults,
                            annotations = annotations,
                            commentaryPacks = commentaryPacks,
                            isProUnlocked = isProUser,
                            onSearchQueryChange = viewModel::updateSearchQuery,
                            onTogglePackInstalled = viewModel::toggleCommentaryPack,
                            onOpenBook = { bId, vId -> navController.navigate(MimirRoutes.reader(bId, vId)) },
                            onRequestPaywall = viewModel::openPaywall
                        )
                    }

                    composable(MimirRoutes.RUNES) {
                        RunesScreen(
                            roomRunes = allRunes,
                            flashcardProgress = flashcardProgress,
                            isProUnlocked = isProUser,
                            onRecordFlashcard = viewModel::recordFlashcard,
                            onOpenElderFutharkTransliterator = { navController.navigate(MimirRoutes.ELDER_FUTHARK_TRANSLITERATOR) },
                            onOpenElderFutharkFlashcards = { navController.navigate(MimirRoutes.ELDER_FUTHARK_FLASHCARDS) },
                            onRequestPaywall = viewModel::openPaywall
                        )
                    }

                    composable(
                        route = "${MimirRoutes.STUDY}?tab={tab}&proOnly={proOnly}",
                        arguments = listOf(
                            navArgument("tab") { type = NavType.IntType; defaultValue = 0 },
                            navArgument("proOnly") { type = NavType.BoolType; defaultValue = false }
                        )
                    ) { backStackEntry ->
                        val initialTab = backStackEntry.arguments?.getInt("tab") ?: 0
                        val proOnly = backStackEntry.arguments?.getBoolean("proOnly") ?: false
                        val requiredGate = MimirRoutes.requiredPaywallFeature(
                            route = backStackEntry.destination.route,
                            studyTab = initialTab,
                            proOnly = proOnly
                        )
                        LaunchedEffect(requiredGate, isProUser) {
                            if (requiredGate != null && !isProUser) {
                                viewModel.openPaywall(requiredGate)
                                navController.popBackStack()
                            }
                        }
                        StudyToolsScreen(
                            initialTab = initialTab,
                            glossary = glossary,
                            manuscripts = manuscripts,
                            ocrProcessing = ocrProcessing,
                            vaultUnlocked = vaultUnlocked,
                            biometricVaultEnabled = settings.biometricVaultEnabled,
                            isProUnlocked = isProUser,
                            onUnlockVaultRequest = unlockVault,
                            onProcessOcrUri = { uri, title, isPriv, cb ->
                                viewModel.processManuscriptOcr(activity, uri, title, isPriv, cb)
                            },
                            onProcessCameraFrame = { imageProxy, title, isPriv, cb ->
                                viewModel.processManuscriptCameraFrame(imageProxy, title, isPriv, cb)
                            },
                            onSaveManualManuscript = viewModel::saveManualManuscript,
                            onDeleteManuscript = viewModel::deleteManuscript,
                            onOpenPassage = openPassageById,
                            onShowMessage = viewModel::showMessage,
                            onRequestPaywall = viewModel::openPaywall
                        )
                    }

                    composable(
                        route = "${MimirRoutes.PLANS}?tab={tab}",
                        arguments = listOf(navArgument("tab") { type = NavType.IntType; defaultValue = 0 })
                    ) { backStackEntry ->
                        val initialTab = backStackEntry.arguments?.getInt("tab") ?: 0
                        PlansAndCalendarScreen(
                            initialTab = initialTab,
                            planProgress = planProgress,
                            onTogglePlanDay = viewModel::togglePlanDay,
                            onOpenReader = { wId, pId -> navController.navigate(MimirRoutes.reader(wId, pId)) }
                        )
                    }

                    composable(
                        route = MimirRoutes.READER,
                        arguments = listOf(
                            navArgument("workId") { type = NavType.StringType },
                            navArgument("passageId") { type = NavType.StringType; nullable = true; defaultValue = null }
                        )
                    ) { backStackEntry ->
                        val bookId = backStackEntry.arguments?.getString("workId") ?: "voluspa"
                        val verseId = backStackEntry.arguments?.getString("passageId")
                        val book by viewModel.observeBook(bookId).collectAsStateWithLifecycle(initialValue = null)
                        val chapters by viewModel.observeChaptersForBook(bookId).collectAsStateWithLifecycle(initialValue = emptyList())
                        val verses by viewModel.observeVersesForBook(bookId).collectAsStateWithLifecycle(initialValue = emptyList())

                        LaunchedEffect(bookId) {
                            viewModel.logDailyReadingVerse(1, showBanner = false)
                        }

                        ReaderScreen(
                            book = book,
                            chapters = chapters,
                            verses = verses,
                            targetVerseId = verseId,
                            settings = settings,
                            annotations = annotations,
                            glossary = glossary,
                            commentaryPacks = commentaryPacks,
                            discussions = allDiscussions,
                            onBack = { navController.popBackStack() },
                            onToggleParallelMode = viewModel::setParallelSideBySide,
                            onChangeFontScale = viewModel::setFontScale,
                            onSetHighlight = viewModel::setHighlight,
                            onToggleBookmark = viewModel::toggleBookmark,
                            onSaveNote = viewModel::saveVerseNote,
                            onPostDiscussion = viewModel::postDiscussion,
                            onSpeakOldNorse = { text ->
                                if (isProUser) {
                                    viewModel.audioPlayer.speak(text, isModernIcelandic = false)
                                } else {
                                    viewModel.openPaywall(PaywallFeature.AUDIO_PRONUNCIATION)
                                }
                            }
                        )
                    }

                    composable(MimirRoutes.NORSE_LESSONS) {
                        NorseLessonsScreen(
                            lessonProgress = lessonProgress,
                            isProUnlocked = isProUser,
                            onBack = { navController.popBackStack() },
                            onSaveLessonScore = viewModel::saveLessonScore,
                            onSpeakPhrase = { text, modern -> viewModel.audioPlayer.speak(text, modern) },
                            onOpenPassage = openPassageById,
                            onRequestPaywall = viewModel::openPaywall
                        )
                    }

                    composable(MimirRoutes.ELDER_FUTHARK_TRANSLITERATOR) {
                        ElderFutharkTransliteratorScreen(
                            onBack = { navController.popBackStack() },
                            onShowMessage = viewModel::showMessage
                        )
                    }

                    composable(MimirRoutes.ELDER_FUTHARK_FLASHCARDS) {
                        ElderFutharkFlashcardsScreen(
                            roomRunes = allRunes,
                            flashcardProgress = flashcardProgress,
                            isProUnlocked = isProUser,
                            onBack = { navController.popBackStack() },
                            onRecordFlashcard = viewModel::recordFlashcard,
                            onRequestPaywall = viewModel::openPaywall
                        )
                    }

                    composable(MimirRoutes.SETTINGS_VAULT) {
                        SettingsAndVaultScreen(
                            settings = settings,
                            proEntitlement = proEntitlement,
                            dailyReadingProgress = dailyReadingProgress,
                            annotations = annotations,
                            vaultUnlocked = vaultUnlocked,
                            canUseBiometrics = BiometricHelper.canAuthenticate(activity),
                            onBack = { navController.popBackStack() },
                            onToggleDarkMode = viewModel::toggleDarkMode,
                            onSelectTheme = viewModel::setThemeMode,
                            onChangeFontScale = viewModel::setFontScale,
                            onToggleMorningReminder = { enabled -> viewModel.setMorningReminder(enabled) },
                            onToggleBiometricVault = viewModel::setBiometricVaultEnabled,
                            onUnlockVault = unlockVault,
                            onUpdateHandle = viewModel::setDisplayHandle,
                            onLogDailyReading = { viewModel.logDailyReadingVerse(1) },
                            onSetDailyGoal = viewModel::setDailyReadingGoal,
                            onExportBackup = { uri, pass -> viewModel.exportEncryptedBackupToUri(activity, uri, pass) },
                            onImportBackup = { uri, pass -> viewModel.importEncryptedBackupFromUri(activity, uri, pass) },
                            onOpenPaywall = viewModel::openPaywall,
                            onRevokeProPreview = viewModel::revokeProPreview
                        )
                    }
                }
            }
        }

        val activePaywall = paywallTrigger
        if (activePaywall != null) {
            MimirProPaywallSheet(
                feature = activePaywall,
                initialPlan = proEntitlement.activePlanTier,
                onDismiss = viewModel::dismissPaywall,
                onSubscribe = viewModel::subscribePro,
                onRestorePurchases = viewModel::restorePurchases
            )
        }
    }
}
