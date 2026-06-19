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

---

## Birthday Celebration (game #2)

A second self-contained custom `View` for the same customer display: a full
**Happy Birthday** celebration the POS fires when a loyalty profile says it's
the customer's birthday. Landscape-oriented (per the client's display).

| File | Role |
|---|---|
| `BirthdayCelebrationView.kt` | The custom `View` — cake, candles, flames, balloons, confetti, sparkles, headline text, state machine |
| `BirthdayAudio.kt` | Procedural "Happy Birthday" melody + blow-out / finale SFX (synthesized PCM → SoundPool, no asset files) |
| `BirthdayActivity.kt` + `res/layout/activity_birthday.xml` | Demo host (name field + Celebrate button) |

**Web preview:** the approved look is the demo in `web-demo-birthday/index.html`
(landscape) — the native view matches it 1:1.

**The show (state machine):**
`INTRO` (headline kinetics + balloons rise + opening confetti + tune) →
`LIT` (candles flicker, balloons drift — the calm resting display) →
tap the cake → `BLOWING` (candles blow out left→right with smoke wisps) →
`FINALE` ("Make a wish!" + confetti waves) → back to `LIT` (relit), so the
display stays alive and re-tappable.

**Same performance model as the wheel.** Everything static is pre-rendered once
to a `Bitmap` — the cake (plate, tiers, frosting, drips, sprinkles, candle
bodies), the balloon bodies, the sparkle glyph and the flame halo. Each frame
only blits those bitmaps and draws the cheap live bits (flame paths, confetti,
motion): no per-frame gradient allocation, no shadow layers in the loop → holds
60fps on a low-end POS tablet.

**Production hook (mirrors `spinTo`):**

```kotlin
val birthday = findViewById<BirthdayCelebrationView>(R.id.birthdayView)
birthday.onComplete = { posController.returnToIdleScreen() }   // after the finale
birthday.celebrate(customer.firstName)                          // play the show
```

Public API: `celebrate(name)`, `blow()`, `onComplete`, `soundEnabled`,
`respectReducedMotion`. Honors the system "remove animations" setting and
recycles all bitmaps / releases audio on detach.

---

## Lucky Spin Slots (game #3)

A third self-contained custom `View`: a slot machine the customer plays for a
reward. Landscape.

| File | Role |
|---|---|
| `SlotMachineView.kt` | The custom `View` — cabinet, 3 reels, symbols, lever, payline, chase lights, confetti |
| `SlotAudio.kt` | Procedural reel tick / clunk / lever / win-fanfare (synthesized PCM → SoundPool, no asset files) |
| `SlotActivity.kt` + `res/layout/activity_slot.xml` | Demo host (Spin button + result line) |

**Web preview:** the approved look is `web-demo-slot/index.html` — the native
view matches it 1:1.

**How it plays.** Tap the machine (or the Spin button) to pull the lever; the
three reels spin and stop one-by-one with a tick per symbol and a clunk on each
lock. Six symbols (7 · ★ · BAR · cherry · bell · diamond). Three matching on the
payline = a win → flashing payline + chase lights + confetti + fanfare + the
prize.

**Same performance model.** The cabinet and every symbol tile are pre-rendered
once to `Bitmap`s; each frame only blits the visible tiles per reel and draws
the cheap live bits (lever, lights, confetti).

**Production hook (mirrors `spinTo`):** the back office decides the outcome:

```kotlin
val slot = findViewById<SlotMachineView>(R.id.slotMachine)
slot.onResult = { symbols, isWin, prize -> if (isWin) pos.recordReward(prize) }
slot.spinTo(rewardEngine.decideReels(customer))   // land server-chosen symbols
```

Public API: `spinTo(IntArray)`, `spin()`, `pullLever()`, `onResult`,
`soundEnabled`, `respectReducedMotion`. Reels land *exactly* on the chosen
symbols — the machine never decides the prize itself.

---

