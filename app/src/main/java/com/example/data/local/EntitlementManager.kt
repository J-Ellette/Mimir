package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.entitlementDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "mimir_pro_entitlement"
)

enum class ProPlanTier(
    val id: String,
    val title: String,
    val priceLabel: String,
    val cadenceNote: String,
    val badge: String? = null
) {
    ANNUAL(
        id = "pro_annual",
        title = "Eddic Annual",
        priceLabel = "$29.99 / yr",
        cadenceNote = "Save 50% · Full Scholarly Access",
        badge = "Recommended"
    ),
    MONTHLY(
        id = "pro_monthly",
        title = "Scholar Monthly",
        priceLabel = "$4.99 / mo",
        cadenceNote = "Flexible month-to-month access"
    ),
    LIFETIME(
        id = "pro_lifetime",
        title = "Skald Lifetime",
        priceLabel = "$59.99 once",
        cadenceNote = "Permanent offline unlock"
    )
}

enum class PaywallFeature(
    val headline: String,
    val subtitle: String,
    val badgeLabel: String
) {
    GENERAL(
        headline = "Unlock Mímir Pro",
        subtitle = "Get unlimited 24-rune flashcards, advanced Old Norse grammar units, and scholarly commentary packs.",
        badgeLabel = "Mímir Pro"
    ),
    LICENSED_PACKS(
        headline = "Scholarly Commentary & Packs",
        subtitle = "Unlock Contemporary Scholarly Editions alongside Eddic Meter, Skaldic Kennings, and Runology commentary packs.",
        badgeLabel = "Pro Commentary"
    ),
    NORSE_LESSONS_PRO(
        headline = "Advanced Old Norse Grammar",
        subtitle = "Unlock Units 2+ covering strong verbs, Eddic syntax, skaldic diction, and phonetic drills.",
        badgeLabel = "Pro Grammar"
    ),
    AUDIO_PRONUNCIATION(
        headline = "Spoken Old Norse & Icelandic Audio",
        subtitle = "Hear reconstructed Old Norse and Modern Icelandic pronunciation across stanzas and IPA drills.",
        badgeLabel = "Pro Audio"
    ),
    MANUSCRIPT_OCR(
        headline = "Manuscript OCR Study Scanner",
        subtitle = "Scan manuscript pages via live CameraX or gallery photos and cross-reference the Old Norse lexicon offline.",
        badgeLabel = "Pro Study Tool"
    ),
    BIOMETRIC_VAULT(
        headline = "Biometric Vault & Encrypted Sync",
        subtitle = "Protect private reflections behind device biometrics and export AES-256 encrypted backups.",
        badgeLabel = "Pro Vault"
    ),
    UNLIMITED_SRS(
        headline = "Unlimited 24-Rune SM-2 Flashcards",
        subtitle = "Drill Heimdallr's & Týr's Ættir, reverse sound/name prompt modes, and all historic runestone decoders.",
        badgeLabel = "Pro Flashcards"
    )
}

data class ProEntitlement(
    val isProUser: Boolean = false,
    val activePlanTier: ProPlanTier = ProPlanTier.ANNUAL,
    val activatedEpochMillis: Long = 0L
) {
    val isProUnlocked: Boolean
        get() = isProUser
}

class EntitlementManager(private val context: Context) {

    private object Keys {
        val IS_PRO_USER = booleanPreferencesKey("is_pro_unlocked")
        val ACTIVE_PLAN_TIER = stringPreferencesKey("active_plan_tier")
        val ACTIVATED_EPOCH_MILLIS = longPreferencesKey("activated_epoch_millis")
    }

    val entitlementFlow: Flow<ProEntitlement> = context.entitlementDataStore.data.map { prefs ->
        val tierName = prefs[Keys.ACTIVE_PLAN_TIER] ?: ProPlanTier.ANNUAL.name
        ProEntitlement(
            isProUser = prefs[Keys.IS_PRO_USER] ?: false,
            activePlanTier = runCatching { ProPlanTier.valueOf(tierName) }
                .getOrDefault(ProPlanTier.ANNUAL),
            activatedEpochMillis = prefs[Keys.ACTIVATED_EPOCH_MILLIS] ?: 0L
        )
    }

    val isProUser: Flow<Boolean> = entitlementFlow.map { it.isProUser }

    suspend fun setProUser(
        enabled: Boolean,
        plan: ProPlanTier = ProPlanTier.ANNUAL,
        nowMillis: Long = System.currentTimeMillis()
    ) {
        context.entitlementDataStore.edit { prefs ->
            prefs[Keys.IS_PRO_USER] = enabled
            if (enabled) {
                prefs[Keys.ACTIVE_PLAN_TIER] = plan.name
                prefs[Keys.ACTIVATED_EPOCH_MILLIS] = nowMillis
            }
        }
    }

    suspend fun unlockPro(
        plan: ProPlanTier = ProPlanTier.ANNUAL,
        nowMillis: Long = System.currentTimeMillis()
    ) {
        setProUser(enabled = true, plan = plan, nowMillis = nowMillis)
    }

    suspend fun restorePurchases(nowMillis: Long = System.currentTimeMillis()): Boolean {
        setProUser(enabled = true, plan = ProPlanTier.ANNUAL, nowMillis = nowMillis)
        return true
    }

    suspend fun revokeProPreview() {
        setProUser(enabled = false)
    }
}
