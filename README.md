# AniAssist

An Android app for reporting and helping animals in need — injured animals, lost pets, and found animals — organized by city, with community verification and an animal-knowledge trivia section.

Published on Google Play: https://play.google.com/store/apps/details?id=abhishek.aniassist

## Features

- **Report Injured Animal** — photo (camera/gallery), problem, animal type, condition, exact map location
- **Report Lost / Found Pet** — same flow, tuned for lost and found animals
- **Verify sightings** — community proof submission for reported cases
- **Nearby cases feed** — city-based feed with filters, search, and embedded maps + "Navigate to location"
- **Profile** — avatar (selfie/gallery with crop), name/city editing, Help & Safety reporting, account deletion
- **Animal Kingdom trivia** — Air / Land / Water animals with facts and breeds
- **Auth** — email/password sign-up, login, forgot password

## Tech Stack

- **Kotlin + Jetpack Compose** (Material 3), single-activity navigation
- **Firebase Auth** — email/password accounts
- **Firebase Realtime Database** — all data + base64 image storage (no Storage/billing needed)
- **Google Maps Compose + CameraX + Coil + Lottie**
- **uCrop** for avatar cropping, **Photo Picker** for gallery images

## Setup

This repo intentionally excludes secrets. To build, create the following:

### 1. `google-services.json`

Place your Firebase config file at `app/google-services.json`
(Firebase Console → Project settings → Your apps).

### 2. `.env` in the project root

```env
# Google Maps API key
MAPS_API_KEY=your_maps_api_key

# Firebase Realtime Database URL
FIREBASE_DB_URL=https://<project>-default-rtdb.firebaseio.com

# Release signing (only needed for release builds)
KEYSTORE_PASSWORD=your_keystore_password
KEY_ALIAS=your_key_alias
KEY_PASSWORD=your_key_password
```

### 3. Signing keystore (release builds only)

Place your upload keystore at `app/aniassist-upload.jks` (or update the path in
`app/build.gradle.kts` → `signingConfigs`).

## Build

```bash
./gradlew assembleDebug     # debug APK  → app/build/outputs/apk/debug/app-debug.apk
./gradlew assembleRelease   # release APK → app/build/outputs/apk/release/app-release.apk
./gradlew bundleRelease     # release AAB → app/build/outputs/bundle/release/app-release.aab
```

## Firebase Database Structure

```
Users/{sanitizedEmail}/           → name, avatar ref, Proof submissions
Location/{city}/Post|Lost|Found/  → animal reports per city
Images/...                        → base64 image blobs (compressed ≤1024px)
SafetyReports/{id}                → in-app safety concern reports
```

Required Realtime Database rules:

```json
{
  "rules": {
    "Users":         { ".read": "auth != null", ".write": "auth != null" },
    "Location":      { ".read": "auth != null", ".write": "auth != null" },
    "Images":        { ".read": "auth != null", ".write": "auth != null" },
    "SafetyReports": { ".read": false,          ".write": "auth != null" }
  }
}
```

## Project Structure

```
app/src/main/java/abhishek/aniassist/
├── data/            # models, repository, prefs, trivia data
├── navigation/      # Screen routes + NavHost
├── sensor/          # shake detector
├── ui/
│   ├── components/  # shared form/detail/image components
│   ├── screens/     # auth, home, post/lost/found, verify,
│   │                # profile, trivia, location, camera
│   └── theme/       # colors, typography
└── viewmodel/       # AppViewModel
```

## Notes

- `versionCode`/`versionName` in `app/build.gradle.kts` must be bumped for each release.
- The Maps API key should be restricted in Google Cloud Console by package name
  `abhishek.aniassist` + the SHA-1 fingerprints of your debug and release certs.
- Image uploads are downscaled and JPEG-compressed before being stored as base64
  in the Realtime Database.
