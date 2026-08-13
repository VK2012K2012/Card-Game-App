# Card Game Hub

**Card Game Hub** is a native Android card-game application built with Kotlin, Jetpack Compose, Material 3, and Room. The current playable game is **Durak** in offline single-player mode. Its architecture separates the game engine, opponent configuration, persistence, and interface so additional card games, on-device AI, and real multiplayer can be introduced without rebuilding the app shell.

## What is included

| Area | Current behaviour |
|---|---|
| Game library | A calm, single-purpose home screen that presents Durak as the available game and future titles as planned additions. |
| Match setup | Players select the variant, deck size, bot count, and standard or Smart local bot engine before starting. |
| Offline play | The current classic bot remains fully offline. Smart bot selection is designed as an on-device-engine contract; it clearly falls back to the classic bot until a bundled model is added. |
| Durak rules | Validated attacks, defenses, throw-ins, bito clearing, defender takes, hand replenishment, and end-of-game states. |
| Design | Dynamic Material You color on Android 12+ with expressive shape, size, containment, and hierarchy choices implemented using stable Material 3 Compose APIs. |
| Persistence | Completed matches and aggregate statistics are committed through one Room transaction to prevent partial history/stat updates. |
| Multiplayer | Represented honestly as a planned product lane; no misleading inactive online controls are exposed as if they were live. |

## Build locally

This repository includes a Gradle wrapper. Install Android Studio or the Android command-line tools with **Android SDK Platform 36.1**, set `ANDROID_HOME` (or place the SDK path in `local.properties`), and use a Java 21 JDK.

```bash
./gradlew :app:assembleDebug
```

The debug APK will be written to:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Run the focused Durak rules regression suite with:

```bash
./gradlew :app:testDebugUnitTest --tests 'com.example.durak.game.DurakEngineTest'
```

## Project layout

| Path | Responsibility |
|---|---|
| `app/src/main/java/com/example/durak/game` | Deterministic Durak state machine and rule validation. |
| `app/src/main/java/com/example/durak/ai` | Offline bot decision policy. |
| `app/src/main/java/com/example/durak/model` | Cards, player types, game configuration, and opponent-engine contract. |
| `app/src/main/java/com/example/ui/screens` | Home, match setup, in-game table, history, statistics, and settings experiences. |
| `app/src/main/java/com/example/ui/theme` | Dynamic Material 3 color, typography, and expressive shape tokens. |
| `app/src/main/java/com/example/data` | Room database, match history, and transactional statistics persistence. |

## Product roadmap

The app intentionally uses explicit seams rather than pretending future features already work. A bundled on-device model can implement `OnDeviceOpponentEngine`; network multiplayer can later be added behind a dedicated match service and authenticated room/lobby layer. Additional games should provide their own rules engine and game screen while reusing the library, setup, history, and profile shell.

See [DESIGN_AND_ARCHITECTURE.md](DESIGN_AND_ARCHITECTURE.md) for the rebuild decisions and detailed architecture notes.
