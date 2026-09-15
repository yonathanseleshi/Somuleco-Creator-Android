package com.somuleco.creator.data.repository.impl

import com.somuleco.creator.core.network.CoreApiService
import com.somuleco.creator.core.network.CreateCreatorAccountRequest
import com.somuleco.creator.core.network.CreatorAccountDto
import com.somuleco.creator.data.repository.interfaces.CreatorAccountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Real, Retrofit-backed [CreatorAccountRepository] (Foundation Wave 09, plan §7.4 task 1;
 * `GRD/Contracts/v0.1-creator-authorization.md` §2-3). Did not exist before this wave — Android
 * had no `CreatorAccountRepository`, ViewModel, or screen at all.
 *
 * Follows [FirebaseAuthRepository]'s established real-repository conventions: constructor
 * injection of [CoreApiService] directly (no data-source indirection), `Result<T>` returned
 * from every suspend call via `runCatching`, `Response<T>` checked with `isSuccessful`/`body()`.
 * Unlike [FirebaseAuthRepository], this repository owns no [com.somuleco.creator.core.session.
 * AppSessionState] mutation itself — which account is *selected* is shell state the caller
 * (`CreatorAccountViewModel`) resolves against [AppSessionState.selectedCreatorAccountId],
 * mirroring [com.somuleco.creator.data.repository.interfaces.ChannelRepository]'s own explicit
 * "no selectedChannelId here" precedent (this is a stateless domain boundary).
 */
@Singleton
class CreatorAccountRepositoryImpl @Inject constructor(
    private val coreApiService: CoreApiService
) : CreatorAccountRepository {

    private val _accounts = MutableStateFlow<List<CreatorAccountDto>>(emptyList())
    override val accounts: StateFlow<List<CreatorAccountDto>> = _accounts.asStateFlow()

    override suspend fun listAccounts(): Result<List<CreatorAccountDto>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = coreApiService.listCreatorAccounts()
            if (!response.isSuccessful) {
                error("Failed to list Creator Accounts: HTTP ${response.code()}")
            }
            response.body() ?: emptyList()
        }.onSuccess { list -> _accounts.value = list }
    }

    override suspend fun getAccount(id: String): Result<CreatorAccountDto> = withContext(Dispatchers.IO) {
        runCatching {
            val response = coreApiService.getCreatorAccount(id)
            if (!response.isSuccessful) {
                // Uniform 404 semantics per v0.1-creator-authorization.md §4 — this repository
                // surfaces whatever HTTP status the server returns (not-found and not-yours are
                // indistinguishable server-side by design) rather than inventing a distinct case.
                error("Failed to load Creator Account $id: HTTP ${response.code()}")
            }
            response.body() ?: error("Empty Creator Account response body")
        }.onSuccess { account ->
            _accounts.value = _accounts.value.map { if (it.id == account.id) account else it }
                .let { updated -> if (updated.any { it.id == account.id }) updated else updated + account }
        }
    }

    override suspend fun activate(creatorType: String): Result<CreatorAccountDto> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response = coreApiService.activateCreatorAccount(
                    CreateCreatorAccountRequest(creatorType = creatorType)
                )
                if (!response.isSuccessful) {
                    error("Failed to activate Creator Account: HTTP ${response.code()}")
                }
                response.body() ?: error("Empty Creator Account response body")
            }.onSuccess { account -> _accounts.value = _accounts.value + account }
        }

    override suspend fun completeOnboarding(id: String): Result<CreatorAccountDto> = withContext(Dispatchers.IO) {
        runCatching {
            val response = coreApiService.completeCreatorAccountOnboarding(id)
            if (!response.isSuccessful) {
                // 409 (not currently `draft`) or 404 (not-found/not-yours) both surface here as
                // a failed Result per v0.1-creator-authorization.md §3.
                error("Failed to complete onboarding for Creator Account $id: HTTP ${response.code()}")
            }
            response.body() ?: error("Empty Creator Account response body")
        }.onSuccess(::replaceInAccounts)
    }

    override suspend fun reactivate(id: String): Result<CreatorAccountDto> = withContext(Dispatchers.IO) {
        runCatching {
            val response = coreApiService.reactivateCreatorAccount(id)
            if (!response.isSuccessful) {
                error("Failed to reactivate Creator Account $id: HTTP ${response.code()}")
            }
            response.body() ?: error("Empty Creator Account response body")
        }.onSuccess(::replaceInAccounts)
    }

    override suspend fun close(id: String): Result<CreatorAccountDto> = withContext(Dispatchers.IO) {
        runCatching {
            val response = coreApiService.closeCreatorAccount(id)
            if (!response.isSuccessful) {
                error("Failed to close Creator Account $id: HTTP ${response.code()}")
            }
            response.body() ?: error("Empty Creator Account response body")
        }.onSuccess(::replaceInAccounts)
    }

    private fun replaceInAccounts(account: CreatorAccountDto) {
        _accounts.value = _accounts.value.map { if (it.id == account.id) account else it }
    }
}
