package com.somuleco.creator.core.session

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.somuleco.creator.data.model.AuthState
import com.somuleco.creator.data.repository.interfaces.ChannelRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private val Context.sessionDataStore by preferencesDataStore(name = "app_session_state")

/**
 * Android's cross-app state holder (`GRD/Contracts/v0.1-client-data-access.md` §5, plan
 * §13 Decision 7, §7.3 task 2). This did not exist before Foundation Wave 05 — all
 * cross-app state previously lived inside `CreatorRepository`.
 *
 * Owns exactly: [authState], [isCreatorMode], [selectedChannelId], and their setters — no
 * domain collections, per Decision 8 ("do not rename the god object"). It is also the
 * DataStore persistence owner for mode + Channel context (closing the Wave 04 gap recorded
 * in the Wave 05 plan §2.5(1) — Wave 04 reported this as implemented; it was not).
 *
 * [SomulecoApp]/[com.somuleco.creator.feature.shell.CreatorShell] read this for Wave 04's
 * auth gating and mode-switch routing.
 */
@Singleton
class AppSessionState @Inject constructor(
    @ApplicationContext private val context: Context,
    private val channelRepository: ChannelRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Foundation Wave 08 (plan §7.4 task 6): starts Unknown, not pre-authenticated. Firebase
    // persists sessions locally on Android, so on cold start this holder genuinely does not yet
    // know whether there is a restorable session — only FirebaseAuthRepository's own
    // AuthStateListener callback (which calls setAuthState below) can resolve that, once it has
    // actually asked Firebase. This holder is deliberately not itself an AuthRepository; it
    // mirrors the session's current auth snapshot for shell-level gating and persists
    // mode/channel alongside it.
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unknown)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _isCreatorMode = MutableStateFlow(true)
    val isCreatorMode: StateFlow<Boolean> = _isCreatorMode.asStateFlow()

    private val _selectedChannelId = MutableStateFlow<String?>(null)
    val selectedChannelId: StateFlow<String?> = _selectedChannelId.asStateFlow()

    // Foundation Wave 09 (`v0.1-creator-authorization.md` §7, plan §7.4 task 3): client-side-only
    // "which of the caller's possibly-several Creator Accounts is current," persisted exactly
    // the same way as [selectedChannelId] above. Unlike Channel selection, there is no
    // "no selection = all" fallback semantic here — [CreatorAccountViewModel] applies the
    // default-selection rule (sole account; else most-recently-activatedAt; else
    // most-recently-createdAt) once the real account list has loaded.
    private val _selectedCreatorAccountId = MutableStateFlow<String?>(null)
    val selectedCreatorAccountId: StateFlow<String?> = _selectedCreatorAccountId.asStateFlow()

    // Real, server-fetched status of the selected Creator Account (plan §13 Decision 9) — NOT
    // [isCreatorMode], which stays 100% presentation-only per the guide's explicit rule. This is
    // what [com.somuleco.creator.core.navigation.navigateFromShell]'s Creator-mode landing gate
    // must key off instead of the previously-hardcoded `AuthState.Authenticated.isCreator`.
    // Defaults false: a freshly authenticated identity has taken no activation action yet.
    private val _isCreatorAccountActive = MutableStateFlow(false)
    val isCreatorAccountActive: StateFlow<Boolean> = _isCreatorAccountActive.asStateFlow()

    init {
        scope.launch { hydrateFromDataStore() }
    }

    private suspend fun hydrateFromDataStore() {
        val prefs = context.sessionDataStore.data.first()
        val persistedMode = prefs[KEY_IS_CREATOR_MODE] ?: true
        _isCreatorMode.value = persistedMode

        val persistedChannelId = prefs[KEY_SELECTED_CHANNEL_ID]
        // Missing-channel fallback per v0.1-navigation-semantics.md §4.3: a persisted
        // channel id that no longer exists in the current channel list falls back to
        // null ("All Channels") rather than crashing or keeping a stale reference.
        val channelIds = channelRepository.channels.value.map { it.id }.toSet()
        _selectedChannelId.value = persistedChannelId?.takeIf { it in channelIds }

        // Unlike Channel selection above, there is no locally-available account list at hydrate
        // time to validate this id against (Creator Accounts are remote-only, unlike the mock
        // Channel list) — CreatorAccountViewModel.load() re-validates this persisted id against
        // the real fetched list and re-applies the default-selection rule if it's stale/absent.
        _selectedCreatorAccountId.value = prefs[KEY_SELECTED_CREATOR_ACCOUNT_ID]
    }

    fun setAuthState(state: AuthState) {
        _authState.value = state
    }

    fun setCreatorMode(enabled: Boolean) {
        _isCreatorMode.value = enabled
        persist { it[KEY_IS_CREATOR_MODE] = enabled }
    }

    fun toggleCreatorMode() = setCreatorMode(!_isCreatorMode.value)

    fun selectChannel(id: String?) {
        _selectedChannelId.value = id
        persist {
            if (id == null) it.remove(KEY_SELECTED_CHANNEL_ID) else it[KEY_SELECTED_CHANNEL_ID] = id
        }
    }

    fun selectCreatorAccount(id: String?) {
        _selectedCreatorAccountId.value = id
        persist {
            if (id == null) it.remove(KEY_SELECTED_CREATOR_ACCOUNT_ID) else it[KEY_SELECTED_CREATOR_ACCOUNT_ID] = id
        }
    }

    /** Set by [com.somuleco.creator.feature.creator.account.CreatorAccountViewModel] whenever
     * the selected account's fetched `status` changes, so navigation gating always reflects
     * real server truth rather than a client-decided flag. */
    fun setCreatorAccountActive(active: Boolean) {
        _isCreatorAccountActive.value = active
    }

    fun logout() {
        _authState.value = AuthState.Unauthenticated
        setCreatorMode(false)
        selectChannel(null)
        selectCreatorAccount(null)
        setCreatorAccountActive(false)
    }

    private fun persist(edit: (androidx.datastore.preferences.core.MutablePreferences) -> Unit) {
        scope.launch {
            context.sessionDataStore.edit { edit(it) }
        }
    }

    companion object {
        private val KEY_IS_CREATOR_MODE = booleanPreferencesKey("is_creator_mode")
        private val KEY_SELECTED_CHANNEL_ID = stringPreferencesKey("selected_channel_id")
        private val KEY_SELECTED_CREATOR_ACCOUNT_ID = stringPreferencesKey("selected_creator_account_id")
    }
}
