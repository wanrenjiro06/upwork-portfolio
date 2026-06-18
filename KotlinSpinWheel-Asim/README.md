# Spin & Win — Native Android Prize Wheel (Kotlin Custom View)

A production-quality, self-contained `SpinWheelView` for a customer-facing POS
display. This is the **native Kotlin** implementation of the web preview at
**https://spin-and-win-demo.netlify.app** — same visuals, same physics, built
the way you'd actually ship it on Android.

> Drop `SpinWheelView` into any layout. No external graphics libraries, no
> bundled image or audio assets — everything is drawn and synthesized at runtime.

---

## What's inside

| File | Role |
|---|---|
| `SpinWheelView.kt` | The custom `View` — rendering, physics, input, confetti |
| `WheelAudio.kt` | Procedural tick + fanfare sound (synthesized PCM → SoundPool, no asset files) |
| `MainActivity.kt` | Demo host showing both tap-to-spin and server-chosen-prize |
| `res/layout/activity_main.xml` | Sample screen |

---

## How it works (the engineering)

**Pre-rendered face for a cheap frame loop.**
The wheel face (slices, gradients, labels, dividers) is drawn **once** to a
`Bitmap` in `renderFace()` whenever the size or segments change. Every animation
frame then just rotates and composites that bitmap — so a spinning wheel costs
almost nothing per frame and holds 60fps even on low-end POS tablets.

**Realistic deceleration.**
The spin runs on a `Choreographer` frame callback over ~5 seconds using an
`easeOutQuart` interpolator plus 6–8 full turns, so it whips around fast then
eases into a long, satisfying settle — never a robotic linear stop.

**The pointer ticks against the pegs.**
As each slice boundary crosses the top pointer, the pointer gets a physical
kick and settles back via a spring (`flickVel += -22·angle − 6·vel`). A tick
sound plays on the same boundary crossing, and its pitch rises with wheel
speed — the detail that makes it *feel* real.

**Lands exactly where the business decides.**
`spinTo(index)` computes the precise stop rotation so the chosen slice ends up
under the pointer. This is the important part for a real POS — see below.

**Procedural audio, zero assets.**
`WheelAudio` synthesizes the tick blip and the 4-note win fanfare as PCM at
startup, writes them to the app cache as WAV, and plays them through a
`SoundPool` (low-latency, overlapping — ideal for rapid ticks).

**Accessibility & cleanup.**
Honors the system "remove animations" setting (shorter spin, no confetti),
recycles the face bitmap and releases the SoundPool on detach.

---

## Integrating with your POS logic

In a real deployment the **back office decides the prize** — weighted odds,
remaining stock, per-customer limits, fraud rules. The wheel should never pick
the winner itself. So:

```kotlin
val wheel = findViewById<SpinWheelView>(R.id.spinWheel)

// 1. Get notified when the wheel stops, to record the redemption.
wheel.onResult = { index, label ->
    posController.recordReward(index, label)   // print coupon, log, etc.
}

// 2. Your server/business logic picks the prize, the wheel animates to it.
val prizeIndex = rewardEngine.decidePrize(customer)   // your rules
wheel.spinTo(prizeIndex)
```

For a self-serve kiosk you can also just let the customer tap the wheel —
`spin()` (random) is wired to touch by default.

### Customizing the prizes

```kotlin
wheel.segments = listOf(
    SpinWheelView.Segment("FREE COFFEE", Color.parseColor("#E94B5A"), Color.parseColor("#FF7A86")),
    SpinWheelView.Segment("BUY 1 GET 1",  Color.parseColor("#3FB6A8"), Color.parseColor("#74E7D9")),
    SpinWheelView.Segment("15% OFF",      Color.parseColor("#F2A03D"), Color.parseColor("#FFC476")),
    SpinWheelView.Segment("TRY AGAIN",    Color.parseColor("#5A6BD8"), Color.parseColor("#94A3FF")),
    // 3–12 segments supported
)
```

### Public API

```kotlin
var segments: List<Segment>                       // the slices
var onResult: ((Int, String) -> Unit)?            // landed callback (UI thread)
var soundEnabled: Boolean                         // mute for silent kiosks
var respectReducedMotion: Boolean                 // honor accessibility setting
val isSpinning: Boolean

fun spinTo(index: Int)   // production — land on a predetermined prize
fun spin()               // demo — land on a random prize
```

---

## Build & run

```bash
# From this folder:
./gradlew installDebug          # build + install to a connected device
# or open the folder in Android Studio and press Run.
```

- `minSdk 21` (Android 5.0+), `targetSdk 34`, Kotlin, AndroidX only.
- No third-party rendering/animation libraries.

---

## Notes for review

- This mirrors the approved web preview 1:1 in look and feel, so what the client
  saw is what the device shows.
- The same architecture (pre-rendered face + `Choreographer` loop + `spinTo`)
  extends cleanly to the other games discussed (slot reel, pick-a-cup): each is
  a custom `View` with a deterministic "land on the chosen outcome" entry point.
