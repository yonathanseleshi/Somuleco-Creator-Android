package com.somuleco.creator.core.di

import com.somuleco.creator.data.repository.impl.FirebaseAuthRepository
import com.somuleco.creator.data.repository.interfaces.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Foundation Wave 08 (plan §7.4 task 3): the [AuthRepository] binding split out of
 * [RepositoryModule] into its own module solely so a Hilt test module
 * (`app/src/test/.../di/TestAuthRepositoryModule.kt`) can replace it in isolation via
 * `@dagger.hilt.testing.TestInstallIn(replaces = [AuthRepositoryModule::class])` — Dagger
 * requires replacing an entire module, not a single `@Binds` method within a larger one.
 * Mirrors Somuleco Connect's own `di/AuthRepositoryModule.kt` split, for the same reason:
 * Hilt-driven Compose UI tests (`NavigationFlowTest`) need an AuthRepository that never touches
 * real Firebase, since neither the Firebase Local Emulator Suite nor a real Firebase project is
 * reachable from this repo's Robolectric unit test process.
 *
 * Production behavior is unchanged by this split: [FirebaseAuthRepository] is still bound here
 * exactly as it was inside [RepositoryModule] before this refactor.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: FirebaseAuthRepository): AuthRepository
}
