package com.somuleco.creator.feature.creator.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.somuleco.creator.core.network.CreatorAccountDto
import com.somuleco.creator.core.session.AppSessionState
import com.somuleco.creator.core.ui.UiState
import com.somuleco.creator.core.ui.uiStateOf
import com.somuleco.creator.data.repository.interfaces.CreatorAccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Foundation Wave 09 (plan §7.4 task 2; `v0.1-creator-authorization.md` §2-3, §7). Did not
 * exist before this wave. Follows the Wave 05 [ChannelsViewModel] convention exactly —
 * [state] is the migrated four-state [UiState] shape for the caller's account list — plus the
 * selected account's own state, since a caller may own several accounts (§2.6 of the wave
 * plan: a Somuleco identity may hold multiple Creator Accounts).
 *
 * Owns the client-side default-selection rule (`v0.1-creator-authorization.md` §7): applied
 * once per successful list load whenever there is no still-valid prior selection.
 */
@HiltViewModel
class CreatorAccountViewModel @Inject constructor(
    private val creatorAccountRepository: CreatorAccountRepository,
    private val appSessionState: AppSessionState
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<List<CreatorAccountDto>>>(UiState.Loading)
    val state: StateFlow<UiState<List<CreatorAccountDto>>> = _state.asStateFlow()

    /** The currently-selected account's own DTO, or null if none is selected / it hasn't
     * loaded yet. Derived rather than separately fetched — [creatorAccountRepository.accounts]
     * already holds every account this caller owns once [load] has run once. */
    val selectedAccount: StateFlow<CreatorAccountDto?> = combine(
        creatorAccountRepository.accounts,
        appSessionState.selectedCreatorAccountId
    ) { accounts, selectedId -> accounts.find { it.id == selectedId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val selectedAccountId: StateFlow<String?> = appSessionState.selectedCreatorAccountId

    /** Result of the most recent activate()/completeOnboarding()/reactivate()/close() call —
     * a screen shows this alongside (not instead of) [state], since activating a second
     * account for an already-Creator caller should not blank out the list it's already showing. */
    private val _actionState = MutableStateFlow<UiState<CreatorAccountDto>?>(null)
    val actionState: StateFlow<UiState<CreatorAccountDto>?> = _actionState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            creatorAccountRepository.listAccounts()
                .onSuccess { accounts ->
                    _state.value = uiStateOf(accounts)
                    applyDefaultSelectionIfNeeded(accounts)
                    syncSelectedAccountActiveStatus(accounts)
                }
                .onFailure { e ->
                    _state.value = UiState.Error(e.message ?: "Unable to load Creator Accounts.")
                }
        }
    }

    /** `v0.1-creator-authorization.md` §7's default-selection rule, applied once on first
     * successful load with no still-valid prior selection: the sole account if exactly one
     * exists; else the most-recently-activatedAt account among those with a non-null
     * activatedAt; else (every account still `draft`) the most-recently-createdAt account. */
    private fun applyDefaultSelectionIfNeeded(accounts: List<CreatorAccountDto>) {
        val currentSelection = appSessionState.selectedCreatorAccountId.value
        if (currentSelection != null && accounts.any { it.id == currentSelection }) return

        val defaultId = when {
            accounts.isEmpty() -> null
            accounts.size == 1 -> accounts.first().id
            else -> {
                val activated = accounts.filter { it.activatedAt != null }
                if (activated.isNotEmpty()) {
                    activated.maxByOrNull { it.activatedAt!! }?.id
                } else {
                    accounts.maxByOrNull { it.createdAt }?.id
                }
            }
        }
        appSessionState.selectCreatorAccount(defaultId)
    }

    private fun syncSelectedAccountActiveStatus(accounts: List<CreatorAccountDto>) {
        val selected = accounts.find { it.id == appSessionState.selectedCreatorAccountId.value }
        appSessionState.setCreatorAccountActive(selected?.status == "active")
    }

    /** User-initiated account switch (`v0.1-creator-authorization.md` §7: "the user can change
     * the selection explicitly at any time"). */
    fun selectAccount(id: String) {
        appSessionState.selectCreatorAccount(id)
        syncSelectedAccountActiveStatus(creatorAccountRepository.accounts.value)
    }

    /** Minimal activation form (plan §7.4 task 4) — `creatorType` only. Verified directly
     * against the real NestJS `CreateCreatorAccountDto`
     * (`src/creator-account/dto/create-creator-account.dto.ts`), which declares only
     * `creatorType: string` (`@IsNotEmpty`); the API's `forbidNonWhitelisted: true` validation
     * pipe rejects any other property, so an earlier accountName/displayName/description shape
     * (a literal reading of the plan's own loosely-worded §7.4 task 4) made every real
     * `POST /creator-account` call a guaranteed 400. No monetization/Stripe/tier fields. Creates
     * a new, independent `draft` account; never fails because the caller already owns one
     * (`v0.1-creator-authorization.md` §2). */
    fun activate(creatorType: String) {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            creatorAccountRepository.activate(creatorType)
                .onSuccess { account ->
                    _actionState.value = UiState.Success(account)
                    _state.value = uiStateOf(creatorAccountRepository.accounts.value)
                    // A freshly-activated account is the obvious thing to select next.
                    appSessionState.selectCreatorAccount(account.id)
                    appSessionState.setCreatorAccountActive(account.status == "active")
                }
                .onFailure { e ->
                    _actionState.value = UiState.Error(e.message ?: "Unable to activate Creator Account.")
                }
        }
    }

    /** `draft` -> `active` transition (plan §7.4 task 4's "complete onboarding" step). */
    fun completeOnboarding(id: String) {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            creatorAccountRepository.completeOnboarding(id)
                .onSuccess { account ->
                    _actionState.value = UiState.Success(account)
                    _state.value = uiStateOf(creatorAccountRepository.accounts.value)
                    if (appSessionState.selectedCreatorAccountId.value == account.id) {
                        appSessionState.setCreatorAccountActive(account.status == "active")
                    }
                }
                .onFailure { e ->
                    _actionState.value = UiState.Error(e.message ?: "Unable to complete onboarding.")
                }
        }
    }

    fun reactivate(id: String) {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            creatorAccountRepository.reactivate(id)
                .onSuccess { account ->
                    _actionState.value = UiState.Success(account)
                    _state.value = uiStateOf(creatorAccountRepository.accounts.value)
                    if (appSessionState.selectedCreatorAccountId.value == account.id) {
                        appSessionState.setCreatorAccountActive(account.status == "active")
                    }
                }
                .onFailure { e ->
                    _actionState.value = UiState.Error(e.message ?: "Unable to reactivate Creator Account.")
                }
        }
    }

    fun close(id: String) {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            creatorAccountRepository.close(id)
                .onSuccess { account ->
                    _actionState.value = UiState.Success(account)
                    _state.value = uiStateOf(creatorAccountRepository.accounts.value)
                    if (appSessionState.selectedCreatorAccountId.value == account.id) {
                        appSessionState.setCreatorAccountActive(false)
                    }
                }
                .onFailure { e ->
                    _actionState.value = UiState.Error(e.message ?: "Unable to close Creator Account.")
                }
        }
    }

    fun clearActionState() {
        _actionState.value = null
    }
}
