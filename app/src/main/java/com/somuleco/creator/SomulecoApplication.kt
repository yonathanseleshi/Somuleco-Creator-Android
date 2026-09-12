package com.somuleco.creator

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry point. Annotated with [HiltAndroidApp] so Hilt generates the
 * application-level component that every `@AndroidEntryPoint`/`@HiltViewModel` graph
 * hangs off (Foundation Wave 05, plan §13 Decision 4).
 */
@HiltAndroidApp
class SomulecoApplication : Application()
