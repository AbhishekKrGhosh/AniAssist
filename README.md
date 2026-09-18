# AniAssist

### Small help. A kinder tomorrow.

AniAssist is a community-driven Android app designed to help people report injured animals, lost pets, and found animals, while making it easier for nearby people to discover and respond to animal-related cases.

Built as an end-to-end Android project using Kotlin and Jetpack Compose, AniAssist covers UI development, application architecture, Firebase integration, location services, camera workflows, image processing, account management, safety features, and Google Play deployment.

<p align="center">
  <a href="https://play.google.com/store/apps/details?id=abhishek.aniassist">
    <img src="https://img.shields.io/badge/Google%20Play-Live-34A853?style=for-the-badge&logo=google-play" />
  </a>
  <img src="https://img.shields.io/badge/Kotlin-Android-7F52FF?style=for-the-badge&logo=kotlin" />
  <img src="https://img.shields.io/badge/Jetpack%20Compose-UI-4285F4?style=for-the-badge&logo=jetpackcompose" />
  <img src="https://img.shields.io/badge/Firebase-Backend-FFCA28?style=for-the-badge&logo=firebase" />
</p>

---

## 📱 App Preview

<p align="center">
  <img width="1942" height="809" alt="githubreadme" src="https://github.com/user-attachments/assets/3e1944c2-4396-41ca-b9db-36ab3b6f40a9" />
</p>

---

## 🐶 What AniAssist Does

### Report an Animal in Need

Users can report:

- 🩹 Injured animals
- 🐕 Lost pets
- 🏠 Found animals

Reports can include photos, animal type, condition, description, and an exact location.

Photos can be captured directly using the camera or selected from the gallery.

### Discover Cases Nearby

Reports are organized around the user's selected city.

Users can:

- 📍 Detect their current city
- 🗺️ Select a location on a map
- 🔎 Search reported cases
- 🩹 Filter injured cases
- 🐕 Filter lost pets
- 🏠 Filter found animals
- 📄 Open detailed case information
- 🧭 Navigate to reported locations using Google Maps

### Community Verification

Users can submit proofs or sightings for existing reports.

This allows information about a case to be updated by people who encounter the animal after the original report.

### 🐾 Learn & Play

AniAssist includes an animal trivia section covering:

- 🌤️ Air
- 🌱 Land
- 🌊 Water

Users can explore information such as habitat, diet, lifespan, breeds, and interesting facts.

The app also contains small interactive Easter eggs, including shake-to-discover interactions and animated animal experiences.

### 👤 Account & Safety

The application includes:

- Email/password authentication
- Forgot-password flow
- Profile management
- Profile photo using camera or gallery
- Image cropping
- Help & Safety
- In-app safety concern reporting
- Account deletion
- Logout

---

# 🏗️ Technical Overview

AniAssist is a native Android application built with Kotlin and Jetpack Compose.

| Area | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose |
| Design System | Material 3 |
| Navigation | Compose Navigation |
| Architecture | ViewModel + Repository |
| Authentication | Firebase Authentication |
| Database | Firebase Realtime Database |
| Local Preferences | DataStore |
| Maps | Google Maps Compose |
| Location | Google Location APIs + Geocoder |
| Camera | CameraX |
| Image Loading | Coil 3 |
| Image Cropping | uCrop |
| Animations | Lottie |
| Release Optimization | R8 / ProGuard |

---

# 🧩 Architecture

The application follows a layered structure separating UI, navigation, state management, and data access.

```text
                    ┌──────────────────────┐
                    │   Jetpack Compose    │
                    │        UI            │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │     AppViewModel     │
                    │ Session + UI State   │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │ FirebaseRepository   │
                    │     Data Layer       │
                    └──────────┬───────────┘
                               │
                    ┌──────────┴───────────┐
                    ▼                      ▼
          ┌─────────────────┐    ┌──────────────────┐
          │ Firebase Auth   │    │ Realtime Database │
          └─────────────────┘    └──────────────────┘
````

Additional Android integrations are handled directly through the UI/application layers:

```text
Jetpack Compose
      │
      ├── CameraX
      ├── Google Maps Compose
      ├── Geocoder
      ├── Coil 3
      ├── uCrop
      ├── Lottie
      └── DataStore
```

---

# 🧠 Key Engineering Decisions

## Image Processing

Images are processed on the device before being uploaded.

```text
Camera / Gallery
       ↓
Resize
       ↓
JPEG Compression
       ↓
Base64 Encoding
       ↓
Firebase Realtime Database
```

Report images are downscaled to a maximum of 1024px and profile images to 512px before upload.

This was a deliberate architectural choice to avoid using Firebase Storage for the current application.

---

## 📷 Camera Integration

CameraX provides in-app image capture with context-specific camera selection.

* Rear camera for animal reports
* Front camera for profile selfies
* Gallery selection as an alternative

The camera workflow is integrated directly into the Compose application.

---

## 🗺️ Location & Maps

Location functionality combines:

* Google Maps Compose
* Google location APIs
* Geocoder
* Reverse geocoding
* Map pin selection
* Google Maps navigation intents

This allows reports to store both geographic coordinates and readable location information.

---

## 🖼️ Image Handling

The application uses:

* CameraX for image capture
* Coil 3 for image loading
* uCrop for profile image cropping
* Client-side image resizing
* JPEG compression
* Base64 encoding

This reduces the size of images before they are written to the database.

---

## 🔐 Authentication & Security

AniAssist uses Firebase Authentication for email/password authentication and Firebase Realtime Database for application data.

Security considerations include:

* Authenticated database access
* Firebase Realtime Database security rules
* Environment-based configuration
* No committed signing credentials
* No sensitive configuration committed to the repository
* R8/ProGuard release optimization
* Firebase model keep rules for release builds

API keys, database configuration, and signing credentials are injected through build configuration rather than committed directly to the repository.

---

# ⚙️ Production Considerations

The project also includes production-oriented Android practices such as:

* Release builds with R8/ProGuard
* Firebase model keep rules
* Client-side image optimization
* Reusable Compose components
* Centralized application state
* Persistent local preferences using DataStore
* Environment-based configuration
* Google Play production deployment

---

# 🚀 Getting Started

## Requirements

* Android Studio
* JDK
* Android SDK
* Firebase project
* Google Maps API configuration

## Clone the repository

```bash
git clone https://github.com/AbhishekKrGhosh/AniAssist.git
cd AniAssist
```

Open the project in Android Studio and allow Gradle to sync.

Configure the required Firebase and Google Maps credentials before running the application.

---

# 📲 Google Play

AniAssist is available on Google Play.

<p align="center">
  <a href="https://play.google.com/store/apps/details?id=abhishek.aniassist">
    <img src="https://img.shields.io/badge/Download%20AniAssist-Google%20Play-34A853?style=for-the-badge&logo=google-play" />
  </a>
</p>

---

# 📌 Project Status

AniAssist is currently published on Google Play and actively maintained.

The repository is shared publicly for portfolio and technical review purposes.

**This project is not open source. Please do not reuse or republish the source code without permission.**

---

# 👨‍💻 Author

## Abhishek Kumar

Android / React Native Developer

* GitHub: [https://github.com/AbhishekKrGhosh](https://github.com/AbhishekKrGhosh)
* LinkedIn: [https://linkedin.com/in/abhishek-ghosh-dev](https://linkedin.com/in/abhishek-ghosh-dev)

---

<p align="center">
  🐾 <strong>Because every life matters.</strong>
</p>

