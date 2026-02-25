# Dereva Smart - Native Android App

NTSA Driving Theory Test preparation app built with Kotlin and Jetpack Compose.

## Features

- **Mock Tests**: 50-question practice tests with 80% pass mark
- **Progress Tracking**: Monitor study time, streaks, and achievements
- **AI Tutor**: Gemini-powered assistant for instant answers (English & Swahili)
- **Offline-First**: Room database for local data storage
- **Clean Architecture**: Domain, Data, and Presentation layers

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Architecture**: MVVM + Clean Architecture
- **Database**: Room
- **DI**: Koin
- **Networking**: Retrofit + OkHttp
- **AI**: Google Gemini API
- **Async**: Kotlin Coroutines + Flow

## Project Structure

```
app/src/main/java/com/dereva/smart/
├── domain/
│   ├── model/          # Domain entities
│   └── repository/     # Repository interfaces
├── data/
│   ├── local/          # Room database, DAOs, entities
│   ├── remote/         # API services
│   └── repository/     # Repository implementations
├── ui/
│   ├── screens/        # Compose screens
│   ├── navigation/     # Navigation setup
│   └── theme/          # Material theme
└── di/                 # Dependency injection
```

## Setup

1. Clone the repository
2. Add your Gemini API key in `di/AppModule.kt`
3. Sync Gradle dependencies
4. Run on Android device/emulator (API 26+)

## Build

```bash
./gradlew assembleDebug
```

## Test

```bash
./gradlew test
./gradlew connectedAndroidTest
```

## Requirements

- Android Studio Hedgehog or later
- Kotlin 1.9.20+
- Min SDK: 26 (Android 8.0)
- Target SDK: 34 (Android 14)

## License

Proprietary - Dereva Smart © 2024
