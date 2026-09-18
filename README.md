# AniAssist

A community-driven Android app for helping animals in need — report injured
animals, lost pets, and found animals, and let nearby people respond.

📱 [AniAssist on Google Play](https://play.google.com/store/apps/details?id=abhishek.aniassist)

<img width="1942" height="809" alt="githubreadme" src="https://github.com/user-attachments/assets/3e1944c2-4396-41ca-b9db-36ab3b6f40a9" />

---

## What the app does

### Report an animal in need

- **Injured animals** — snap a photo in-app (or pick from gallery), describe the
  problem, pick the animal type and condition, and pin the exact spot on a map
- **Lost & found pets** — the same guided flow tuned for lost and found cases
- **Community verification** — other users can submit proof/sightings for a
  reported case

### Discover cases nearby

- **City-based feed** — reports are grouped by city with Injured / Lost / Found
  filters and text search
- **Detail pages** — photo hero, status badges, and every piece of info in
  clearly styled cards
- **Embedded map + one-tap navigation** — jump straight into Google Maps
  directions to the reported location

### Learn & play

- **Animal Kingdom trivia** — Air / Land / Water categories with facts, habitat,
  diet, lifespan, and breed details
- **Easter eggs** — shake-to-discover, a wandering dog animation, a dog-bark
  sound bite on the profile page

### Account & safety

- Email/password auth with forgot-password flow
- Profile photo via front-camera selfie or gallery, with circular crop
- **Help & Safety** — in-app child-safety concern reporting (Google Play child
  safety standards compliant)
- **In-app account deletion** — removes profile data and auth account
  (Play account-deletion policy compliant)

---

## Technical overview

- **Kotlin + Jetpack Compose** — single-activity, Compose Navigation,
  Material 3 design system with a custom illustrated visual language
- **Firebase Auth** — email/password session management
- **Firebase Realtime Database** — feeds, user profiles, proofs, and safety
  reports; security rules scoped to authenticated users
- **Images stored as base64 in RTDB** — a deliberate billing-free alternative
  to Firebase Storage, with client-side downscaling (≤1024px reports / ≤512px
  avatars) + JPEG compression before upload
- **Google Maps Compose + Geocoder** — auto city detection, map pin picker,
  reverse-geocoded addresses, `google.navigation` intents
- **CameraX** — in-app capture with front/back lens selection per context
  (selfie for avatars, rear camera for reports)
- **Coil 3** — image loading with crossfade; placeholders + fade-in for
  base64-decoded images
- **uCrop** — avatar cropping via Activity Result contracts
- **Lottie** — lightweight ambient animations
- **R8/ProGuard** — release builds minified with keep rules for Firebase model
  deserialization
- **Secrets handled correctly** — API keys, DB URL, and signing credentials
  injected via `.env` → `BuildConfig`/manifest placeholders; nothing sensitive
  is committed

### Architecture

```
data/        models, FirebaseRepository, DataStore prefs, trivia data
navigation/  Screen routes + NavHost
ui/components/  shared detail scaffold, form components, image loader
ui/screens/     auth, home feed, post/lost/found, verify, profile,
                trivia, location picker, camera capture, onboarding
viewmodel/      AppViewModel — session + cross-screen state
```

---

## Notes

Source is shared publicly **for portfolio/review purposes**.
Not open source — please don't reuse or republish it.
