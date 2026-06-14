# Claude's Capabilities for UpWork Jobs

## What I CAN do

### Mobile Development
- React Native components, screens, and full apps
- Android XML layouts (fixing constraints, padding, spacing, cards)
- Flutter UI widgets and screens
- Styling, theming, button variants, form inputs
- Password toggles, modals, navigation, lists
- Build & compile APK locally (production release builds)
- Payment integration (Razorpay, PhonePe, Stripe, Google Play IAP)
- Offline databases (SQLite, op-sqlite) with local-first architecture
- State management, deep linking, referral systems

### Web Development
- HTML, CSS, JavaScript, TypeScript
- React, Next.js, Vue
- REST API integration (frontend side)
- Responsive layouts and styling
- Animated gradients, scroll-reveal animations, interactive components
- Static site hosting (Netlify, GitHub Pages)

### Backend / General
- Node.js, Python, Java, C++ (logic and APIs)
- Database queries (SQL)
- Writing proposals and cover letters
- Analyzing job requirements and planning work
- Systems programming (architecture, design, algorithms)

---

## What I CANNOT do

- **Use Figma, Adobe XD, or any design tool** — I write code, not design files
- **Build in visual no-code/low-code platforms** — Bubble.io, Adalo, Glide, FlutterFlow, Webflow, etc. I have no access to these drag-and-drop builders. (I can write raw Flutter/React/Node code, but not build *in* their visual editors.)
- **Access the internet or download client repos** — client must share files directly
- **Jobs requiring a live video call or screen share**
- **Machine learning model training** — inference/integration yes, training no

---

## Before you apply: Job Screening Checklist

**Ask these questions to decide if I can do a job (Claude does 100% of the work, you handle only communication/approvals):**

1. **Is the core work code-based, or is it stuck in a no-code platform?**
   - ✅ Code-based (React, React Native, Flutter, Node.js, Python, vanilla JS/HTML/CSS, native Android/iOS) → I can do it
   - ❌ Locked to visual platform (Bubble, Adalo, Glide, FlutterFlow, Webflow — their visual editors) → I cannot do it
   - ⚠️ No-code tool with API/code escape hatch → Possible, but clarify first