## Find the Ball (game #4 — shell game)

A fourth self-contained custom `View`: the classic cups-and-ball shell game.
Landscape.

| File | Role |
|---|---|
| `ShellGameView.kt` | The custom `View` — cups, ball, reveal/shuffle/pick/lift state machine, confetti, prompt |
| `ShellAudio.kt` | Procedural shuffle-whoosh / lift / win-fanfare / lose tone (synthesized PCM → SoundPool, no asset files) |
| `ShellActivity.kt` + `res/layout/activity_shell.xml` | Demo host (New Round button + result line) |

**Web preview:** the approved look is `web-demo-shell/index.html` — the native
view matches it 1:1.

**How it plays.** The cups lift to reveal which one hides the ball, drop, then
shuffle in quick arcs (speeding up). The customer taps a cup; it lifts to reveal
a win (ball underneath → confetti + fanfare + prize) or a miss.

**Same performance model.** The cup and the ball are pre-rendered once to
`Bitmap`s; each frame only blits them at computed positions and draws the cheap
live bits (shadows, confetti).

**Production hook (mirrors `spinTo`).** The customer always gets to pick, but the
*outcome* is the back office's decision — the reveal is rigged to it, so the
business keeps full control of odds/cost (the standard controlled shell game):

```kotlin
val shell = findViewById<ShellGameView>(R.id.shellGame)
shell.onResult = { won, prize -> if (won) pos.recordReward(prize) }
shell.newRound(shouldWin = rewardEngine.decide(customer), prize = "Free Coffee")
```

Public API: `newRound(shouldWin, prize)`, `onResult`, `onPrompt`, `soundEnabled`,
`respectReducedMotion`. Call `newRound()` with no args for a weighted-random demo
outcome.

---

## One app, one menu

All four games ship in a single app behind a landscape home screen
(`HomeActivity` + `res/layout/activity_home.xml`) — a tile per game. It's the
only launcher icon ("POS Games"); each tile opens its game. A real POS shell can
also launch any game directly (e.g. fire the birthday celebration when a loyalty
profile says it's the customer's birthday).

---

## Configuration & styling (modular — easy to edit)

Two layers, both code-only (no rebuild gymnastics — just change a value):

**1. Global, one place — `GameStyle.kt`.** The shared brand palette and a single
speed knob:

```kotlin
GameStyle.gold = Color.parseColor("#FFC400")   // re-tints every game's gold accents
GameStyle.blue = Color.parseColor("#2E90C4")
GameStyle.speedFactor = 1.5f                    // 1.0 = normal, 2.0 = twice as fast (all games)
```

**2. Per-game options — a `CONFIG` block at the top of each View.** Each is a
public property with a sensible default:

| Game | Editable options |
|---|---|
| `SpinWheelView` | `segments` (label+colours+prizes), `spinDurationMs`, `extraSpins` |
| `SlotMachineView` | `symbols` (label/colour/prize), `reelBaseDurationMs`, `reelStaggerMs`, `demoWinChance` |
| `BirthdayCelebrationView` | `candleCount`, `bottomTierColor`/`Frost`, `topTierColor`/`Frost`, `balloonColors`, `kickerText`, `titleText` |
| `ShellGameView` | `cupColor`, `shuffleCount`, `swapDurationMs`, `demoWinChance`, `prizes` |

Example — restyle and re-time the slot machine:

```kotlin
slot.symbols = arrayOf(
    SlotMachineView.Symbol("seven", Color.RED, "JACKPOT — Free Meal"),
    /* …six entries… */
)
slot.reelBaseDurationMs = 1200f   // snappier reels
slot.demoWinChance = 0.5f
```

Sizing scales automatically to the view's bounds, so the same code looks right on
any display resolution. All four also honour `soundEnabled` and
`respectReducedMotion`.

---

## Orientation

All four games run **landscape** to match the POS customer display (the wheel's
`MainActivity` was switched from portrait to landscape).
