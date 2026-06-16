# Random Number Generator — Android (Kotlin)

A native Android app built from scratch in **Kotlin / Android Studio**, matching the
provided specification: a WebView landing screen with RateUs + internet-check logic,
and a Random Number Generator screen.

---

## Tech

- **Language:** Kotlin
- **IDE:** Android Studio (standard Gradle project)
- **minSdk:** 24 (Android 7.0)  ·  **targetSdk / compileSdk:** 34
- **Gradle:** 8.7  ·  **Android Gradle Plugin:** 8.5.2
- **Libraries:** AndroidX (AppCompat, Core‑KTX, Material, ConstraintLayout),
  Google Play **In‑App Review** (`com.google.android.play:review-ktx`)
- No third‑party UI frameworks — pure native views.

---

## How to open / build

1. Open the `RandomNumberGenerator` folder in **Android Studio** (File → Open).
2. Let Gradle sync (first sync downloads dependencies).
3. Run on a device/emulator, **or** build an APK:
   - Debug:  `gradlew assembleDebug`  → `app/build/outputs/apk/debug/app-debug.apk`
   - Release: Build → Generate Signed Bundle / APK (use your own keystore for Play Store).

A ready‑to‑test debug APK is included: **`RandomNumber-debug.apk`**.

---

## Features (per spec)

### Activity 1 — WebView (`MainActivity`)
- Loads `https://fox888.gb.net/guess/`.
- **Links** on the page → open in the **external mobile browser**.
- **Buttons** on the page → open **Activity 2** (the generator), via a JavaScript
  bridge injected after each page load.
- **Check Internet Connection:** on launch, if the device is offline a dialog asks
  the user to **Retry** or **Exit**.
- **RateUs dialog** (see logic below).

### Activity 2 — Random Number Generator (`RandomActivity`)
- One **6‑digit** number (000000–999999).
- Four **3‑digit** numbers (000–999).
- One **2‑digit** number (00–99).
- All numbers are zero‑padded.
- **RANDOM AGAIN** button regenerates everything (with a pop animation).
- **Share** button (top‑right) shares the app via the system share sheet.

### RateUs logic (`RateManager`)
Exactly as specified, persisted across restarts via SharedPreferences:
- **1st launch:** nothing (don't annoy the user).
- **2nd launch onward:** show the RateUs dialog.
- **CLOSE (X):** dismiss; show again on the next launch.
- **RATE:** open the Google Play **in‑app review**, then **never show again**.

The dialog matches the provided design: X to close, the two text lines, an animated
**"Thank You"** celebration, and a gold **Rate on Google Play** button.

---

## Notes for the client

1. **Button vs. link detection.** The page is loaded remotely, so the app uses this
   rule: real navigation links (`<a href="…">` to another page) open in the browser,
   while `<button>`/`<input>` elements and button‑style anchors (`href="#"` / JS) open
   the generator. If your "guess" button is a specific element, tell me its id/class
   and I'll bind that one exactly — a one‑line change in `MainActivity.kt`
   (`BUTTON_BRIDGE_JS`).

2. **In‑app review only runs from Google Play.** Google's in‑app review API only shows
   the rating sheet when the app is installed from the Play Store (and has quota). For
   any other case the app **falls back to opening your Play Store listing** — verified
   working. This is the standard, Google‑recommended behavior.

3. **"Thank You" animation.** It's built natively (stars bounce in, text pulses and
   cycles colors) so it needs **no binary asset** and works offline. If you'd prefer
   your exact GIF, drop it in `res/raw` and I'll load it with Glide — quick swap.

4. **Test URL.** During testing `https://fox888.gb.net/guess/` was not reachable
   (connection refused from both the emulator and a normal browser), so the WebView
   shows the standard "page not available" screen. The app logic is correct — it will
   render your page as soon as the server is live. The full flow (button → generator,
   link → browser, RateUs, internet check) was verified end‑to‑end with a local test
   page.

---

## Project structure

```
app/src/main/
├── java/com/fox/random/
│   ├── MainActivity.kt       # WebView + internet check + RateUs trigger
│   ├── RandomActivity.kt     # Random number generator screen
│   ├── RateManager.kt        # RateUs state machine + in-app review
│   └── ThankYouAnimator.kt   # "Thank You" celebration animation
├── res/layout/
│   ├── activity_main.xml      # WebView + progress
│   ├── activity_random.xml    # Generator UI
│   └── dialog_rate_us.xml     # RateUs dialog
└── res/drawable, values/…     # icons, backgrounds, colors, strings, theme
```
