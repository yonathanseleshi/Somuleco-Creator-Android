package com.somuleco.creator.core.di

import com.somuleco.creator.core.network.AuthTokenProvider
import com.somuleco.creator.core.network.AuthTokenProviderImpl
import com.somuleco.creator.core.network.CoreApiService
import com.somuleco.creator.core.network.CreatorAiApiService
import com.somuleco.creator.core.network.FirebaseAuthIdTokenProvider
import com.somuleco.creator.core.network.FirebaseIdTokenProvider
import com.somuleco.creator.core.network.NetworkModule
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

/**
 * Wires the Wave 02 `ApiConfig` config seam (BuildConfig-derived base URLs) into the DI
 * graph through [NetworkModule]'s factory functions (plan §7.3 task 10 — previously
 * `core/network` had zero external references and nothing consumed it via DI).
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkBindsModule {
    @Binds
    @Singleton
    abstract fun bindAuthTokenProvider(impl: AuthTokenProviderImpl): AuthTokenProvider

    // Foundation Wave 08 (plan §7.4 task 4): binds the synchronous real-Firebase-ID-token seam
    // AuthTokenProviderImpl now reads through, kept as an interface so it's fake-able in tests
    // (AuthTokenProviderImplTest) without touching a real FirebaseAuth instance.
    @Binds
    @Singleton
    abstract fun bindFirebaseIdTokenProvider(impl: FirebaseAuthIdTokenProvider): FirebaseIdTokenProvider
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkProvidesModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(tokenProvider: AuthTokenProvider): OkHttpClient =
        NetworkModule.createOkHttpClient(tokenProvider)

    @Provides
    @Singleton
    fun provideCoreApiService(okHttpClient: OkHttpClient): CoreApiService =
        NetworkModule.createCoreApiService(okHttpClient)

    @Provides
    @Singleton
    fun provideCreatorAiApiService(okHttpClient: OkHttpClient): CreatorAiApiService =
        NetworkModule.createCreatorAiApiService(okHttpClient)
}
