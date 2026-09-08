# Somuleco Creator — Android

Native Android client for the Somuleco Creator platform (Jetpack Compose, single `app` module).

## Prerequisites

- **JDK 17.** The project's Gradle toolchain requires JDK 17. On macOS with Homebrew:

  ```bash
  brew install openjdk@17
  ```

  `/usr/libexec/java_home` does **not** discover the Homebrew JDK automatically, so it will
  not be picked up by default. Export it explicitly before running any Gradle command:

  ```bash
  export JAVA_HOME=/opt/homebrew/opt/openjdk@17
  export PATH="$JAVA_HOME/bin:$PATH"
  ```

- **Android SDK** (via Android Studio, or the standalone command-line tools) with `compileSdk 36`
  available.
- No emulator is required to build, unit test, or lint — only to run the app.

## Build

```bash
./gradlew assembleDebug
```

## Test

```bash
./gradlew test
```

Unit tests (including Robolectric-based tests) run without a device/emulator.
Instrumented tests (`app/src/androidTest`) require a connected device or emulator and are run via:

```bash
./gradlew connectedDebugAndroidTest
```

## Lint

```bash
./gradlew lintDebug
```

### Formatting

No Kotlin formatter (ktlint/detekt) is currently configured in this repository. This is a
documented gap, not an oversight — Wave 02 intentionally did not introduce a new formatting
tool to avoid unreviewed repo-wide reformatting. Adding ktlint/detekt (with a first
apply-and-commit pass) is left for a follow-up wave.

## Package structure convention

The app's Kotlin source lives under `app/src/main/java/com/somuleco/creator/`, matching the
`namespace`/`applicationId` in `app/build.gradle.kts` (`com.somuleco.creator` — aligned with
iOS's `PRODUCT_BUNDLE_IDENTIFIER: com.somuleco.creator` for cross-platform identity
consistency). Sub-packages are organized by architectural layer:

```text
com.somuleco.creator
├── core/          # cross-cutting concerns: design system, navigation, network config
├── data/          # models, repositories (currently backed by an in-memory mock repository)
├── feature/       # screen-level UI grouped by product area (auth, consumer, creator, ...)
└── ui/theme/      # Compose theme (color, type, shapes)
```

New code should be added under the layer it belongs to rather than introducing new top-level
packages.

## API base URL configuration

Core (NestJS) and Creator AI (FastAPI) API base URLs are **not** hardcoded in source. They are
defined per Gradle build type as `BuildConfig` fields in `app/build.gradle.kts`, and read by
`app/src/main/java/com/somuleco/creator/core/network/ApiConfig.kt`:

| Build type | Conceptual environment | `CORE_API_BASE_URL`             | `AI_API_BASE_URL`              |
|------------|-------------------------|----------------------------------|----------------------------------|
| `debug`    | local / dev              | `http://10.0.2.2:3000/api/v1/`  | `http://10.0.2.2:8000/api/v1/`  |
| `release`  | production (placeholder) | `https://api.somuleco.com/v1/`  | `https://ai.somuleco.com/v1/`   |

`10.0.2.2` is the Android emulator's alias for the host machine's loopback interface, so `debug`
builds expect a NestJS Core API on the host at port `3000` and a FastAPI Creator AI service at
port `8000`. The `release` URLs are documented placeholders — no production infrastructure is
provisioned yet.

Only `debug` and `release` build types exist; no product flavors were introduced, per the
"only create environments actually needed" guidance.

**This configuration boundary is not yet wired to any real network calls.** The entire
`core/network/` layer (`NetworkModule`, `CoreApiService`, `CreatorAiApiService`, `ApiConfig`,
`AuthInterceptor`) is currently unused — every screen in the app runs against the in-memory
mock repository at `app/src/main/java/com/somuleco/creator/data/repository/CreatorRepository.kt`.
Wiring real networking in is out of scope until a later wave (see the Wave 01/02 GRD reports).

## Secrets

No server secrets are embedded in the client. The Secrets Gradle Plugin is configured
(`secrets { propertiesFileName = ".env" ... }` in `app/build.gradle.kts`) to read a local,
gitignored `.env` file against the template in `.env.example`. It currently defines a single,
commented-out placeholder (`GEMINI_API_KEY`) that nothing in the codebase references — the
plugin wiring is present but unused. Left as-is; not cleaned up in this wave since it introduces
no secret and is not actively harmful.

The `release` signing config reads `KEYSTORE_PATH` / `STORE_PASSWORD` / `KEY_PASSWORD` from
environment variables (never committed). The `debug` signing config uses a standard,
non-secret Android debug keystore (`debug.keystore` at the repo root, gitignored).
