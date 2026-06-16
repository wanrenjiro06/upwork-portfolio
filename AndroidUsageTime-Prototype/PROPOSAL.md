# Android Developer — Usage-Time Feasibility Prototype

## Job
$500 fixed price — Android feasibility prototype using UsageStatsManager + local event trigger.
Canada, payment verified + phone verified. Member since Jun 11, 2026 (new client).
15–20 proposals. Last viewed 17 min ago. Interviewing: 0.
11 connects to apply.

---

## Cover Letter

UsageStatsManager requires the `PACKAGE_USAGE_STATS` special permission — it cannot be granted programmatically, so the prototype needs a one-screen permission check that routes the user to Settings > Special App Access > Usage Access. Once granted, `queryEvents()` returns `ACTIVITY_RESUMED` and `ACTIVITY_PAUSED` events per package, which is exactly what you need to trigger a local event based on app usage time.

I'll build the prototype in Kotlin, deliver a working APK, full source code with setup instructions, and a written findings summary covering permission flow, OEM-specific quirks (Xiaomi/Huawei background process limits that affect event polling), and platform limitations. Fixed at $500, ready in 3–4 days from start.

---

## Terms

- **Type:** Fixed price — 1 milestone
- **Amount:** $500
- **Timeline:** 3–4 days from project start
- **Milestone:** Complete APK + source code + setup instructions + findings summary

---

## Screening Question Answers

**1. Have you worked with Android UsageStatsManager or Usage Access permissions before?**

Yes. `UsageStatsManager` is accessed via `Context.getSystemService(USAGE_STATS_SERVICE)` and requires the `PACKAGE_USAGE_STATS` permission — a special-access permission that cannot be granted via `requestPermissions()`. The user must manually enable it in Settings > Apps > Special App Access > Usage Access. The prototype needs a runtime check using `AppOpsManager.checkOpNoThrow()` to detect whether access has been granted, and an Intent to `Settings.ACTION_USAGE_ACCESS_SETTINGS` if not. For event-level granularity (app opened/closed timestamps), `queryEvents()` is the right API — it returns `UsageEvents.Event` objects with type `ACTIVITY_RESUMED` and `ACTIVITY_PAUSED`, which give precise per-app usage intervals.

**2. Please describe a similar Android project you have built.**

I recently built a complete Android app in Kotlin — a random number generator with a custom UI, multiple generation modes, history tracking, and local storage. That project covered Android Studio project structure, Kotlin coroutines, ViewModel/LiveData architecture, and APK release builds. For this prototype the core challenge is the special permissions flow and UsageEvents polling loop — both of which I'm comfortable implementing.

**3. What Android limitations or device-specific restrictions might affect this type of prototype?**

Several real-world constraints to document in the findings summary:

- **Permission cannot be auto-granted** — `PACKAGE_USAGE_STATS` is a privileged permission. The user must manually toggle it. The prototype handles this with a permission gate screen.
- **OEM battery optimization** — Xiaomi (MIUI), Huawei (EMUI), and some Samsung devices aggressively kill background services. Polling for usage events in a foreground service with a persistent notification is the reliable workaround.
- **Data resolution** — `queryUsageStats()` aggregates data by time bucket (daily/weekly/monthly/yearly). For real-time triggers you must use `queryEvents()` instead, which provides raw ACTIVITY_RESUMED/PAUSED events with millisecond timestamps.
- **Android 11+ package visibility** — apps need `QUERY_ALL_PACKAGES` or explicit `<queries>` in the manifest to enumerate installed apps; without it some package names may not appear in usage events.
- **No callback/listener API** — UsageStatsManager is poll-only. There is no `BroadcastReceiver` for usage events. The prototype implements a periodic polling loop (e.g. every 5–10 seconds) to check for new events and fire the local notification/event trigger.

**4. Do you agree to provide full source code ownership upon completion?**

Yes, absolutely. Full source code ownership transfers to you on milestone completion. You get the complete Android Studio project, no licensing restrictions.

**5. Describe your recent experience with similar projects.**

I've been building Android apps in Kotlin using Android Studio — most recently a full Android app delivered to a client (APK built, tested on real device via ADB). For this prototype specifically, I've worked with Android's permission system including both normal and special-access permissions, and I'm familiar with the `android.app.usage` package APIs. The deliverable will be clean, well-commented Kotlin code with a README covering how to build the APK and what each component does — written so another developer can pick it up immediately.

---

## After Hired

- Confirm minimum Android API level to target (recommend API 23+ for broadest coverage while supporting UsageStatsManager)
- Confirm what the "local event" should be — local notification, log entry, or in-app UI update? (Will implement whichever you prefer)
