package com.somuleco.creator

import android.app.Application
import com.somuleco.creator.data.repository.interfaces.AuthRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent

/**
 * Application entry point. Annotated with [HiltAndroidApp] so Hilt generates the
 * application-level component that every `@AndroidEntryPoint`/`@HiltViewModel` graph
 * hangs off (Foundation Wave 05, plan §13 Decision 4).
 */
@HiltAndroidApp
class SomulecoApplication : Application() {

    /** Bridges into the Hilt `SingletonComponent` from [onCreate], before any
     * `@AndroidEntryPoint` Activity/`@HiltViewModel` exists to request field injection —
     * mirrors Somuleco Connect's own `SomulecoConnectApplication.ApplicationEntryPoint`. */
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ApplicationEntryPoint {
        fun authRepository(): AuthRepository
    }

    override fun onCreate() {
        super.onCreate()
        // Foundation Wave 08 (plan §7.4 task 6): force-construct the singleton AuthRepository
        // (FirebaseAuthRepository) at process start, so its FirebaseAuth.AuthStateListener is
        // attached before MainActivity's first Compose frame ever reads
        // AppSessionState.authState.value to pick a navigation start destination. Without this,
        // the listener would only attach lazily whenever some ViewModel first injects
        // AuthRepository (e.g. AuthViewModel on the login screen), which is too late for a
        // cold-start session restoration to have any chance of beating that first read.
        //
        // runCatching: on a real device/emulator, Firebase's own ContentProvider
        // (FirebaseInitProvider) auto-initializes the default FirebaseApp before any
        // Application.onCreate() runs, so this succeeds. Under Robolectric (this repo's unit
        // test runner — `AppSessionStatePersistenceTest` et al. all instantiate this real
        // Application class, Hilt-annotated or not, since Robolectric always runs the
        // manifest's declared Application), that ContentProvider auto-init does not reliably
        // happen first, so FirebaseApp.getInstance() can throw here. Swallowing that failure
        // keeps every non-auth-focused Robolectric test from crashing solely because of this
        // eager hook; AuthTokenProviderImplTest/AuthViewModelTest cover the real auth behavior
        // this hook exists for via fakes that never touch FirebaseApp at all.
        runCatching {
            EntryPointAccessors.fromApplication(this, ApplicationEntryPoint::class.java).authRepository()
        }
    }
}
