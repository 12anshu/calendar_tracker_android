# Smart Expense Calendar

Smart Expense Calendar is a production-ready Android application built with Kotlin and Jetpack Compose. It helps users track their expenses manually and automatically by detecting financial SMS messages.

## Features

- **Google Calendar Style Home Screen**: Monthly view with daily expense indicators and SMS detection highlights.
- **AI-Based SMS Detection**: Automatically parses financial SMS to extract amount and merchant, and categorizes them intelligently.
- **Monthly Summary**: Visual breakdown of expenses by category with a simple bar chart.
- **Export/Import**: Export data to CSV or JSON; import data from JSON using Android Storage Access Framework.
- **Offline-First**: All data is stored locally using Room Database.
- **Privacy Focused**: All SMS processing happens on-device.
- **Search & Filter**: Easily find expenses by category or merchant.
- **Material 3 UI**: Modern, clean design with Dark Mode support.

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM + Clean Architecture + Repository Pattern
- **Dependency Injection**: Hilt
- **Database**: Room
- **Background Tasks**: WorkManager
- **Preferences**: DataStore
- **Navigation**: Compose Navigation

## Setup Instructions

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   ```
2. **Open in Android Studio**:
   - Use Android Studio Hedgehog or newer.
   - Sync the project with Gradle files.
3. **Permissions**:
   - The app requires `READ_SMS` and `RECEIVE_SMS` permissions to function correctly.
   - Grant these permissions when prompted on the first launch.
4. **Testing SMS Detection**:
   - You can send mock SMS to the emulator using the "Extended Controls" menu.
   - Example SMS: "Spent Rs 250 on Swiggy", "Paid ₹450 to Uber".
5. **Build & Run**:
   - Select the `app` configuration and click **Run**.

## Project Structure

- `data/`: Room database, entities, DAOs, and repository implementations.
- `domain/`: Business logic models and repository interfaces.
- `presentation/`: ViewModels and UI state management.
- `ui/`: Compose screens, components, and themes.
- `sms/`: SMS parsing and categorization engine.
- `utils/`: Export/Import and other utility classes.

## License

This project is licensed under the MIT License.
