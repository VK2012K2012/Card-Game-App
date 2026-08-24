# CardGameHub source package

This archive contains the current local CardGameHub Android project source.

The current local changes include Material 3 Expressive theming, Google Sans Flex, independent System/Light/Dark controls, AMOLED black support, palette customization, onboarding, and the existing Durak game source.

## Build

Open the project in Android Studio, allow Gradle synchronization, and run:

```bash
./gradlew clean :app:testDebugUnitTest :app:assembleRelease --no-daemon --console=plain -Dorg.gradle.jvmargs=-Xmx1g
```

The release APK is generated at:

```text
app/build/outputs/apk/release/app-release.apk
```

`local.properties`, Gradle caches, build outputs, and generated APK files are intentionally excluded from this source archive. Set the Android SDK path through Android Studio or create a local `local.properties` file for the target machine.

The GitHub repository and release were not changed by this package export.
