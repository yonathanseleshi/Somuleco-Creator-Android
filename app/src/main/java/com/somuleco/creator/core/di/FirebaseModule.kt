package com.somuleco.creator.core.di

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.somuleco.creator.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Foundation Wave 08 (plan §7.4 task 2, §13 Decision 1). Provides the app's single
 * [FirebaseAuth] instance. Debug builds (the "local/dev" conceptual environment — see
 * `app/build.gradle.kts`'s `buildTypes` comments) point it at the Firebase Local Emulator
 * Suite via the Android emulator's host-loopback alias (`10.0.2.2`, matching every other
 * `10.0.2.2`-addressed host this build type already targets), never a real Firebase backend —
 * this is what lets the whole wave be implemented and validated without the real
 * `com.somuleco.creator` app registration this repo does not yet have (`app/google-services.json`'s
 * own header comment). Release builds talk to the real, shared `somuleco-firebase` project once
 * this app is registered in it (external follow-up, disclosed in the Wave 08 completion report).
 */
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    private const val AUTH_EMULATOR_HOST = "10.0.2.2"
    private const val AUTH_EMULATOR_PORT = 9099

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        val auth = Firebase.auth
        if (BuildConfig.DEBUG) {
            // useEmulator() throws if called more than once against the same FirebaseAuth
            // instance (e.g. a second Hilt component created in a test run) — this provider is
            // itself @Singleton so that shouldn't happen in production code, but runCatching
            // keeps a stray double-call from crashing app startup.
            runCatching { auth.useEmulator(AUTH_EMULATOR_HOST, AUTH_EMULATOR_PORT) }
        }
        return auth
    }
}
