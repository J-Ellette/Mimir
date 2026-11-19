package com.example.data.local

import android.content.Context
import kotlinx.coroutines.flow.Flow

/**
 * Compatibility delegate preserving [ProEntitlementDataStore] callers atop [EntitlementManager].
 */
class ProEntitlementDataStore(context: Context) {
    private val delegate = EntitlementManager(context)

    val entitlementFlow: Flow<ProEntitlement> = delegate.entitlementFlow
    val isProUser: Flow<Boolean> = delegate.isProUser

    suspend fun unlockPro(plan: ProPlanTier, nowMillis: Long = System.currentTimeMillis()) {
        delegate.unlockPro(plan, nowMillis)
    }

    suspend fun restorePurchases(nowMillis: Long = System.currentTimeMillis()): Boolean {
        return delegate.restorePurchases(nowMillis)
    }

    suspend fun revokeProPreview() {
        delegate.revokeProPreview()
    }
}