2. **Difficulty level for Claude (what's the actual technical work)?**
   - ⭐ Easy (1–5 hours): Bug fixes, CSS tweaks, simple features, copy updates
   - ⭐⭐ Medium (5–20 hours): Feature implementation, API integration, testing, layout work, performance fixes
   - ⭐⭐⭐ Hard (20–80 hours): Full app builds, complex architecture, ML/real-time systems, DevOps, large refactors
   - ❌ Very hard: Anything requiring specialized hardware (games w/ 3D graphics, AR), specialized cloud infra you don't have credentials for, or custom device drivers
   - **Apply to jobs at any difficulty level, but factor time into budget**

3. **Are there manual clicks that you (the user) must do? (These are OK if they're <5 min)**
   - ✅ "Click Submit on Play Store" (1 min) → I build & prepare, you click Submit
   - ✅ "Test on your physical device" (10 min) → I build APK, you test & verify
   - ❌ "Daily standup sync" or "review my code regularly" → That's ongoing user participation, skip it
   - ⚠️ "I'll send you screenshots of what I want" (requires multiple back-and-forth) → Only if scope is clear

4. **Communication: Chat-only, no live calls?**
   - ❌ Job requires live video calls, screen shares, real-time meetings, or daily standups → Skip it
   - ✅ Async communication only (messages, email, Discord, Upwork chat) → Apply
   - ⚠️ One-time onboarding call (30 min, just to clarify scope)? → Can accept, but note async preference in proposal

5. **Access: Can I get the files and credentials I need?**
   - ❌ "Download our private GitHub" (I can't access internet URLs) → Ask client to share files/zip directly, or invite you as collaborator on a repo they share access to
   - ❌ "You'll need Firebase/AWS access" without client providing credentials → Request credentials from client
   - ✅ Client can share source code, .zip files, credentials via Upwork → Can do it

6. **Does this align with Islamic values?**
   - ❌ Explicitly haram: interest-based lending, alcohol, gambling, deception, scams, unlicensed gambling/lotteries, un-halal food promotion
   - ❌ Work that conflicts with your conscience
   - ❌ **Clients from Israel** — Do NOT accept or proceed with jobs from Israeli clients, even if accidentally applied
   - ✅ Neutral technical work: building tools, apps, APIs for legitimate businesses (even if they serve other religions)
   - ⚠️ Grey area? Use judgment. Building a verification app for Hindu restaurants is technical work. Apps facilitating haram (betting odds, loan calc for interest loans) are haram. If unsure, skip it.

**Decision rule:**
- ✅ **Apply if:** Code-based, doable difficulty, async-only or minimal user clicks, files/access available, ethical
- ❌ **Skip if:** No-code platform, locked scope, requires your active participation beyond communication, haram
- ⚠️ **Ask first if:** Scope unclear, difficulty/hours uncertain, or client needs clarification

---

## How this works: Your role vs. Claude's role

**Claude (me) does — ALL the literal work:**
- All technical work (code, debugging, fixes, architecture, database design, etc.)
- Write the proposal, cover letter, terms, screening answers
- Build, compile, optimize (within my tool limits)
- Test on Android device via adb/USB
- For React Native/Flutter: Write code for iOS + Android, but test Android only
- Draft client replies and communication
- **Portfolio is built on the quality of this work**

**You do — Business + Communication + iOS Testing:**
- Review & approve proposals before I submit them
- Handle client communication (accept/decline offers, send replies via Upwork)
- Accept payment when milestones are complete
- Grant access when needed (GitHub collaborator, adb, etc.)
- Connect Android phone via USB when testing needed
- **For iOS apps: Test iOS yourself OR hire Mac-based dev for iOS testing**
- Final verification/sign-off on deliverables
- End contract & leave reviews

**Key rule:** Only apply to jobs where I can do 100% of the technical work from my end. If a job requires you to do technical work (compile code, test on real device without adb, use tools I can't access), flag it first.

**Portfolio strategy:** Since I do all the technical work, the portfolio grows through quality delivery. Lower pricing now + excellent work = strong portfolio + future higher rates.

---

## Using scrcpy for Android Testing & Demos

**What scrcpy enables:**
- Mirror Android phone screen to your computer
- I can see and interact with the phone display via computer-use
- Watch videos/demos on the phone
- Test apps in real-time
- Verify UI/UX during development

**How to use:**
1. Connect Android phone via USB
2. Enable USB Debugging (if not already)
3. Run `scrcpy` in terminal (it mirrors the phone screen)
4. I use computer-use to view/interact with the mirrored display
5. Use for: Testing, demos, watching videos, UI verification

**When this is useful:**
- Client demos app functionality on their phone → I can see it
- Need to test React Native/Flutter app on real device → I can see results
- Client wants to show a video on their phone → I can watch it via mirror

---

## Clarification: Cross-Platform Apps (React Native / Flutter)

**When a job says "iOS and Android":**
- Often means: App should work on both platforms (not necessarily upload to both stores)
- I CAN write React Native/Flutter code for both iOS + Android ✅
- I CAN test Android (via adb) ✅
- I CANNOT test iOS (no macOS) ❌
- **In proposals:** Clearly state iOS testing requirement upfront. Example: "I'll develop and test Android fully. iOS testing must be done on your end or by hiring a Mac-based developer."

**For jobs requiring iOS App Store upload:**
- I cannot directly upload (no account access, no macOS)
- You can: Upload yourself (I guide you) or hire someone with Mac for that step

## How to use me for UpWork jobs

1. Share the client's job post or screenshots here
2. I screen it against the checklist (can I do it? is it worth it? chat-only? halal?)
3. If ✅ all green, I write a PROPOSAL.md ready to copy-paste
4. You review & submit the proposal to the client
5. Once hired, I do all the technical work; you handle client comms
6. I deliver the code/deliverables in the job folder
7. You submit/verify with the client; I handle any tech questions

---

## Job Screening Workflow (Framer → GoFullPage → Code)

**How you can use design platforms safely:**

1. **Framer design** (you do this)
   - You design in Framer (or use Framer template)
   - You do the UX/design work (user research, wireframes, prototypes)

2. **GoFullPage screenshot** (you do this)
   - Screenshot the Framer design
   - Export as image/PDF

3. **I code it** (I do this)
   - I build HTML/CSS/JavaScript from your screenshots
   - You don't use Framer directly; you just see the output

**This works because:** You do the design work, I do the code work. No platform restrictions violated.

---

## Jobs to TARGET (Portfolio-Building Phase)

✅ **APPLY to these:**
- "Build a React landing page" ($50-200, 2-5 days)
- "Fix this bug in my React app" ($30-75, 1-2 days)
- "Create a contact form with validation" ($40-100, 1-2 days)
- "Build a Node.js REST API endpoint" ($75-150, 2-3 days)
- "Polish existing website" (clear scope, 1-2 weeks, $100-300)
- "Convert this design to HTML/CSS" ($75-250, 3-5 days)
- "Android UI fix" ($30-75, 1-2 days)
- "Build a simple React app" ($100-300, 5-7 days)

---

## Jobs to SKIP (Even if price is high)

❌ **SKIP these (regardless of budget):**
- "Must be built in Webflow" / "Must use FlutterFlow" / "Must use Leadpages" (platform blocker)
- "Figma design required" / "Adobe Illustrator expertise" (design tools blocked)
- "Full website redesign" without scope details (vague = scope creep)
- "Ongoing project" with undefined duration (lock-in risk)
- "Build me an app" with no details (too vague)
- "UI/UX Designer needed" without your having design portfolio (design work, not coding)
- "Show examples of [platform] work you've done" when you don't have them (instant rejection)

---

## Red Flags to Watch For

🚨 **MAJOR (Skip immediately):**
- Budget is extremely low ($50 for "full website") + scope is large
- "Must have published apps" or "Must have [platform] experience" when you don't
- Vague scope + "ongoing" = unclear commitment
- "Visual platform required" (Webflow, FlutterFlow, Framer itself as builder, etc.)
- Design work required ("Create wireframes," "Design UI," "Show design portfolio")

⚠️ **MEDIUM (Proceed with caution):**
- Budget seems low but scope is clear (negotiate or skip)
- 10+ proposals already (high competition, low win rate)
- "Ongoing project" without defined duration (ask client for specifics)
- Client inactive (last viewed "2 weeks ago" = slow responder)
- Platform recommendation required ("What platform would you use?")

---

## Screening Decision Tree

**For every job, ask:**

1. **Can I do this work?** (Code-based? No design tools? No iOS testing?)
   - NO → Skip
   - YES → Continue

2. **Is scope clear?** (Can I define exactly what "done" looks like?)
   - NO (vague scope) → Skip
   - YES → Continue

3. **Will I likely get a 5-star review?** (Straightforward work? Happy client likely?)
   - NO (high complexity, risky, low trust) → Skip
   - YES → Apply

4. **Is budget realistic for the work?** (Even if low, not poverty wages?)
   - NO (under $20-30 for major scope) → Skip
   - YES → Apply

---

## When to Apply (Even at Low Prices)

✅ **Apply if:**
- Clear, specific scope ("5-page website, contact form, responsive")
- You're confident you can deliver ("polish existing code" not "redesign from scratch")
- Fast timeline (1-2 weeks max = quick 5-star wins)
- 5-star probability is high (straightforward deliverable)
- Client is active (viewed recently, responsive)

❌ **Don't apply if:**
- Scope is vague ("full website", "make it better", "improve it")
- Duration is unclear ("ongoing", no end date)
- Work requires expertise you don't have (design, marketing, iOS testing)
- Client has red flags (inactive, 50+ proposals, platform-specific requirements)

### Cleanup after a job is delivered
Once a job is finished and the milestone is paid, clean up disk space but keep what the client might still ask for.

**Delete (regenerable / obsolete):**
- Temporary standalone build copies made to work around Windows path limits (e.g. C:\m) — these can be huge (10+ GB of node_modules, gradle caches, android build output). The real fix lives in the git repo and CI rebuilds the APK on demand.
- Obsolete one-off scratch files (e.g. support-message drafts for issues already resolved).

**Always keep (in the job folder under C:\UpWork\):**
- The job's PROPOSAL.md
- The final deliverable(s) — e.g. the working APK (save a clearly-named copy like app-release-WORKING.apk before deleting the build folder)
- A short FIX-NOTES.md documenting the problem, root cause(s), exact files/commits changed, and how to rebuild — so the fix can be explained or redone if the client comes back.
- The actual git repo clone (e.g. C:\el) if it has committed, pushed work — it holds the history and is the source of truth.

Always copy/save the keepers FIRST, verify they're saved, then delete the bulk. Deletion is destructive — never delete the git repo or a deliverable that hasn't been backed up.

### Pricing Guide — Exact Rates by Job Type

| Job Type | Rate | Notes |
|---|---|---|
| **Bug fix / small feature** | $25-30/hr | 1-5 hours, straightforward work |
| **React Native / Flutter app** | $35-45/hr | Full app or major feature, API integration |
| **Web app (React/Next.js)** | $30-40/hr | Moderate complexity, includes design/UX |
| **Backend (Node.js/Python)** | $35-50/hr | API design, database work, integrations |
| **Full-stack project** | $40-55/hr | Combined front + back, deployment |
| **Android XML layouts** | $20-25/hr | Layout fixes, constraint work, styling |
| **DevOps / CI-CD** | $50-75/hr | GitHub Actions, Docker, deployment pipelines |
| **Proposal writing / analysis** | $15-20/hr | Job analysis, cover letters, scope planning |

**Pricing rules:**
- Never quote below the minimum for the job type — you're worth it
- Estimate conservatively (add 20% buffer for unknowns), then quote the middle or high-end number
- If client says "this is all I can afford" and it's below the minimum, **decline politely** or propose a smaller scope
- For fixed-price: quote (estimated hours × rate × 1.2) to account for unknowns

### Proposal Format Rules
- Every proposal gets its own folder in C:\UpWork\
- All text must be ready to copy-paste — no editing required
- Keep cover letters short — 4 to 6 lines max
- PROPOSAL.md always includes: cover letter + terms
- Always paste the full cover letter and terms directly in the chat response too (not just in the file), so it can be copied immediately
- **For pricing: use the exact rates from the Pricing Guide above. Quote a specific number, not a range.** Example: "I quote $35/hour for this work" instead of "$30-40/hour"
- Only include these sections IF they apply to the job:

| Section | Include when |
|---|---|
| **Terms** (payment type, amount, duration, boost) | Always |
| **Cover Letter** | Always |
| **Screening Question Answers** | Only if the job has screening questions |
| **Attachments** | Only if you have relevant work samples to share |
| **Profile Highlights** | Only if the client's job is in a category matching your portfolio projects |
| **After Hired** | Only if I need files from the client to do the work |

---

## Proposal Writing Rules

### The First Two Lines Rule
When a client looks at their dashboard, they only see the first two sentences of your cover letter. Those two lines decide whether they click or skip.

**Never start with:**
- "Hi, my name is X and I have 3 years of experience..."
- "I am a skilled developer who..."
- "I read your job post carefully..."

**Always start with the solution:**
- "I can implement that Show/Hide password toggle today using SecureTextEntry and a basic state variable."
- "I know exactly what's causing your APK crash — it's almost always a missing release signing config in Gradle."
- "I can fix the Stock badge overflow in your Android layout by adding a minWidth constraint to the TextView."

The first two sentences must prove you understand the problem and can solve it — before they even click to read more.

### Don't Give Away the Solution
Show that you know the answer — but don't explain it in detail. If you spell out the exact fix, the client can just do it themselves and won't need to hire you.

**Wrong:** "Your build fails because EAS detects pnpm from pnpm-workspace.yaml — fix it by adding packageManager to your package.json."
**Right:** "I've fixed this exact EAS + pnpm monorepo conflict before. Share your eas.json and I'll have it working within the hour."

Prove you know it. Don't teach it.

---

## Working Style Rules

### Always give client reply text
Whenever a client (on Upwork, Discord, etc.) sends a message that needs a response, always draft a ready-to-copy-paste reply for the user — don't wait to be asked "what should I reply".

### Just do simple tasks
For straightforward, low-risk tasks (installing tools, cloning repos, running builds, writing code, drafting messages), just do them directly without asking for permission first. Only pause to ask if the action is destructive, costs money, or is ambiguous.

### Recommend model/effort for harder tasks
If a task is complex (large codebase work, big refactors, long builds/debug sessions), recommend which Claude Code model and effort/thinking level to use (e.g. "this is complex enough to use Opus" or "use high effort/thinking mode for this") instead of just proceeding on default settings.

### Don't re-read previous messages unless told to
Context is expensive. Only read old messages if the user explicitly asks you to ("read the previous...", "check what I said about...", "look back at..."), or if you're resuming from a context break and the summary doesn't have the info you need. Otherwise, assume you already have the context you need from the active conversation. This keeps token usage lean.

### The user is Muslim — keep it halal and respectful
- The user is a Muslim. Keep all work, advice, and drafted messages aligned with Islamic values: be honest, no deception, no interest (riba), no haram content, no scams.
- When drafting replies to clients who are also Muslim, a warm, respectful tone is welcome — light, natural use of phrases like "Alhamdulillah" or "Insha'Allah" is fine when the client uses them first or the context fits. Don't force it.
- Always be honest with clients about what was and wasn't done — never overstate a fix or claim something works if it hasn't been verified.
- Be kind, patient, and encouraging in all drafted messages and in working with the user.

---

## Upwork Profile Setup (Completed 2026-06-14)

**Profile Structure & Learnings:**
- **Headline:** "Landing Page & E-commerce Builder | React | Node.js | Payment Integration" (specific WHAT you build, then HOW)
- **About Me:** 350-400 words max (NOT 700+). People skim, don't read full long profiles. Keep it punchy.
- **Content flow:** Opening → What I do → Proof (portfolio) → Reliability promise (response time, delivery) → Call to action
- **Response time:** 12 hours (not 24, more competitive)
- **Delivery promises:** Website builds (2–5 days) | Bug fixes (same-day to 2 days) | Payment integration (2–4 days) | Mobile apps (1–2 weeks)
- **Revisions:** Two rounds included, major scope changes billed separately
- **Communication:** Daily status updates (no ghosting)

**Portfolio Setup:**
- Keep 3 separate portfolio items (NOT combined) — better visibility, appears in more searches
- Format: Title → My role → Description (with ✅ checkmarks) → Skills & deliverables → Live link

**Portfolio Item Skills (Final):**

1. **Quillo (SaaS Landing Page):**
   - Landing Page Design, Web Design, UI/UX Design, HTML5, CSS3, JavaScript, Animation, Responsive Design

2. **Meridian (Creative Agency Portfolio):**
   - Web Design, UI/UX Design, HTML5, CSS3, JavaScript, Animation, Responsive Design, Portfolio Design (or closest match)

3. **Lumira (E-commerce Store):**
   - Ecommerce Website Development, HTML5, CSS3, JavaScript, Responsive Design, UI/UX Design

Note: Use exact skill names from Upwork dropdown. If skill doesn't exist, choose closest match.

**First Application:**
- Applied to Netlify/Node.js developer job ($100, 2-3 hours, posted 6 min ago, <5 proposals)
- Proposal: "I can have this done in 2 hours. I've deployed 50+ Node.js projects to Netlify. Install 2 files, verify build, test endpoints, deploy. Live in 2 hours, test link before payment. Starting now."

**Key Wins:**
- Profile live and searchable
- 3 portfolio items added
- First job applied
- Ready to apply to 2-3 more jobs in next 24 hours

---

## Current Job Status (as of 2026-06-14)

### Active Proposals
1. **React Native Live-Streaming & Short-Video App**
   - Client: Professional startup building live-streaming platform
   - Budget: $50/hr, 40 hrs/week, estimated 22–24 weeks
   - Total: ~$44–48K
   - Status: ✅ Proposal submitted (21 connects spent), waiting for response
   - Proposal includes: Auth, short-video feed, live streaming (Mux), IAP (StoreKit 2 + Google Play Billing), App Store/Play submission
   - Design: Client provides specs, or you propose standard mobile-first design
   - Timeline: 22–24 weeks conservative estimate. Realistically 12–16 weeks with no scope creep, could be 4–8 weeks aggressive coding
   - Next: Wait 3–5 days for client response; if no response, send friendly follow-up

2. **React Native Refer & Earn + Payment Gateway Integration**
   - Client: Mobile app company with existing app
   - Budget: $1,800 fixed-price (proposed, client's original: $110)
   - Status: ✅ Proposal submitted (1 connect spent), waiting for response
   - Scope: Refer & Earn module, Razorpay integration, PhonePe integration, 4-5 UI screens, testing, App Store/Play Store deployment
   - Timeline: 8 days standard, 6 days if Day 1 assets provided immediately
   - Payment: 2 milestones - $300 upfront (June 12), $1,500 on delivery (June 19)
   - Next: Wait for client counter-offer or acceptance

### Connect Status
- Current: 14 connects left (after Refer & Earn proposal)
- Strategy: Looking at new portfolio-building opportunities

### Portfolio Websites (Web Design Showcase)
**3 live design portfolio pieces deployed to Netlify:**
1. **Quillo – AI Writing Assistant** (https://quillo-ai-writer.netlify.app/)
   - SaaS landing page with animated gradient hero, interactive FAQ accordion, product showcase
   - Shimmer loading effects, scroll-reveal animations
   - ~11,000 lines of HTML/CSS/JavaScript
   - Showcases: UI Design, Animation, Web Design, JavaScript, CSS3, HTML5

2. **Meridian Studio – Creative Agency** (https://meridian-creative.netlify.app/)
   - Minimal creative portfolio with auto-scrolling logo marquee, smooth animations
   - Interactive FAQ with animated icons, gradient text effects
   - ~6,136px scrollable page with glass-morphism styling
   - Showcases: Web Design, Animation, UI Design, CSS3, HTML5

3. **Lumira – Fashion E-commerce** (https://lumira-apparel.netlify.app/)
   - Interactive e-commerce interface with working product filters (category, size, availability)
   - Dynamic banner updates, shopping cart with toast notifications
   - Animated gradient hero, responsive grid layout
   - ~40KB index.html with product filtering logic and state management
   - Showcases: Web Design, Responsive Design, JavaScript, CSS3, HTML5

**Portfolio strategy for these:** These are front-end design showcase pieces (not full business apps). They demonstrate:
- Modern UI/UX capabilities
- Responsive design (mobile 560px, tablet 980px, desktop)
- Interactive features without backend
- Animation and visual design
- Code quality and performance
- Ready to show to clients interested in web design or frontend development

**Repository:** Hosted on GitHub in `Websites Portfolio` folder for version control and easy deployment

---

### Building Reputation Strategy
🎯 **PRIMARY GOAL: Build portfolio NOW → Higher prices LATER**

**Current status: Only 1 client in history. Need 5–10 completed jobs with 5-star reviews FIRST.**

- **Why:** Strong portfolio = can charge 2-3x more in 6-12 months. Clients hire based on proven track record, not just skills.
- **Pricing mindset:** Money is NOT the priority right now. A 5-star review from a verified client is worth more than holding out for $500. Since Claude does all the work, even $25–50 USD = real portfolio win + real Rupiah value.
- **New rule — Easy tasks, any price:** If a job is genuinely easy (UI tweaks, simple bug fixes, basic features that take <1 day), **accept ANY price** to get the 5-star review fast. Example: $20 Flutter UI updates = 1 day work = 5-star review = portfolio win.
- **Current Strategy (Portfolio-First):** Accept ANY job that is:
  - ✅ Clear scope (no vague "build me an app")
  - ✅ Honest deliverables (nothing I can't deliver)
  - ✅ Halal work
  - ✅ Code-based (my skills)
  - ✅ Fast turnaround (1–14 days preferred)
  - ✅ High 5-star review probability (straightforward = happy client)
  - ⚠️ Price is FLEXIBLE (no hard minimums, focus on portfolio diversity)
- **Portfolio-phase pricing (ACCEPT NOW):**
  - Quick bug fix (1–3 days): ANY PRICE if scope is clear
  - Feature addition (3–5 days): $30–200
  - Small app / prototype (5–10 days): $50–500
  - Medium project (10–20 days): $100–1,000
  - **Philosophy:** Money is for later. 5-star reviews + diverse portfolio = 2-3x higher rates in 2-3 months
- **Context:** User is Indonesian. $25–50 USD = Rp400K–800K = significant local value + portfolio investment
- **After 5–10 completed jobs:** Raise rates to market rate ($35–50/hr)
- **Approach:**
  - Complete visible projects quickly (8-30 day turnarounds)
  - Get 5-star reviews and testimonials
  - Build case studies showing Android, React Native, backend, integrations
  - Show consistent, professional delivery
- **Selection criteria (PORTFOLIO-FIRST):** Only code-based work, with preference for:
  - **Clear scope & realistic timeline** (VAGUE SCOPE = NO; clear scope with low price = YES)
  - **Fast turnaround** (1-14 days preferred) for rapid portfolio wins
  - **Different tech stacks** (Android, React Native, Node.js, Python, etc.) to show breadth
  - **No tech blockers** (iOS testing, FlutterFlow visual builder, Figma design) = Can't deliver = Bad portfolio
  - **High 5-star probability** (straightforward projects beat ambitious projects)
  - **Avoid long hourly contracts** (6+ months locks you in, prevents portfolio diversity)
  - **ACCEPT low prices** if scope is clear and doable (portfolio > hourly rate at this stage)
- **Testing capability:** Can connect Android phone via USB only (adb testing + scrcpy mirroring). Can use scrcpy to mirror phone screen, interact with UI, watch videos, test apps in real-time
- **Quality bar:** ALL work must be production-ready and honest with clients (no overstating, no shortcuts). Reputation > quick money. A bad 5-star review is worse than no portfolio.
- **Long-term payoff:** 10-15 solid projects with 5-star reviews in 2-3 months → charge $50-75/hr instead of $25-35/hr → earn 2-3x more per project

### Jobs to Skip / Rejected
**Don't skip based on price.** Skip only if:
- ❌ **Vague scope** (can't define deliverables clearly)
- ❌ **Tech blockers** (iOS testing, Figma design, FlutterFlow, visual no-code platforms)
- ❌ **Impossible deadlines** (can't deliver = bad review)
- ❌ **Dishonest/haram work** (scams, misrepresentation, unethical)
- ❌ **Long hourly contracts** (6+ months locks you in)
- ❌ **Zero review probability** (ambiguous scope = unhappy client = bad review)

**Examples of what to SKIP:**
- Dating app with vague requirements → Unclear scope, scope creep risk
- iOS-specific React Native fix → Can't test iOS (no macOS)
- "Build me an AI app" (no details) → Vague = bad review
- 3-day turnaround for 80-hour project → Can't deliver = bad review
- WordPress visual builder work → Can't do (visual platform)

**Examples of what to ACCEPT (regardless of price):**
- ✅ "Build a simple landing page" ($20, 1 day) → Clear scope, easy win
- ✅ "Fix this bug in my React app" ($30, 2 hours) → Straightforward
- ✅ "Build a to-do app with React" ($50, 1 week) → Clear, doable
- ✅ "Create a Node.js API endpoint" ($40, 3 days) → Standard work

### Pricing Strategy
**PRIMARY STRATEGY: Portfolio-First (Experience Over Earnings)**

🎯 **Core Principle:** Since Claude does 100% of the technical work, pricing is SECONDARY to portfolio building. Focus on accumulating 5-star reviews and diverse project examples.

**Pricing Guidelines (in order of priority):**
1. **Clear scope + doable work** — ACCEPT (regardless of price)
2. **Fast turnaround** (1-7 days) — ACCEPT (quick portfolio wins)
3. **Different tech stack** — PRIORITIZE (build breadth: React, React Native, Node, Python, etc.)
4. **5-star review probability** — ACCEPT (straightforward projects = happy clients = reviews)
5. **Price** — FLEXIBLE (as long as it's not obviously poverty wages like $5)

**Acceptable Price Ranges (for portfolio building):**
- Micro tasks (1-3 hours): $10–50
- Small features (4-8 hours): $30–150
- Medium projects (1-2 weeks): $100–500
- Larger projects (2-4 weeks): $300–1500

**NOT acceptable:**
- ❌ Extremely vague scope (scope creep = bad reviews)
- ❌ Impossible deadlines (can't deliver = bad reviews)
- ❌ Client expects you to do the work (but you do all work through me)
- ❌ Inherently dishonest work (no scams, misrepresentation, haram)

**Strategy:**
- Quote fast, accept quickly
- Deliver early/on-time (beats expectations)
- Get 5-star review + testimonial
- Build next job offer based on previous success
- After 5-10 completed jobs with strong reviews: RAISE RATES to $50-75/hr

**Portfolio Building:** Willing to accept lower rates than typical to build reputation, as long as:
- Work is code-based and clear-scoped
- Price is competitive vs. average bid (not poverty wages)
- Fast turnaround (8–30 days) for quick portfolio wins

### Key Decisions Made Today
1. ✅ Apply to React Native $44K job (strong opportunity, professional client, clear scope)
2. ✅ Cancel/skip low-budget jobs ($200–$600 range) — waste of time and connects
3. ✅ Kept Freelancer Plus membership ($9.99/month for 100 connects/month) — worth the investment
4. ✅ Use Sonnet or Opus for production development (not Haiku) — better reasoning for complex work
5. ✅ Quote conservatively (22–24 weeks) so finishing early looks amazing
6. ✅ Don't renegotiate price after applying — lock in $50/hr, negotiate rate increases after milestone success

### How to Handle App Store Submission
- You code the full app
- Client tests on real devices (or you test on their device via ADB if using Android)
- You prepare App Store/Play Store submission (screenshots, metadata, privacy policy)
- Client (or you guide them) submits to App Store / Google Play
- First rejection is common — you provide fixes, client resubmits
- Budget 1–2 weeks for review cycles

### Next Steps When New Session Starts
1. Check if React Native client has responded (should check Upwork messages)
2. If yes → Start development (use Sonnet as main model, Opus for hard problems)
3. If no after 3 days → Buy connects, apply to other quality jobs
4. Focus on 40 hrs/week, daily commits, weekly Monday progress reports
5. Build reputation through this contract (22+ weeks of visible, steady work)
