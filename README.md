# 🎬 CineCompare — Movie Ticket Booking Aggregator

CineCompare is a production-grade Android application designed with modern Clean Architecture and Jetpack Compose. It aggregates live movie show listings, seat availability, and ticket pricing from multiple cinema booking platforms across India, allowing moviegoers to compare prices and book through official channels at the best available rate.

---

## 🌟 Key Features

1. **Multi-Platform Price Comparison**:
   - Compare identical movie showtimes across **BookMyShow**, **District**, and **TicketNew**.
   - Automatic highlight for the cheapest platform showing total savings (e.g., *"₹50 cheaper on District"*).
   - Real-time seat availability indicator: `Available` 🟢, `Filling Fast!` 🟡, `Almost Full` 🟠, or `Housefull` 🔴.

2. **Zero-Friction Deep-Link Booking**:
   - Tapping **[Book]** opens the respective platform's installed Android app directly at the show's checkout page.
   - Graceful fallback to the official mobile web checkout if the app is not installed.
   - User security: 100% private. The app never intercepts user credentials or payment details.

3. **Smart City & Date Selector**:
   - Pre-configured support for 10 major metro regions: Mumbai, Delhi NCR, Bengaluru, Hyderabad, Chennai, Kolkata, Pune, Ahmedabad, Jaipur, and Lucknow.
   - Multi-day calendar strip with real-time show synchronization.

4. **Background Price Drop Alerts**:
   - Integrated `WorkManager` background worker periodically monitors watchlisted titles.
   - Instant Android system notification whenever ticket prices drop for saved movies.

5. **Offline First Architecture**:
   - Powered by Room database with stale-while-revalidate caching.
   - Full browsing and watchlist support when offline.

---

## 🏗️ Architecture & Tech Stack

```
CineCompare/
├── app/                          # Application entry point, Navigation host, Hilt configuration
├── core/
│   ├── core-ui/                  # Material 3 Design System, Cinema theme, typography, components
│   ├── core-network/             # OkHttp client factory, interceptors, user agent rotation
│   ├── core-database/            # Room entities (MovieEntity, ShowListingEntity), DAOs, Database
│   └── core-common/              # Functional Result<T> monad, safeApiCall, DateTime & Currency formatters
├── aggregator/
│   ├── aggregator-base/          # Common aggregator interfaces, Platform enum, Movie & Show models
│   ├── aggregator-bookmyshow/    # BookMyShow JSON API adapter
│   ├── aggregator-district/      # District (Paytm Movies) REST adapter
│   └── aggregator-ticketnew/     # TicketNew HTML scraper & adapter
└── feature/
    ├── feature-home/             # Home feed, Movie grid, City selector bottom sheet
    ├── feature-movie-detail/     # Synopsis, ratings, formats, Cast carousel
    ├── feature-compare/          # Unified comparison engine, savings calculation, deep linking
    ├── feature-search/           # Debounced multi-platform search with trending suggestions
    └── feature-profile/          # Watchlist manager, price drop toggle, WorkManager integration
```

### Tech Stack Details:
- **Language**: Kotlin 2.0 (100%)
- **UI Framework**: Jetpack Compose + Material 3
- **Dependency Injection**: Dagger Hilt with Multibindings (`@IntoSet Set<MovieAggregator>`)
- **Persistence**: Room 2.6.1 + KSP
- **Networking**: OkHttp 4.12, Retrofit 2.11, Jsoup
- **Image Loading**: Coil 2.7.0
- **Background Tasks**: AndroidX WorkManager + Hilt Worker

---

## 🚀 Building & Running

### Prerequisites
- Android Studio Ladybug / Meerkat or newer
- JDK 17
- Android SDK 35 (minSdk 24)

### Build Commands
```powershell
# Open project directory
cd C:\Users\ASHIR\.gemini\antigravity\scratch\CineCompare

# Build debug APK
./gradlew assembleDebug

# Run unit tests across all modules
./gradlew test
```
