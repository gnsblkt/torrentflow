# TorrentFlow Android Companion

Native Android application scaffold for the TorrentFlow project.

## Implemented in this milestone

- Kotlin + Jetpack Compose dark-mode starter UI.
- Android Gradle project configuration (JDK 17, API 28+).
- libtorrent4j 2.1.0-39 Java API and native Android artifacts for ARM, ARM64, x86 and x86_64.
- Native session lifecycle wrapper using libtorrent4j.
- Signed catalogue data model and fail-closed Ed25519 verification boundary.
- Native enqueue method accepts only a `VerifiedCatalogueEntry`.
- Unit test confirming unknown publishers are rejected.
- GitHub Actions workflow for Android debug build and unit tests.

## Build

Open this `android/` directory in Android Studio with JDK 17 and Android SDK 35 installed. The native libtorrent4j 2.1.0-39 release requires Android API 28 or newer.

The Gradle wrapper JAR is not included in this initial source commit. Build using a locally installed Gradle 8.11.1:

```bash
gradle -p android :app:assembleDebug
gradle -p android :app:testDebugUnitTest
```

The GitHub Actions workflow uses a provisioned Gradle 8.11.1 distribution. Its result is the first automated build verification; until it passes, do not treat the project as build-verified or distribute an APK.

## Catalogue security gate

The publisher key registry is intentionally empty. Consequently, all catalogue entries are rejected and no transfer can be started until reviewed publisher Ed25519 public keys are added to app configuration. Do not populate this registry from remote catalogue data.

Only public-domain, openly licensed, or otherwise authorized material may be listed. A licence label is not proof of rights by itself; publisher identity, source, signature, and attribution must also be reviewed.

## Engine and licence notices

The app uses the libtorrent4j Java interface and platform-specific Android native artifacts, version 2.1.0-39. The upstream project declares MIT licensing. Review the exact transitive dependency notices and native binaries for the release build. The project does not copy LibreTorrent's GPL-3.0-or-later source or artwork.

- Upstream: https://github.com/aldenml/libtorrent4j
- Maven Central: https://central.sonatype.com/artifact/org.libtorrent4j/libtorrent4j
