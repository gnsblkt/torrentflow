# TorrentFlow Android Companion — implementation blueprint

## Product boundary

TorrentFlow Android Companion is a native Android client for **verified public-domain, openly licensed, or user-authorized content only**.

The first release is catalogue-driven. It does not accept arbitrary magnet URIs, arbitrary torrent URLs, RSS auto-download feeds, or user-supplied torrent files. A transfer can start only from a catalogue record whose publisher, source, content identifier, and licence metadata pass validation.

This is a product-scope safeguard, not a claim that a torrent's legal status can be inferred from its info-hash.

## Why a native companion

The existing Next.js app uses WebTorrent in the browser. Browser WebTorrent relies on WebRTC-compatible peers and cannot connect to arbitrary conventional TCP/µTP BitTorrent peers. A native Android process can use Android networking and persistent foreground-service lifecycle rules.

Vercel remains the web dashboard host. It must not be used as a long-running torrent daemon.

## Proposed architecture

- **Web dashboard:** existing Next.js app; catalogue browsing, transfer status, and device pairing UI.
- **Android app:** Kotlin + Jetpack Compose; owns all network transfers and local file access.
- **Transfer engine:** evaluate libtorrent4j as a separately licensed dependency. Pin an exact tested version and verify ABI support before shipping.
- **Catalogue API:** serves signed catalogue manifests containing title, creator, source URL, content hash, licence identifier, licence URL, attribution, file list, and a publisher signature.
- **Policy gate:** Android verifies the manifest signature and checks the catalogue publisher allowlist before passing a descriptor to the engine. No generic URI input is exposed.
- **Local bridge:** authenticated pairing between the web dashboard and the Android app. Pairing uses a short-lived one-time code; API binds to loopback on-device and never exposes the engine port publicly.
- **Storage:** Android Storage Access Framework; user chooses the destination folder. No broad storage permission by default.

## Initial feature milestones

### Milestone 1 — safe foundation
- Kotlin Android project and Compose navigation shell
- Dark / light theme and responsive phone / tablet layouts
- Catalogue model and signed-manifest verification interface
- Licence and attribution details before starting a transfer
- Device pairing protocol and local API contract
- Privacy, data retention, and threat-model documentation

### Milestone 2 — transfer integration
- Integrate a reviewed native engine only behind the catalogue policy gate
- Transfer state model: queued, checking, downloading, paused, complete, error
- Per-file selection and progress
- Foreground service with notification and battery-aware behavior
- User-selected storage destination

### Milestone 3 — playback and dashboard
- Sequential playback only for supported media and authorized catalogue items
- Android media playback
- Dashboard status sync over authenticated local pairing
- Graceful reconnect and clear offline states

## Catalogue record contract

Required fields:
- `id`: stable catalogue identifier
- `title`, `creator`, `description`
- `sourcePublisher`: allowlisted publisher identifier
- `sourcePage`: human-readable source page
- `contentHash`: verified content identifier
- `licenseId`, `licenseUrl`, `attribution`
- `descriptorUrl`: fetched only after signature and publisher validation
- `signature`: publisher signature over canonical manifest bytes

Records missing licence, attribution, publisher identity, or signature are rejected. A publisher allowlist is maintained in the app release/configuration, not supplied by the catalogue response itself.

## Security and privacy requirements

- Never expose an unauthenticated remote control API.
- Never bind the local bridge to 0.0.0.0.
- Pairing codes expire quickly and are single-use.
- Validate all manifest fields and enforce HTTPS.
- Do not log content identifiers, local paths, or pairing secrets unnecessarily.
- Make network, battery, storage, and background-transfer behavior visible to the user.
- Do not claim a source is authorized solely because it is reachable or has peers.

## Licensing

LibreTorrent is GPL-3.0-or-later. This project does not copy LibreTorrent source code or UI assets. libtorrent4j is a separate dependency with its own licence; its current Maven Central metadata identifies it as MIT-licensed. Before distribution, verify the selected version's complete dependency and native-binary notices, provide required notices/source offers, and obtain legal review for the combined distribution.

## Build and release gates

Do not label the Android app production-ready until:
1. Gradle builds reproducibly on CI.
2. Supported Android API levels and ABIs are tested on real devices.
3. Signature verification and publisher allowlist have negative tests.
4. Local pairing is tested against unauthorized and replayed requests.
5. Foreground transfer lifecycle, process death, storage permissions, and thermal/battery behavior are tested.
6. Licence notices and source disclosures are complete.

## Current status

This commit establishes the architecture and acceptance criteria only. It is **not** an Android APK and does not claim that native transfers are implemented yet.
